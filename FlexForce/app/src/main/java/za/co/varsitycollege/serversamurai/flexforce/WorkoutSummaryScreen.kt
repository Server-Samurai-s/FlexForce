package za.co.varsitycollege.serversamurai.flexforce

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.fragment.findNavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import za.co.varsitycollege.serversamurai.flexforce.service.ApiClient
import za.co.varsitycollege.serversamurai.flexforce.service.WorkoutRequest
import com.google.firebase.auth.FirebaseAuth

class WorkoutSummaryScreen : Fragment() {

    private lateinit var selectedExercises: MutableList<Exercise> // The list of selected exercises
    private lateinit var workoutName: String
    private lateinit var selectedDay: String
    private lateinit var rvSelectedExercises: RecyclerView
    private lateinit var btnDone: Button
    private lateinit var btnAddMoreExercises: Button
    private lateinit var workoutSummaryScreenBackBtn: ImageView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_workout_summary_screen, container, false)

        // Retrieve selected exercises, workout name, and selected day from arguments
        workoutName = arguments?.getString("workoutName") ?: "Default Workout"
        selectedDay = arguments?.getString("selectedDay") ?: "Monday"
        selectedExercises = arguments?.getParcelableArrayList("selectedExercises") ?: mutableListOf()

        // Initialize RecyclerView
        rvSelectedExercises = view.findViewById(R.id.rv_selected_exercises)
        rvSelectedExercises.layoutManager = LinearLayoutManager(context)
        rvSelectedExercises.adapter = SelectedExerciseAdapter(selectedExercises)

        // Set workout name in the title
        val tvWorkoutTitle: TextView = view.findViewById(R.id.tv_workout_title)
        tvWorkoutTitle.text = workoutName

        // Initialize Done button
        btnDone = view.findViewById(R.id.btn_done)
        btnDone.setOnClickListener {
            saveWorkout()
        }

        // Add more exercises button
        btnAddMoreExercises = view.findViewById(R.id.btn_add_more_exercises)
        btnAddMoreExercises.setOnClickListener {
            // Navigate to add more exercises screen and pass the current exercises and workout details
            val bundle = Bundle().apply {
                putParcelableArrayList("selectedExercises", ArrayList(selectedExercises)) // Pass previously selected exercises
                putString("workoutName", workoutName)
                putString("selectedDay", selectedDay)
            }
            findNavController().navigate(R.id.action_workoutSummaryScreen_to_selectExerciseScreen, bundle)
        }

        workoutSummaryScreenBackBtn = view.findViewById(R.id.workoutSummaryBackBtn)
        workoutSummaryScreenBackBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        return view
    }

    private fun saveWorkout() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "default_user_id"
        Log.e("WORKOUT_SAVE", "User ID: $userId")
        Log.e("WORKOUT_SAVE", "Workout Name: $workoutName")
        Log.e("WORKOUT_SAVE", "Exercises: $selectedExercises")

        val workoutRequest = WorkoutRequest(workoutName = workoutName, workoutDay = selectedDay, exercises = selectedExercises)

        ApiClient.retrofitService.addWorkout(userId, workoutRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Workout saved successfully!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_workoutSummaryScreen_to_nav_workout)
                } else {
                    Log.e("API_ERROR", "Failed to save workout. Response code: ${response.code()}, message: ${response.message()}")
                    Toast.makeText(context, "Failed to save workout", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("API_ERROR", "Error saving workout: ${t.message}", t)
                Toast.makeText(context, "Error saving workout", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
