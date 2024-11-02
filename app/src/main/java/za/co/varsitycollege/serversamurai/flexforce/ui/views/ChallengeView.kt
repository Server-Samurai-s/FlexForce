package za.co.varsitycollege.serversamurai.flexforce.ui.views

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import za.co.varsitycollege.serversamurai.flexforce.R
import za.co.varsitycollege.serversamurai.flexforce.data.models.Challenge
import za.co.varsitycollege.serversamurai.flexforce.network.ApiClient
import za.co.varsitycollege.serversamurai.flexforce.Models.ApiDataModels
import za.co.varsitycollege.serversamurai.flexforce.service.ApiClient
import za.co.varsitycollege.serversamurai.flexforce.service.UpdateChallengeStatusRequest

class challengeView : Fragment() {
    private lateinit var tvChallengeType: TextView
    private lateinit var tvTimePeriod: TextView
    private lateinit var tvTracking: TextView
    private lateinit var tvDescription: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
        Log.d("challengeView", "onCreate: started")
    arguments?.let {
        param1 = it.getString(ARG_PARAM1)
        param2 = it.getString(ARG_PARAM2)
    }
    //val challengeId = arguments?.getString("challengeId") ?: "defaultChallengeId"
    //fetchChallengeData(challengeId)
        Log.d("challengeView", "onCreate: completed with param1=$param1, param2=$param2")
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
            startChallenge(challengeId)
            findNavController().navigate(R.id.action_nav_challenge_view_to_nav_challenge_view_inner)
            Toast.makeText(context, "Start challenge btn clicked", Toast.LENGTH_SHORT).show()
        }

        backBtn.setOnClickListener {
            Log.d("challengeView", "backBtn: clicked")
            findNavController().navigate(R.id.action_nav_challenge_view_to_nav_challenge)
            Toast.makeText(context, "Back btn clicked", Toast.LENGTH_SHORT).show()
        }

        fetchChallengeData(challengeId)
        Log.d("challengeView", "onCreateView: completed")
        return view
    }

    private fun initializeTextViews(view: View) {
        Log.d("challengeView", "initializeTextViews: started")
        tvChallengeType = view.findViewById(R.id.challengeTypeTxt)
        tvTimePeriod = view.findViewById(R.id.timePeriodTxt)
        tvTracking = view.findViewById(R.id.trackingWithTxt)
        tvDescription = view.findViewById(R.id.descriptionTxt)
        Log.d("challengeView", "initializeTextViews: completed")
    }

    private fun fetchChallengeData(challengeId: String) {
        Log.d("challengeView", "fetchChallengeData: started with challengeId=$challengeId")
        ApiClient.retrofitService.getChallengeView(challengeId).enqueue(object :
            Callback<Challenge> {
            override fun onResponse(call: Call<Challenge>, response: Response<Challenge>) {
        ApiClient.retrofitService.getChallengeView(challengeId).enqueue(object : Callback<ApiDataModels.Challenge> {
            override fun onResponse(call: Call<ApiDataModels.Challenge>, response: Response<ApiDataModels.Challenge>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        tvChallengeType.text = "Type: ${it.challengeType}"
                        tvTimePeriod.text = "Period: ${it.timePeriod}"
                        tvTracking.text = "Tracking: ${it.tracking}"
                        tvDescription.text = it.description
                    }
                } else {
                    Log.e(
                        "challengeView",
                        "fetchChallengeData: failed to retrieve challenge details"
                    )
                    Toast.makeText(
                        context,
                        "Failed to retrieve challenge details",
                        Toast.LENGTH_SHORT
                    ).show()
                    Toast.makeText(context, "Failed to retrieve challenge details", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Challenge>, t: Throwable) {
                Log.e("challengeView", "fetchChallengeData: error=${t.localizedMessage}", t)
                Toast.makeText(context, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
        Log.d("challengeView", "fetchChallengeData: completed")
    }

    private fun startChallenge(challengeId: String) {
        // Get the current logged-in user's ID from Firebase
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val updateRequest = UpdateChallengeStatusRequest(challengeId = challengeId, status = "started")

    companion object {

        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            challengeView().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
        // Call the Retrofit method with userId as a path parameter
        ApiClient.retrofitService.updateUserChallengeStatus(userId, updateRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Challenge started successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to start challenge", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
