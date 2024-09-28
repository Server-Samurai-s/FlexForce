package za.co.varsitycollege.serversamurai.flexforce

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import za.co.varsitycollege.serversamurai.flexforce.service.ExerciseResponse

// Define data classes for API request and response
data class MuscleRequest(val muscles: List<String>)
data class Exercise(val name: String, val sets: Int, val reps: Int)

interface ApiService {
    @POST("api/workouts/exercises")
    fun getExercisesByMuscles(@Body request: MuscleRequest): Call<ExerciseResponse>
}

class SelectExerciseScreen : Fragment() {

    private lateinit var rvExerciseList: RecyclerView
    private lateinit var apiService: ApiService
    private lateinit var selectedMuscles: List<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_select_exercise_screen, container, false)

        // Initialize RecyclerView
        rvExerciseList = view.findViewById(R.id.rv_exercise_list)
        rvExerciseList.layoutManager = LinearLayoutManager(context)

        // Get selected muscles from arguments (from previous fragment)
        selectedMuscles = arguments?.getStringArrayList("selectedMuscles") ?: listOf("Biceps", "Triceps") // default example

        // Initialize Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://flexforce-api.vercel.app/")  // Vercel API base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        // Button to go to the muscle group selection screen
        val btnSelectMuscleGroup = view.findViewById<Button>(R.id.btn_muscle_group)
        btnSelectMuscleGroup.setOnClickListener {
            findNavController().navigate(R.id.action_selectExerciseScreen_to_selectMuscleGroupScreen)
        }

        // Set up the adapter with a callback for exercise selection
        rvExerciseList.adapter = ExerciseAdapter(emptyList()) { selectedExercise ->
            // Handle what happens when an exercise is selected
            Toast.makeText(context, "${selectedExercise.name} selected", Toast.LENGTH_SHORT).show()
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
}


