package za.co.varsitycollege.serversamurai.flexforce

import za.co.varsitycollege.serversamurai.flexforce.service.AppDatabase
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import za.co.varsitycollege.serversamurai.flexforce.databinding.FragmentLoginScreenBinding

class loginScreen : Fragment() {
    private lateinit var binding: FragmentLoginScreenBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var database: AppDatabase

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

        // Initialize Room Database
        database = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "flexforce-database"
        ).build()

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
                if (isConnected()) {
                    loginUserOnline(email, password)
                } else {
                    loginUserOffline(email, password)
                }
            } else {
                Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle register link click
        binding.registerLink.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
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
        // If the user checked "Remember Me"
        if (binding.checkboxRememberMe.isChecked) {
            val editor = sharedPreferences.edit()
            editor.putBoolean("rememberMe", true)
            editor.putString("email", email)  // Optionally save the email
            editor.apply()  // Commit changes
        }
    }
}