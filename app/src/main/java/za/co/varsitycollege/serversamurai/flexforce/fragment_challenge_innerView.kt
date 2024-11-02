package za.co.varsitycollege.serversamurai.flexforce

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
import za.co.varsitycollege.serversamurai.flexforce.Models.ApiDataModels
import za.co.varsitycollege.serversamurai.flexforce.service.ApiClient
import za.co.varsitycollege.serversamurai.flexforce.service.UpdateChallengeStatusRequest

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class fragment_challenge_innerView : Fragment() {
    private lateinit var tvMonday: TextView
    private lateinit var tvTuesday: TextView
    private lateinit var tvWednesday: TextView
    private lateinit var tvThursday: TextView
    private lateinit var tvFriday: TextView
    private lateinit var tvSaturday: TextView
    private lateinit var tvSunday: TextView
    private lateinit var challengeId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("FragmentChallengeInnerView", "onCreateView: started")
        val view = inflater.inflate(R.layout.fragment_challenge_inner_view, container, false)

        // Retrieve challengeId from arguments
        arguments?.let {
            challengeId = it.getString("challengeId") ?: "defaultChallengeId"
        }

        // Initialize TextViews for each day
        tvMonday = view.findViewById(R.id.mondayChallenge)
        tvTuesday = view.findViewById(R.id.tuesdayChallenge)
        tvWednesday = view.findViewById(R.id.wedChallenge)
        tvThursday = view.findViewById(R.id.thursdayChallenge)
        tvFriday = view.findViewById(R.id.fridayChallenge)
        tvSaturday = view.findViewById(R.id.saturdayChallenge)
        tvSunday = view.findViewById(R.id.sundayChallenge)
        //tvTest = view.findViewById(R.id.textView2Inner) // New TextView

        // Fetch the weekly workout plan
        fetchWeeklyWorkoutPlan()

        // Back button navigation
        val backBtnChallengeView: ImageButton = view.findViewById(R.id.backBtn)
        backBtnChallengeView.setOnClickListener {
            Log.d("FragmentChallengeInnerView", "backBtnChallengeView: clicked")
            findNavController().navigate(R.id.action_nav_challenge_view_inner_to_nav_challenge_view)
            Toast.makeText(context, "Back button clicked", Toast.LENGTH_SHORT).show()
        }

        val leaveChallengeBtn: Button = view.findViewById(R.id.buttonLeaveChallenge)

        leaveChallengeBtn.setOnClickListener {
            leaveChallenge(challengeId)
        }


        Log.d("FragmentChallengeInnerView", "onCreateView: completed")
        return view
    }

    private fun fetchWeeklyWorkoutPlan() {
        Log.d("FragmentChallengeInnerView", "fetchWeeklyWorkoutPlan: started")
        ApiClient.retrofitService.getChallengesWorkouts().enqueue(object : Callback<ApiDataModels.ChallengeResponse> {
            override fun onResponse(call: Call<ApiDataModels.ChallengeResponse>, response: Response<ApiDataModels.ChallengeResponse>) {
                if (response.isSuccessful) {
                    val challengeResponse = response.body()
                    Log.d("FragmentChallengeInnerView", "API response received: $challengeResponse")

                    activity?.runOnUiThread {
                        challengeResponse?.let {
                            tvMonday.text = it.Monday?.joinToString(separator = "\n") { exercise ->
                                "${exercise.name} - Sets: ${exercise.sets}, Reps: ${exercise.reps}"
                            } ?: "No exercises"

                            tvTuesday.text = it.Tuesday?.joinToString(separator = "\n") { exercise ->
                                "${exercise.name} - Sets: ${exercise.sets}, Reps: ${exercise.reps}"
                            } ?: "No exercises"

                            tvWednesday.text = it.Wednesday?.joinToString(separator = "\n") { exercise ->
                                "${exercise.name} - Sets: ${exercise.sets}, Reps: ${exercise.reps}"
                            } ?: "No exercises"

                            tvThursday.text = it.Thursday?.joinToString(separator = "\n") { exercise ->
                                "${exercise.name} - Sets: ${exercise.sets}, Reps: ${exercise.reps}"
                            } ?: "No exercises"

                            tvFriday.text = it.Friday?.joinToString(separator = "\n") { exercise ->
                                "${exercise.name} - Sets: ${exercise.sets}, Reps: ${exercise.reps}"
                            } ?: "No exercises"

                            tvSaturday.text = it.Saturday?.joinToString(separator = "\n") { exercise ->
                                "${exercise.name} - Sets: ${exercise.sets}, Reps: ${exercise.reps}"
                            } ?: "No exercises"

                            tvSunday.text = if (it.Sunday is List<*>) {
                                (it.Sunday as List<ApiDataModels.ChallengeExercise>).joinToString(separator = "\n") { exercise ->
                                    "${exercise.name} - Sets: ${exercise.sets}, Reps: ${exercise.reps}"
                                }
                            } else {
                                "Rest Day"
                            }
                        }
                        Toast.makeText(context, "Workout data loaded", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Log.e("FragmentChallengeInnerView", "fetchWeeklyWorkoutPlan: failed to retrieve workout plan")
                    Toast.makeText(context, "Failed to retrieve workout plan", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ApiDataModels.ChallengeResponse>, t: Throwable) {
                Log.e("FragmentChallengeInnerView", "fetchWeeklyWorkoutPlan: error=${t.localizedMessage}", t)
                Toast.makeText(context, "Error fetching workout plan: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
        Log.d("FragmentChallengeInnerView", "fetchWeeklyWorkoutPlan: completed")
    }

    private fun leaveChallenge(challengeId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val updateRequest = UpdateChallengeStatusRequest(challengeId = challengeId, status = "Challenge Left")

        // Call the Retrofit method with userId as a path parameter
        ApiClient.retrofitService.updateUserChallengeStatus(userId, updateRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Challenge left successfully", Toast.LENGTH_SHORT).show()
                    //tvChallengeProgress.text = "Status: Left" // Update the status directly after leaving
                } else {
                    Toast.makeText(context, "Failed to leave challenge", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            fragment_challenge_innerView().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}