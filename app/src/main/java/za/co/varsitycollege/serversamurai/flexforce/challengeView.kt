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
 * Use the [challengeView.newInstance] factory method to
 * create an instance of this fragment.
 */
class challengeView : Fragment() {
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
    val view = inflater.inflate(R.layout.fragment_challenge_view, container, false)

    // Find the ImageButton by its ID
    val startChallengeBtn: Button = view.findViewById(R.id.buttonStartChallenge)

    // Find the ImageButton by its ID
    val backBtn: ImageButton = view.findViewById(R.id.backBtnC)

    startChallengeBtn.setOnClickListener {
        // Use NavController to navigate to the target fragment
        findNavController().navigate(R.id.action_nav_challenge_view_to_nav_challenge_view_inner)

        Toast.makeText(context, "Start challenge btn clicked", Toast.LENGTH_SHORT).show()
    }

    backBtn.setOnClickListener {
        // Use NavController to navigate to the target fragment
        findNavController().navigate(R.id.action_nav_challenge_view_to_nav_challenge)

        Toast.makeText(context, "Back btn clicked", Toast.LENGTH_SHORT).show()
    }

    return view
}

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment challengeView.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            challengeView().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}