package za.co.varsitycollege.serversamurai.flexforce.ui.fragments.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import za.co.varsitycollege.serversamurai.flexforce.R
import za.co.varsitycollege.serversamurai.flexforce.database.AppDatabase
import za.co.varsitycollege.serversamurai.flexforce.data.models.UserEntity
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ProfileScreenFragment : Fragment() {
    private lateinit var database: AppDatabase
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var editTextName: EditText
    private lateinit var editTextNickname: EditText
    private lateinit var editTextSurname: EditText
    private lateinit var profileImageView: ImageView
    private lateinit var saveButton: Button
    private lateinit var logoutButton: Button
    private lateinit var backHomeButton: ImageButton

    private val IMAGE_FILE_NAME = "profile_image.jpg"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_screen_view, container, false)

        database = AppDatabase.getDatabase(requireContext())
        sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)

        initializeViews(view)
        setupClickListeners()
        setupLanguageSelection(view)
        loadUserProfile()
        loadImageFromStorage()

        return view
    }

    private fun initializeViews(view: View) {
        editTextName = view.findViewById(R.id.editText_name)
        editTextNickname = view.findViewById(R.id.editText_nickname)
        editTextSurname = view.findViewById(R.id.editText_lastname)
        profileImageView = view.findViewById(R.id.changeProfilePic)
        saveButton = view.findViewById(R.id.btn_save)
        logoutButton = view.findViewById(R.id.btn_logOut)
        backHomeButton = view.findViewById(R.id.profileBackHomeBtn)
    }

    private fun setupClickListeners() {
        backHomeButton.setOnClickListener {
            findNavController().navigateUp()
        }

        profileImageView.setOnClickListener {
            selectImageFromGallery()
        }

        saveButton.setOnClickListener {
            saveUserProfile()
        }

        logoutButton.setOnClickListener {
            logOut()
        }
    }

    private fun loadUserProfile() {
        lifecycleScope.launch(Dispatchers.IO) {
            val email = sharedPreferences.getString("USER_EMAIL", "") ?: ""
            val password = sharedPreferences.getString("USER_PASSWORD", "") ?: ""
            val user = database.userDao().getUser(email, password)

            withContext(Dispatchers.Main) {
                user?.let {
                    editTextName.setText(it.name)
                    editTextNickname.setText(it.nickname)
                    editTextSurname.setText(it.surname)
                }
            }
        }
    }

    private fun saveUserProfile() {
        val name = editTextName.text.toString()
        val nickname = editTextNickname.text.toString()
        val surname = editTextSurname.text.toString()

        if (name.isBlank() || surname.isBlank()) {
            Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val email = sharedPreferences.getString("USER_EMAIL", "") ?: ""
            val password = sharedPreferences.getString("USER_PASSWORD", "") ?: ""
            val currentUser = database.userDao().getUser(email, password)

            currentUser?.let { user ->
                val updatedUser = user.copy(
                    name = name,
                    nickname = nickname,
                    surname = surname
                )
                database.userDao().insert(updatedUser)

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupLanguageSelection(view: View) {
        val languageSpinner: Spinner = view.findViewById(R.id.language_spinner)
        val languages = listOf("English", "Afrikaans", "Zulu")
        val languageCodes = listOf("en", "af", "zu")

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = adapter

        val currentLanguage = sharedPreferences.getString("language", "en") ?: "en"
        languageSpinner.setSelection(languageCodes.indexOf(currentLanguage))

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

        val intent = requireActivity().intent
        requireActivity().finish()
        startActivity(intent)
    }

    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        launcher.launch(intent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val imageUri = result.data!!.data
            imageUri?.let {
                val bitmap = getBitmapFromUri(it)
                bitmap?.let {
                    profileImageView.setImageBitmap(it)
                    saveImageToStorage(it)
                }
            }
        }
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uri)
        return BitmapFactory.decodeStream(inputStream)
    }

    private fun saveImageToStorage(bitmap: Bitmap) {
        try {
            val fileOutputStream: FileOutputStream = requireContext().openFileOutput(IMAGE_FILE_NAME, Context.MODE_PRIVATE)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.close()
            Toast.makeText(context, "Profile picture saved locally", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to save profile picture", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadImageFromStorage() {
        try {
            val file = File(requireContext().filesDir, IMAGE_FILE_NAME)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                profileImageView.setImageBitmap(bitmap)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    private fun logOut() {
        // Clear all SharedPreferences
        sharedPreferences.edit().clear().apply()

        // Get the main NavController from the Activity
        val mainNavController = requireActivity().findNavController(R.id.nav_host_fragment)
        // Navigate to the welcome screen in the main navigation graph
        mainNavController.navigate(R.id.welcomeFragment)
    }
}