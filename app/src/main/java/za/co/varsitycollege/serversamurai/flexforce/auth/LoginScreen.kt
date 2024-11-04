package za.co.varsitycollege.serversamurai.flexforce.auth

import za.co.varsitycollege.serversamurai.flexforce.database.AppDatabase
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import za.co.varsitycollege.serversamurai.flexforce.R
import za.co.varsitycollege.serversamurai.flexforce.auth.BiometricHelper
import za.co.varsitycollege.serversamurai.flexforce.data.models.UserEntity
import za.co.varsitycollege.serversamurai.flexforce.databinding.FragmentLoginScreenBinding
import za.co.varsitycollege.serversamurai.flexforce.service.SyncManager
import za.co.varsitycollege.serversamurai.flexforce.utils.UserSecrets

class LoginScreen : Fragment(), BiometricHelper.AuthenticationCallback {
    private lateinit var binding: FragmentLoginScreenBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var database: AppDatabase
    private lateinit var biometricHelper: BiometricHelper
    private lateinit var userSecrets: UserSecrets

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginScreenBinding.inflate(inflater, container, false)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)

        // Initialize Room Database
        database = AppDatabase.getDatabase(requireContext())

        // Initialize Biometric Helper
        biometricHelper = BiometricHelper(requireActivity(), this)

        // Initialize User Secrets
        userSecrets = UserSecrets()

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up email/password sign-in
        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        // Set up biometric authentication
        binding.bioAuthBtn.setOnClickListener {
            biometricHelper.authenticate()
        }

        // Set up registration link
        binding.registerLink.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        // Set up Google Sign-In
        binding.googleSignInBtn.setOnClickListener {
            signInWithGoogle()
        }

        // Automatically authenticate if "Remember Me" is checked
        if (sharedPreferences.getBoolean("rememberMe", false)) {
            biometricHelper.authenticate()
        }
    }

    private fun loginUser(email: String, password: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val user = database.userDao().getUser(email, password)

                if (user != null) {
                    // Store user data in SharedPreferences
                    sharedPreferences.edit()
                        .putString("USER_EMAIL", email)
                        .putString("USER_PASSWORD", password)
                        .putString("USER_NAME", user.name)
                        .putString("USER_SURNAME", user.surname)
                        .putString("USER_NICKNAME", user.nickname)
                        .apply()

                    handleRememberMe(email)

                    // Sync user data after successful login
                    val syncManager = SyncManager(requireContext())
                    syncManager.syncUserData(email)

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

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken)
            } catch (e: ApiException) {
                Log.e("GoogleSignIn", "Google sign in failed", e)
                Toast.makeText(context, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Google Sign-In canceled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        if (idToken == null) {
            Toast.makeText(context, "ID Token is null", Toast.LENGTH_SHORT).show()
            return
        }
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(context, "Sign-In successful: ${user?.displayName}", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                } else {
                    Log.e("FirebaseAuth", "Authentication failed: ${task.exception?.message}")
                    Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show()
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
