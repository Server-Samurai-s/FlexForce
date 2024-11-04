package za.co.varsitycollege.serversamurai.flexforce.ui.fragments.challenge

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import za.co.varsitycollege.serversamurai.flexforce.R
import java.io.File

class ChallengeScreen : Fragment() {
    private val IMAGE_FILE_NAME = "profile_image.jpg"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_challenge_screen, container, false)

        val challengeBtn1: Button = view.findViewById(R.id.challenge_1btn)
        val challengeBtn2: Button = view.findViewById(R.id.challenge_2btn)
        val challengeBtn3: Button = view.findViewById(R.id.challenge_3btn)

        challengeBtn1.setOnClickListener {
            navigateWithPayload("Challenge 1", R.id.action_nav_challenge_to_nav_challenge_view)
        }
        challengeBtn2.setOnClickListener {
            navigateWithPayload("Challenge 2", R.id.action_nav_challenge_to_nav_challenge_view)
        }
        challengeBtn3.setOnClickListener {
            navigateWithPayload("Challenge 3", R.id.action_nav_challenge_to_nav_challenge_view)
        }

        val profileBtn: ImageButton = view.findViewById(R.id.user_profileBtn)

        loadProfileImage(profileBtn)

        profileBtn.setOnClickListener {
            findNavController().navigate(R.id.action_nav_challenge_to_nav_profile)
            Toast.makeText(context, "Profile btn clicked", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun loadProfileImage(profileBtn: ImageButton) {
        val file = File(requireContext().filesDir, IMAGE_FILE_NAME)
        if (file.exists()) {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            profileBtn.setImageBitmap(bitmap)
        } else {
            profileBtn.setImageResource(R.drawable.profilepic)
        }
    }

    private fun navigateWithPayload(challengeId: String, actionId: Int) {
        val bundle = Bundle()
        bundle.putString("challengeId", challengeId)
        findNavController().navigate(actionId, bundle)
        Toast.makeText(context, "$challengeId button clicked", Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChallengeScreen().apply {
                arguments = Bundle().apply {
                    putString("param1", param1)
                    putString("param2", param2)
                }
            }
    }
}