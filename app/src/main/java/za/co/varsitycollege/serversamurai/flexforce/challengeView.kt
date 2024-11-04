package za.co.varsitycollege.serversamurai.flexforce

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import za.co.varsitycollege.serversamurai.flexforce.Models.ApiDataModels
import za.co.varsitycollege.serversamurai.flexforce.service.ApiClient
import za.co.varsitycollege.serversamurai.flexforce.service.UpdateChallengeStatusRequest
import za.co.varsitycollege.serversamurai.flexforce.service.ChallengeStatusResponse

class challengeView : Fragment() {
    private lateinit var tvChallengeType: TextView
    private lateinit var tvTimePeriod: TextView
    private lateinit var tvTracking: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvChallengeProgress: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_challenge_view, container, false)

        initializeTextViews(view)
        val challengeId = arguments?.getString("challengeId") ?: "defaultChallengeId"

        val startChallengeBtn: Button = view.findViewById(R.id.buttonStartChallenge)
        val backBtn: ImageButton = view.findViewById(R.id.backBtnC)
        val NextPage : Button = view.findViewById(R.id.NextPageBtn)

        startChallengeBtn.setOnClickListener {
            startChallenge(challengeId)
            //findNavController().navigate(R.id.action_nav_challenge_view_to_nav_challenge_view_inner)
        }

        NextPage.setOnClickListener {
            navigateWithPayload(challengeId, R.id.action_nav_challenge_view_to_nav_challenge_view_inner)
        }


        backBtn.setOnClickListener {
            findNavController().navigate(R.id.action_nav_challenge_view_to_nav_challenge)
            Toast.makeText(context, "Back btn clicked", Toast.LENGTH_SHORT).show()
        }

        fetchChallengeData(challengeId)
        fetchChallengeStatus(challengeId) // Fetch the challenge status on view creation
        return view
    }

    private fun initializeTextViews(view: View) {
        tvChallengeType = view.findViewById(R.id.challengeTypeTxt)
        tvTimePeriod = view.findViewById(R.id.timePeriodTxt)
        tvTracking = view.findViewById(R.id.trackingWithTxt)
        tvDescription = view.findViewById(R.id.descriptionTxt)
        tvChallengeProgress = view.findViewById(R.id.ChallengeProgress) // Initialize the ChallengeProgress TextView
    }

    private fun fetchChallengeData(challengeId: String) {
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
                    Toast.makeText(context, "Failed to retrieve challenge details", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiDataModels.Challenge>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateWithPayload(challengeId: String, actionId: Int) {
        val bundle = Bundle()
        bundle.putString("challengeId", challengeId)
        findNavController().navigate(actionId, bundle)
        Toast.makeText(context, "$challengeId button clicked", Toast.LENGTH_LONG).show()
    }

    private fun fetchChallengeStatus(challengeId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        ApiClient.retrofitService.getUserChallengeStatus(userId, challengeId).enqueue(object : Callback<ChallengeStatusResponse> {
            override fun onResponse(call: Call<ChallengeStatusResponse>, response: Response<ChallengeStatusResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        tvChallengeProgress.text = "Status: ${it.status}" // Set the challenge status in the TextView
                    }
                } else {
                    tvChallengeProgress.text = "Status: Not Started"
                    Toast.makeText(context, "Failed to retrieve challenge status", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ChallengeStatusResponse>, t: Throwable) {
                tvChallengeProgress.text = "Status: Not Started"
                Toast.makeText(context, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun startChallenge(challengeId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val updateRequest = UpdateChallengeStatusRequest(challengeId = challengeId, status = "started")

        ApiClient.retrofitService.updateUserChallengeStatus(userId, updateRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Challenge started successfully", Toast.LENGTH_SHORT).show()
                    tvChallengeProgress.text = "Status: Started" // Update the status directly after starting
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
