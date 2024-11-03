package za.co.varsitycollege.serversamurai.flexforce.auth

import za.co.varsitycollege.serversamurai.flexforce.database.AppDatabase
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import za.co.varsitycollege.serversamurai.flexforce.R
import com.google.firebase.auth.GoogleAuthProvider
import za.co.varsitycollege.serversamurai.flexforce.auth.BiometricHelper
import za.co.varsitycollege.serversamurai.flexforce.databinding.FragmentLoginScreenBinding
import za.co.varsitycollege.serversamurai.flexforce.utils.UserSecrets

class loginScreen : Fragment(), BiometricHelper.AuthenticationCallback {
    private lateinit var binding: FragmentLoginScreenBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var database: AppDatabase
    private lateinit var biometricHelper: BiometricHelper
    private lateinit var userSecrets: UserSecrets
    private lateinit var googleSignOnClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginScreenBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignOnClient = GoogleSignIn.getClient(requireActivity(), gso)

        sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        database = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "flexforce-database"
        ).build()

        // Initialize Biometric Helper
        biometricHelper = BiometricHelper(requireActivity(), this)

        // Initialize UserSecrets utility for encrypted preferences
        userSecrets = UserSecrets()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (isConnected()) {
                    loginUserOnline(email, password)
                } else {
                    loginUserOffline(email, password)
                }
            } else {
                Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        binding.bioAuthBtn.setOnClickListener {
            biometricHelper.authenticate()
        }

        binding.registerLink.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.googleSignInBtn.setOnClickListener {
            signInWithGoogle()
        }

        if (sharedPreferences.getBoolean("rememberMe", false)) {
            biometricHelper.authenticate()
        }
    }

    private fun isConnected(): Boolean {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val activeNetwork = cm?.activeNetwork
        val networkCapabilities = cm?.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun loginUserOnline(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    handleRememberMe(email)
                    // Store the credentials after a successful login
                    storeCredentials(email, password)
                    sharedPreferences.edit().putBoolean("rememberMe", true).apply()
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                } else {
                    Toast.makeText(context, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun loginUserOffline(email: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val user = database.userDao().getUser(email, password)
            CoroutineScope(Dispatchers.Main).launch {
                if (user != null) {
                    handleRememberMe(email)
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                } else {
                    Toast.makeText(context, "Login failed: Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun handleRememberMe(email: String) {
        if (binding.checkboxRememberMe.isChecked) {
            val editor = sharedPreferences.edit()
            editor.putBoolean("rememberMe", true)
            editor.putString("email", email)
            editor.apply()
    private fun storeCredentials(email: String, password: String) {
        val encryptedPrefs = userSecrets.getEncryptedSharedPreferences(requireContext())
        with(encryptedPrefs.edit()) {
            putString("email", email)
            putString("password", password)
            apply()
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignOnClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { results ->
        if (results.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(results.data)
            handleResults(task)
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                updateUI(account)
            }
        } else {
            Toast.makeText(context, "Google sign in failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    Toast.makeText(context, "Google Sign-In successful", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Firebase Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onSuccess() {
        // Retrieve encrypted credentials on biometric success
        val encryptedPrefs = userSecrets.getEncryptedSharedPreferences(requireContext())
        val email = encryptedPrefs.getString("email", null)
        val password = encryptedPrefs.getString("password", null)

        if (email != null && password != null) {
            // Log in with the retrieved credentials
            loginUser(email, password)
        } else {
            Toast.makeText(context, "No saved login credentials found.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onError(error: String) {
        Toast.makeText(context, "Biometric authentication error: $error", Toast.LENGTH_SHORT).show()
    }

    override fun onFailure() {
        Toast.makeText(context, "Biometric authentication failed. Please try again.", Toast.LENGTH_SHORT).show()
    }
}
