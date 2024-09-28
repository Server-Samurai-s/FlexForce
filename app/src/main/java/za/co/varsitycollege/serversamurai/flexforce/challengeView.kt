package za.co.varsitycollege.serversamurai.flexforce

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import za.co.varsitycollege.serversamurai.flexforce.Models.ApiDataModels
import za.co.varsitycollege.serversamurai.flexforce.retrofit.RetrofitClient

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class challengeView : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var tvChallengeType: TextView
    private lateinit var tvTimePeriod: TextView
    private lateinit var tvTracking: TextView

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
        val view = inflater.inflate(R.layout.fragment_challenge_view, container, false)

        initializeTextViews(view)
        val challengeId = arguments?.getString("challengeId") ?: "defaultChallengeId"

        val startChallengeBtn: Button = view.findViewById(R.id.buttonStartChallenge)
        val backBtn: ImageButton = view.findViewById(R.id.backBtnC)

        startChallengeBtn.setOnClickListener {
            findNavController().navigate(R.id.action_nav_challenge_view_to_nav_challenge_view_inner)
            Toast.makeText(context, "Start challenge btn clicked", Toast.LENGTH_SHORT).show()
        }

        backBtn.setOnClickListener {
            findNavController().navigate(R.id.action_nav_challenge_view_to_nav_challenge)
            Toast.makeText(context, "Back btn clicked", Toast.LENGTH_SHORT).show()
        }

        fetchChallengeData(challengeId)

        return view
    }

    private fun initializeTextViews(view: View) {
        tvChallengeType = view.findViewById(R.id.challengeTypeTxt)
        tvTimePeriod = view.findViewById(R.id.timePeriodTxt)
        tvTracking = view.findViewById(R.id.trackingWithTxt)
    }

    private fun fetchChallengeData(challengeId: String) {
        RetrofitClient.instance.getChallengeView(challengeId).enqueue(object : Callback<ApiDataModels.Challenge> {
            override fun onResponse(call: Call<ApiDataModels.Challenge>, response: Response<ApiDataModels.Challenge>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        tvChallengeType.text = "Type: ${it.challengeType}"
                        tvTimePeriod.text = "Period: ${it.timePeriod}"
                        tvTracking.text = "Tracking: ${it.tracking}"
                    }
                } else {
                    Toast.makeText(context, "Failed to retrieve challenge details", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiDataModels.Challenge>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
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