package za.co.varsitycollege.serversamurai.flexforce.ui.fragments.workout

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import za.co.varsitycollege.serversamurai.flexforce.databinding.FragmentWorkoutScreenBinding
import android.text.Editable
import android.text.TextWatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import za.co.varsitycollege.serversamurai.flexforce.R
import za.co.varsitycollege.serversamurai.flexforce.database.AppDatabase
import za.co.varsitycollege.serversamurai.flexforce.ui.adapters.WorkoutAdapter
import za.co.varsitycollege.serversamurai.flexforce.ui.adapters.WorkoutItem

class workoutScreen : Fragment() {

    private var _binding: FragmentWorkoutScreenBinding? = null
    private val binding get() = _binding!!
    private lateinit var workoutAdapter: WorkoutAdapter
    private lateinit var database: AppDatabase
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWorkoutScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the Room database using the singleton pattern
        database = AppDatabase.getDatabase(requireContext())
        sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)

        // Initialize the RecyclerView with an empty list initially
        workoutAdapter = WorkoutAdapter(emptyList(), this)
        binding.recyclerWorkouts.adapter = workoutAdapter
        binding.recyclerWorkouts.layoutManager = LinearLayoutManager(context)

        // Floating Action Button click listener
        binding.fabAddWorkout.setOnClickListener {
            findNavController().navigate(R.id.action_workoutScreen_to_createWorkoutFragment)
        }

        // Set up the search bar listener to filter the workout list
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // After text has changed, filter the workout list
                workoutAdapter.updateWorkoutList(workoutAdapter.workoutList.filter { workout -> workout.name.contains(s.toString(), ignoreCase = true) })
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.workoutsProfileBtn.setOnClickListener{
            findNavController().navigate(R.id.action_nav_workout_to_nav_profile)
        }

        // Fetch and display the workouts
        fetchLocalWorkouts()
    }

    private fun fetchLocalWorkouts() {
        CoroutineScope(Dispatchers.IO).launch {
            val userEmail = sharedPreferences.getString("USER_EMAIL", "") ?: return@launch
            val localWorkouts = database.workoutDao().getAllWorkouts(userEmail)
            withContext(Dispatchers.Main) {
                if (localWorkouts != null) {
                    updateWorkouts(localWorkouts.map { workout ->
                        WorkoutItem(id = workout.id.toString(), day = workout.workoutDay, name = workout.workoutName)
                    })
                }
            }
        }
    }

    private fun updateWorkouts(workouts: List<WorkoutItem>) {
        workoutAdapter.updateWorkoutList(workouts)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}