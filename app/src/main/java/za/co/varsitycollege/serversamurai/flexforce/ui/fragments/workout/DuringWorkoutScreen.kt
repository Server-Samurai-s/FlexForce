package za.co.varsitycollege.serversamurai.flexforce.ui.fragments.workout

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import za.co.varsitycollege.serversamurai.flexforce.R
import za.co.varsitycollege.serversamurai.flexforce.database.AppDatabase
import za.co.varsitycollege.serversamurai.flexforce.ui.adapters.DuringExerciseAdapter
import za.co.varsitycollege.serversamurai.flexforce.ui.adapters.DuringExerciseItem
import java.util.Date

class DuringWorkoutScreen : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var duringExerciseAdapter: DuringExerciseAdapter
    private lateinit var finishWorkoutButton: Button
    private lateinit var workoutTitleTextView: TextView
    private lateinit var duringWorkoutBackBtn: ImageView

    private lateinit var database: AppDatabase
    private var workoutId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_during_workout, container, false)

        // Retrieve the workout ID and workout name from the arguments bundle
        workoutId = arguments?.getString("workoutId") ?: "Unknown Workout"

        // Initialize Room database
        database = AppDatabase.getDatabase(requireContext())

        // Initialize views
        workoutTitleTextView = view.findViewById(R.id.workoutTitle)
        recyclerView = view.findViewById(R.id.recyclerViewExercises)
        finishWorkoutButton = view.findViewById(R.id.finishWorkoutButton)
        duringWorkoutBackBtn = view.findViewById(R.id.duringWorkoutBackBtn)

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Fetch exercises for the selected workout using workoutId
        fetchLocalExercises(workoutId)

        // Handle "Finish Workout" button click
        finishWorkoutButton.setOnClickListener {
            if (duringExerciseAdapter.areAllExercisesCompleted()) {
                // Increment the completion count if all exercises are completed
                incrementWorkoutCompletionCount()
            } else {
                Toast.makeText(context, "Please complete all exercises before finishing", Toast.LENGTH_SHORT).show()
            }
        }

        duringWorkoutBackBtn.setOnClickListener{
            findNavController().popBackStack()
        }

        return view
    }

    private fun fetchLocalExercises(workoutId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val workout = database.workoutDao().getWorkout(workoutId)
            withContext(Dispatchers.Main) {
                val localExercises = workout?.exerciseEntities ?: emptyList()
                updateExercises(localExercises.map { exercise ->
                    DuringExerciseItem(
                        exerciseName = exercise.name,
                        sets = exercise.sets,
                        reps = exercise.reps,
                        muscleGroup = exercise.muscleGroup,
                        equipment = exercise.equipment
                    )
                })
            }
        }
    }

    private fun updateExercises(exercises: List<DuringExerciseItem>) {
        duringExerciseAdapter = DuringExerciseAdapter(exercises)
        recyclerView.adapter = duringExerciseAdapter
    }

    private fun incrementWorkoutCompletionCount() {
        CoroutineScope(Dispatchers.IO).launch {
            val workout = database.workoutDao().getWorkout(workoutId)
            workout?.let {
                val updatedWorkout = it.copy(
                    completionCount = it.completionCount + 1,
                    completionDate = Date().toString()
                )
                database.workoutDao().insert(updatedWorkout)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Workout completed successfully!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_duringWorkoutScreen_to_nav_workout)
                }
            }
        }
    }

    private fun Context.isConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}