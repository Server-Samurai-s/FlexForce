package za.co.varsitycollege.serversamurai.flexforce

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import za.co.varsitycollege.serversamurai.flexforce.databinding.FragmentLoginScreenBinding

private lateinit var auth: FirebaseAuth
private lateinit var binding: FragmentLoginScreenBinding

class loginScreen : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize View Binding
        binding = FragmentLoginScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the login button using View Binding
        binding.buttonLogin.setOnClickListener {
            // Placeholder for real login logic
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
        }
    }
}
