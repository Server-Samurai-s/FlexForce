package za.co.varsitycollege.serversamurai.flexforce

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class ProfileScreenView : Fragment() {
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

        // Setup language selection
        setupLanguageSelection(view)

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

    private fun setupLanguageSelection(view: View) {
        // Get the language selection spinner from the layout
        val languageSpinner: Spinner = view.findViewById(R.id.language_spinner)
        // Define the list of languages and their corresponding language codes
        val languages = listOf("English", "Afrikaans", "Zulu")
        val languageCodes = listOf("en", "af", "zu")
        // Set up the adapter to display the languages in the spinner
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = adapter

        val currentLanguage = sharedPreferences.getString("language", "en") ?: "en"
        languageSpinner.setSelection(languageCodes.indexOf(currentLanguage))
        // Set a listener for when an item in the spinner is selected
        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedLanguage = languageCodes[position]
                if (selectedLanguage != currentLanguage) {
                    changeLanguage(selectedLanguage)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun changeLanguage(languageCode: String) {
        sharedPreferences.edit().putString("language", languageCode).apply()

        // Restart the activity to apply the new language
        val intent = requireActivity().intent
        requireActivity().finish()
        startActivity(intent)
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