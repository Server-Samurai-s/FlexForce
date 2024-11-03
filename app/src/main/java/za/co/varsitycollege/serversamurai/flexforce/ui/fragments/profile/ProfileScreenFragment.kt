package za.co.varsitycollege.serversamurai.flexforce.ui.fragments.profile

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import za.co.varsitycollege.serversamurai.flexforce.R

class ProfileScreenFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var sharedPreferences: SharedPreferences

    // Define your EditText views
    private lateinit var editTextName: EditText
    private lateinit var editTextNickname: EditText
    private lateinit var editTextSurname: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile_screen_view, container, false)

        // Find the views by their IDs
        val backBtn: ImageView = view.findViewById(R.id.backHomeBtn)
        val logOutBtn: Button = view.findViewById(R.id.btn_logOut)

        // Initialize EditTexts
        editTextName = view.findViewById(R.id.editText_name)
        editTextNickname = view.findViewById(R.id.editText_nickname)
        editTextSurname = view.findViewById(R.id.editText_lastname)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val mainNavHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val mainNavController = mainNavHostFragment.navController

        sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)

        // Fetch user details from Firestore
        fetchUserProfile()

        // Handle back button click
        backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        // Handle log out button click
        logOutBtn.setOnClickListener {
            logOut()
            mainNavController.navigate(R.id.welcomeFragment)
        }

        return view
    }

    private fun fetchUserProfile() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            // Access the 'userDetails' sub-collection for the current user
            firestore.collection("users")
                .document(userId)
                .collection("userDetails")
                .document("details")
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Populate the UI with Firestore data
                        editTextName.setText(document.getString("name"))
                        editTextNickname.setText(document.getString("nickname"))
                        editTextSurname.setText(document.getString("surname"))
                    } else {
                        Toast.makeText(context, "User profile not found.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "Error fetching profile data", exception)
                    Toast.makeText(context, "Error fetching profile data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun logOut() {
        auth.signOut()
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}