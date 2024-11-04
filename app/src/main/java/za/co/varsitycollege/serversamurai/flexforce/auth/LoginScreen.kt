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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import za.co.varsitycollege.serversamurai.flexforce.R
import com.google.firebase.auth.GoogleAuthProvider
import za.co.varsitycollege.serversamurai.flexforce.auth.BiometricHelper
import za.co.varsitycollege.serversamurai.flexforce.databinding.FragmentLoginScreenBinding
import za.co.varsitycollege.serversamurai.flexforce.utils.UserSecrets

class LoginScreen : Fragment(), BiometricHelper.AuthenticationCallback {
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
        database = AppDatabase.getDatabase(requireContext())

        biometricHelper = BiometricHelper(requireActivity(), this)
        userSecrets = UserSecrets()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
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

    private fun loginUser(email: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val user = database.userDao().getUser(email, password)

                if (user != null) {
                    // Store all necessary user data
                    sharedPreferences.edit()
                        .putString("USER_EMAIL", email)
                        .putString("USER_PASSWORD", password)
                        .putString("USER_NAME", user.name)
                        .putString("USER_SURNAME", user.surname)
                        .putString("USER_NICKNAME", user.nickname)
                        .apply()

                    handleRememberMe(email)

                    withContext(Dispatchers.Main) {
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Invalid credentials", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun handleRememberMe(email: String) {
        if (binding.checkboxRememberMe.isChecked) {
            sharedPreferences.edit()
                .putBoolean("rememberMe", true)
                .putString("email", email)
                .apply()
        }
    }

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
        val encryptedPrefs = userSecrets.getEncryptedSharedPreferences(requireContext())
        val email = encryptedPrefs.getString("email", null)
        val password = encryptedPrefs.getString("password", null)

        if (email != null && password != null) {
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