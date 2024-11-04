package za.co.varsitycollege.serversamurai.flexforce.ui.fragments.home

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import java.io.File

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val IMAGE_FILE_NAME = "profile_image.jpg"
import za.co.varsitycollege.serversamurai.flexforce.R

class homeInnerScreen : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home_inner_screen, container, false)

        // Find the ImageButton by its ID
        val profileBtn: ImageButton = view.findViewById(R.id.user_profileBtn)

        // Load profile image if available
        loadProfileImage(profileBtn)

        profileBtn.setOnClickListener {
            // Use NavController to navigate to the target fragment
            findNavController().navigate(R.id.action_homeInner_to_profile)
            Toast.makeText(context, "Profile btn clicked", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun loadProfileImage(profileButton: ImageButton) {
        // Locate the saved profile image in internal storage
        val file = File(requireContext().filesDir, IMAGE_FILE_NAME)
        if (file.exists()) {
            // Decode the image and set it to the ImageButton
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            profileButton.setImageBitmap(bitmap)
        } else {
            // Set a default image if no profile image exists
            profileButton.setImageResource(R.drawable.profilepic)
        }
    }

    companion object {

        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            homeInnerScreen().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
