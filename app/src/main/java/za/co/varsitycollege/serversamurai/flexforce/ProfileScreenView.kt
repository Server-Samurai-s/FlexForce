package za.co.varsitycollege.serversamurai.flexforce

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import za.co.varsitycollege.serversamurai.flexforce.R
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ProfileScreenView : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var editTextName: EditText
    private lateinit var editTextNickname: EditText
    private lateinit var editTextSurname: EditText
    private lateinit var profileImageView: ImageView

    private val IMAGE_FILE_NAME = "profile_image.jpg"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_screen_view, container, false)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)

        editTextName = view.findViewById(R.id.editText_name)
        editTextNickname = view.findViewById(R.id.editText_nickname)
        editTextSurname = view.findViewById(R.id.editText_lastname)
        profileImageView = view.findViewById(R.id.changeProfilePic)

        view.findViewById<ImageView>(R.id.backHomeBtn).setOnClickListener {
            findNavController().popBackStack()
        }
        view.findViewById<Button>(R.id.btn_logOut).setOnClickListener {
            logOut()
            val mainNavHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            mainNavHostFragment.navController.navigate(R.id.welcomeFragment)
        }

        profileImageView.setOnClickListener {
            selectImageFromGallery()
        }

        fetchUserProfile()
        loadImageFromStorage() // Load the profile image from internal storage
        return view
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

    private fun fetchUserProfile() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users")
                .document(userId)
                .collection("userDetails")
                .document("details")
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        editTextName.setText(document.getString("name"))
                        editTextNickname.setText(document.getString("nickname"))
                        editTextSurname.setText(document.getString("surname"))
                    } else {
                        Toast.makeText(context, "User profile not found.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error fetching profile data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun logOut() {
        auth.signOut()
        sharedPreferences.edit().clear().apply()
    }
}