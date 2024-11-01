package za.co.varsitycollege.serversamurai.flexforce

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import za.co.varsitycollege.serversamurai.flexforce.auth.BiometricHelper
import za.co.varsitycollege.serversamurai.flexforce.databinding.FragmentLoginScreenBinding
import za.co.varsitycollege.serversamurai.flexforce.utils.UserSecrets

class loginScreen : Fragment(), BiometricHelper.AuthenticationCallback {
    private lateinit var binding: FragmentLoginScreenBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var biometricHelper: BiometricHelper
    private lateinit var userSecrets: UserSecrets

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize View Binding
        binding = FragmentLoginScreenBinding.inflate(inflater, container, false)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)

        // Initialize Biometric Helper
        biometricHelper = BiometricHelper(requireActivity(), this)

        // Initialize UserSecrets utility for encrypted preferences
        userSecrets = UserSecrets()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle login button click
        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()

            // Validate input
            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle biometric authentication button click
        binding.bioAuthBtn.setOnClickListener {
            biometricHelper.authenticate()
        }

        // Handle register link click
        binding.registerLink.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        // Automatically trigger biometric authentication if previously logged in
        if (sharedPreferences.getBoolean("rememberMe", false)) {
            biometricHelper.authenticate()
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Store the credentials after a successful login
                    storeCredentials(email, password)

                    // Optionally mark that biometric login is available
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("rememberMe", true)
                    editor.apply()

                    // Navigate to home screen
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                } else {
                    // If sign-in fails, display a message to the user.
                    Toast.makeText(context, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun storeCredentials(email: String, password: String) {
        // Store email and password securely using EncryptedSharedPreferences
        val encryptedPrefs = userSecrets.getEncryptedSharedPreferences(requireContext())
        with(encryptedPrefs.edit()) {
            putString("email", email)
            putString("password", password)
            apply()
        }
    }

    // Biometric authentication callbacks
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
