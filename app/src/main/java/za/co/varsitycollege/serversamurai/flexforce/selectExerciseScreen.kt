package za.co.varsitycollege.serversamurai.flexforce

import android.graphics.Color
import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.fragment.findNavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import za.co.varsitycollege.serversamurai.flexforce.Exercise
import za.co.varsitycollege.serversamurai.flexforce.service.ExerciseResponse

// Define data classes for API request and response
data class MuscleRequest(val muscles: List<String>)

interface ApiService {
    @POST("api/workouts/exercises")
    fun getExercisesByMuscles(@Body request: MuscleRequest): Call<ExerciseResponse>
}

class SelectExerciseScreen : Fragment() {

    private lateinit var rvExerciseList: RecyclerView
    private lateinit var apiService: ApiService
    private lateinit var selectedMuscles: List<String>
    private var selectedExercises: MutableList<Exercise> = mutableListOf()

    private lateinit var addSelectedText: TextView
    private lateinit var selectedCounterText: TextView
    private lateinit var selectMuscleGroupButton: Button
    private lateinit var addSelectedLayout: View

    private lateinit var workoutName: String
    private lateinit var selectedDay: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_select_exercise_screen, container, false)

        // Retrieve workout name and selected day from arguments
        workoutName = arguments?.getString("workoutName") ?: "Default Workout"
        selectedDay = arguments?.getString("selectedDay") ?: "Monday"

        // Initialize RecyclerView
        rvExerciseList = view.findViewById(R.id.rv_exercise_list)
        rvExerciseList.layoutManager = LinearLayoutManager(context)

        // Get the text views for the Add Selected button and the counter
        addSelectedText = view.findViewById(R.id.tv_add_selected)
        selectedCounterText = view.findViewById(R.id.tv_selected_counter)

        // Get selected muscles from arguments (from previous fragment)
        selectedMuscles = arguments?.getStringArrayList("selectedMuscles") ?: listOf() // default example

        // Initialize Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://flexforce-api.vercel.app/")  // Vercel API base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        // Set up the adapter with a callback for exercise selection
        rvExerciseList.adapter = ExerciseAdapter(emptyList()) { exercise, isSelected ->
            toggleExerciseSelection(exercise, isSelected)
        }

        selectMuscleGroupButton = view.findViewById(R.id.btn_muscle_group)
        selectMuscleGroupButton.setOnClickListener {
            val bundle = Bundle().apply {
                putString("workoutName", workoutName)
                putString("selectedDay", selectedDay)
            }
            findNavController().navigate(R.id.action_selectExerciseScreen_to_selectMuscleGroupScreen, bundle)
        }

        addSelectedLayout = view.findViewById(R.id.add_selected_layout)
        addSelectedLayout.setOnClickListener {
            // Pass the selected exercises to the next fragment
            val bundle = Bundle().apply {
                putParcelableArrayList("selectedExercises", ArrayList(selectedExercises))
                putString("workoutName", workoutName)
                putString("selectedDay", selectedDay)
            }

            findNavController().navigate(R.id.action_selectExerciseScreen_to_workoutSummaryScreen, bundle)
        }


        // Fetch exercises based on selected muscles
        fetchFilteredExercises(selectedMuscles)

        return view
    }

    private fun fetchFilteredExercises(muscles: List<String>) {
        val request = MuscleRequest(muscles)
        apiService.getExercisesByMuscles(request).enqueue(object : Callback<ExerciseResponse> {
            override fun onResponse(call: Call<ExerciseResponse>, response: Response<ExerciseResponse>) {
                if (response.isSuccessful) {
                    // Extract the exercises list from the response
                    val exercises = response.body()?.exercises ?: emptyList()

                    // Update the RecyclerView with filtered exercises
                    (rvExerciseList.adapter as ExerciseAdapter).updateExercises(exercises)
                } else {
                    Toast.makeText(context, "Failed to load exercises", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ExerciseResponse>, t: Throwable) {
                Log.e("API_ERROR", "Error fetching data", t)
                Toast.makeText(context, "Error fetching exercises", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun toggleExerciseSelection(exercise: Exercise, isSelected: Boolean) {
        if (isSelected) {
            // Add exercise to selected list
            selectedExercises.add(exercise)
        } else {
            // Remove exercise from selected list
            selectedExercises.remove(exercise)
        }

        // Update the counter text
        val count = selectedExercises.size
        selectedCounterText.text = if (count > 0) {
            "$count exercises selected"
        } else {
            "0 exercises selected"
        }
    }
}






