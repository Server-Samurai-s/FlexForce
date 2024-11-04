package za.co.varsitycollege.serversamurai.flexforce.ui.fragments.workout

import za.co.varsitycollege.serversamurai.flexforce.database.AppDatabase
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import za.co.varsitycollege.serversamurai.flexforce.R
import za.co.varsitycollege.serversamurai.flexforce.data.models.ExerciseEntity
import za.co.varsitycollege.serversamurai.flexforce.data.models.WorkoutEntity
import za.co.varsitycollege.serversamurai.flexforce.ui.adapters.SelectedExerciseAdapter
import java.util.Date

class WorkoutSummaryScreen : Fragment() {

    private lateinit var selectedExerciseEntities: MutableList<ExerciseEntity>
    private lateinit var workoutName: String
    private lateinit var selectedDay: String
    private lateinit var rvSelectedExercises: RecyclerView
    private lateinit var btnDone: Button
    private lateinit var btnAddMoreExercises: Button
    private lateinit var workoutSummaryScreenBackBtn: ImageView
    private lateinit var database: AppDatabase
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_workout_summary_screen, container, false)

        database = AppDatabase.getDatabase(requireContext())
        sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)

        workoutName = arguments?.getString("workoutName") ?: "Default Workout"
        selectedDay = arguments?.getString("selectedDay") ?: "Monday"
        selectedExerciseEntities = arguments?.getParcelableArrayList("selectedExercises") ?: mutableListOf()

        rvSelectedExercises = view.findViewById(R.id.rv_selected_exercises)
        rvSelectedExercises.layoutManager = LinearLayoutManager(context)
        rvSelectedExercises.adapter = SelectedExerciseAdapter(selectedExerciseEntities)

        val tvWorkoutTitle: TextView = view.findViewById(R.id.tv_workout_title)
        tvWorkoutTitle.text = workoutName

        btnDone = view.findViewById(R.id.btn_done)
        btnDone.setOnClickListener {
            saveWorkout()
        }

        btnAddMoreExercises = view.findViewById(R.id.btn_add_more_exercises)
        btnAddMoreExercises.setOnClickListener {
            val bundle = Bundle().apply {
                putParcelableArrayList("selectedExercises", ArrayList(selectedExerciseEntities))
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
        val userEmail = sharedPreferences.getString("USER_EMAIL", "") ?: return

        val workout = WorkoutEntity(
            userEmail = userEmail,
            workoutName = workoutName,
            workoutDay = selectedDay,
            exerciseEntities = selectedExerciseEntities,
            completionDate = Date().toString(),
            completionCount = 0
        )

        CoroutineScope(Dispatchers.IO).launch {
            database.workoutDao().insert(workout)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Workout saved successfully!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_workoutSummaryScreen_to_nav_workout)
            }
        }
    }
}