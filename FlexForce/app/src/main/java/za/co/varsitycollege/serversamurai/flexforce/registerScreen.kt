package za.co.varsitycollege.serversamurai.flexforce

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import za.co.varsitycollege.serversamurai.flexforce.databinding.FragmentRegisterScreenBinding

class registerScreen : Fragment() {
    private lateinit var binding: FragmentRegisterScreenBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize View Binding
        binding = FragmentRegisterScreenBinding.inflate(inflater, container, false)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

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
        // Step 1: Register the user
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Reload the current user to ensure authentication is fully propagated
                    auth.currentUser?.reload()?.addOnCompleteListener {
                        // Get the newly registered user's ID
                        val userId = auth.currentUser?.uid

                        if (userId != null) {
                            val userDetails = hashMapOf(
                                "name" to name,
                                "surname" to surname,
                                "nickname" to nickname
                            )

                            // Write to Firestore
                            firestore.collection("users")
                                .document(userId)
                                .collection("userDetails")
                                .document("details")
                                .set(userDetails)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "User details saved successfully", Toast.LENGTH_SHORT).show()
                                    findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(context, "Error saving user details: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(context, "User authentication failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
