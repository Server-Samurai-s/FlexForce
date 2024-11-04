package za.co.varsitycollege.serversamurai.flexforce.ui.fragments.exercise

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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import za.co.varsitycollege.serversamurai.flexforce.ui.adapters.ExerciseAdapter
import za.co.varsitycollege.serversamurai.flexforce.R
import za.co.varsitycollege.serversamurai.flexforce.data.models.ExerciseEntity
import za.co.varsitycollege.serversamurai.flexforce.data.models.ExerciseResponse
import za.co.varsitycollege.serversamurai.flexforce.database.AppDatabase

// Define data classes for API request and response
data class MuscleRequest(val muscles: List<String>)

interface ApiService {
    @POST("api/workouts/exercises")
    fun getExercisesByMuscles(@Body request: MuscleRequest): Call<ExerciseResponse>
}

class SelectExerciseScreen : Fragment() {
    private lateinit var rvExerciseList: RecyclerView
    private lateinit var selectedMuscles: List<String>
    private var selectedExerciseEntities: MutableList<ExerciseEntity> = mutableListOf()
    private lateinit var database: AppDatabase

    private lateinit var addSelectedText: TextView
    private lateinit var selectedCounterText: TextView
    private lateinit var selectMuscleGroupButton: Button
    private lateinit var addSelectedLayout: View
    private lateinit var workoutName: String
    private lateinit var selectedDay: String
    private lateinit var selectExerciseBackBtn: ImageView
    private lateinit var selectExerciseWorkoutName: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_select_exercise_screen, container, false)

        // Initialize database
        database = AppDatabase.getDatabase(requireContext())

        // Initialize views and setup
        setupViews(view)
        setupRecyclerView()
        setupClickListeners()

        // Fetch exercises based on selected muscles
        fetchExercisesFromDatabase()

        return view
    }

    private fun setupViews(view: View) {
        // Retrieve arguments
        workoutName = arguments?.getString("workoutName") ?: "Default Workout"
        selectedDay = arguments?.getString("selectedDay") ?: "Monday"
        selectedExerciseEntities = arguments?.getParcelableArrayList<ExerciseEntity>("selectedExercises") ?: mutableListOf()
        selectedMuscles = arguments?.getStringArrayList("selectedMuscles") ?: listOf()

        // Initialize views
        rvExerciseList = view.findViewById(R.id.rv_exercise_list)
        addSelectedText = view.findViewById(R.id.tv_add_selected)
        selectedCounterText = view.findViewById(R.id.tv_selected_counter)
        selectMuscleGroupButton = view.findViewById(R.id.btn_muscle_group)
        addSelectedLayout = view.findViewById(R.id.add_selected_layout)
        selectExerciseBackBtn = view.findViewById(R.id.selectExerciseBackBtn)
        selectExerciseWorkoutName = view.findViewById(R.id.selectExerciseWorkoutName)

        selectExerciseWorkoutName.text = workoutName
        selectedCounterText.text = "${selectedExerciseEntities.size} exercises selected"
    }

    private fun setupRecyclerView() {
        rvExerciseList.layoutManager = LinearLayoutManager(context)
        // Initialize with empty list of ExerciseEntity
        rvExerciseList.adapter = ExerciseAdapter(
            emptyList<ExerciseEntity>(),
            selectedExerciseEntities
        ) { exercise, isSelected ->
            toggleExerciseSelection(exercise, isSelected)
        }
    }

    private fun setupClickListeners() {
        selectMuscleGroupButton.setOnClickListener {
            val bundle = Bundle().apply {
                putString("workoutName", workoutName)
                putString("selectedDay", selectedDay)
                putParcelableArrayList("selectedExercises", ArrayList(selectedExerciseEntities))
            }
            findNavController().navigate(R.id.action_selectExerciseScreen_to_selectMuscleGroupScreen, bundle)
        }

        addSelectedLayout.setOnClickListener {
            val bundle = Bundle().apply {
                putParcelableArrayList("selectedExercises", ArrayList(selectedExerciseEntities))
                putString("workoutName", workoutName)
                putString("selectedDay", selectedDay)
            }
            findNavController().navigate(R.id.action_selectExerciseScreen_to_workoutSummaryScreen, bundle)
        }

        selectExerciseBackBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun fetchExercisesFromDatabase() {
        lifecycleScope.launch(Dispatchers.IO) {
            val exercises = try {
                if (selectedMuscles.isEmpty()) {
                    // If no muscles selected, get all exercises
                    database.exerciseDao().getAllExercises()
                } else {
                    // Get exercises for selected muscle groups
                    database.exerciseDao().getExercisesByMuscleGroup(selectedMuscles)
                }
            } catch (e: Exception) {
                Log.e("Database", "Error fetching exercises", e)
                emptyList<ExerciseEntity>() // Return empty list if there's an error
            }

            withContext(Dispatchers.Main) {
                if (exercises != null) {
                    (rvExerciseList.adapter as ExerciseAdapter).updateExercises(exercises)
                } else {
                    Toast.makeText(context, "No exercises found in database", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun toggleExerciseSelection(exerciseEntity: ExerciseEntity, isSelected: Boolean) {
        if (isSelected) {
            selectedExerciseEntities.add(exerciseEntity)
        } else {
            selectedExerciseEntities.remove(exerciseEntity)
        }
        selectedCounterText.text = "${selectedExerciseEntities.size} exercises selected"
    }
}