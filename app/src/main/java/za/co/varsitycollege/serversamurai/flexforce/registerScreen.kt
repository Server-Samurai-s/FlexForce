package za.co.varsitycollege.serversamurai.flexforce

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import za.co.varsitycollege.serversamurai.flexforce.databinding.FragmentRegisterScreenBinding

class registerScreen : Fragment() {
    private lateinit var binding: FragmentRegisterScreenBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize View Binding
        binding = FragmentRegisterScreenBinding.inflate(inflater, container, false)

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
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Registration successful, navigate to Home
                    // You can save additional user information (name, surname, nickname) to Firebase Database or Firestore here
                    findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(context, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
