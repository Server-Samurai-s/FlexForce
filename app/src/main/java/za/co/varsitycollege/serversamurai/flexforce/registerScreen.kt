package za.co.varsitycollege.serversamurai.flexforce

import za.co.varsitycollege.serversamurai.flexforce.service.AppDatabase
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
import za.co.varsitycollege.serversamurai.flexforce.Models.User
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
        database = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "flexforce-database"
        ).build()

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
        val user = User(
            uid = email,
            name = name,
            surname = surname,
            nickname = nickname,
            email = email,
            password = password
        )

        CoroutineScope(Dispatchers.IO).launch {
            database.userDao().insert(user)
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(
                    context,
                    "User registered locally. Will sync when online.",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
            }
        }
    }
}