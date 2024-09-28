package za.co.varsitycollege.serversamurai.flexforce

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.navigation.fragment.findNavController

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [challengeScreen.newInstance] factory method to
 * create an instance of this fragment.
 */
class challengeScreen : Fragment() {
    // TODO: Rename and change types of parameters
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

        return view
    }


    private fun navigateWithPayload(challengeId: String, actionId: Int) {
        val bundle = Bundle()
        bundle.putString("challengeId", challengeId)
        findNavController().navigate(actionId, bundle)
        Toast.makeText(context, "$challengeId button clicked", Toast.LENGTH_LONG).show()
    }




    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment challengeScreen.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            challengeScreen().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}