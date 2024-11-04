package za.co.varsitycollege.serversamurai.flexforce.auth

import android.content.Context
import za.co.varsitycollege.serversamurai.flexforce.database.AppDatabase
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
import kotlinx.coroutines.withContext
import za.co.varsitycollege.serversamurai.flexforce.data.models.UserEntity
import za.co.varsitycollege.serversamurai.flexforce.R
import za.co.varsitycollege.serversamurai.flexforce.databinding.FragmentRegisterScreenBinding

class registerScreen : Fragment() {
    private lateinit var binding: FragmentRegisterScreenBinding
    private lateinit var database: AppDatabase
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize View Binding
        binding = FragmentRegisterScreenBinding.inflate(inflater, container, false)

        // Initialize Room Database
        // Replace lines 33-36 with:
        database = AppDatabase.getDatabase(requireContext())

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle register button click
        binding.buttonRegister.setOnClickListener {
            val name = binding.editTextName.text.toString()
            val surname = binding.editTextSurname.text.toString()
            val nickname = binding.editTextNickname.text.toString()
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()

            // Validate input
            if (name.isNotEmpty() && surname.isNotEmpty() && nickname.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                registerUser(name, surname, nickname, email, password)
            } else {
                Toast.makeText(context, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.loginLink.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun registerUser(name: String, surname: String, nickname: String, email: String, password: String) {
        val userEntity = UserEntity(
            email = email,
            name = name,
            surname = surname,
            nickname = nickname,
            password = password
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                database.userDao().insert(userEntity)

                // Store credentials in SharedPreferences
                requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
                    .edit()
                    .putString("USER_EMAIL", email)
                    .putString("USER_PASSWORD", password)
                    .apply()

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "User registered successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Registration failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}