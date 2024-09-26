package za.co.varsitycollege.serversamurai.flexforce

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import za.co.varsitycollege.serversamurai.flexforce.databinding.FragmentWorkoutScreenBinding

class workoutScreen : Fragment() {

    // Declare the binding variable
    private var _binding: FragmentWorkoutScreenBinding? = null
    private val binding get() = _binding!!

    private lateinit var workoutAdapter: WorkoutAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using ViewBinding
        _binding = FragmentWorkoutScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Example workout data
        val workoutList = listOf(
            WorkoutItem("Mon", "Chest and Biceps"),
            WorkoutItem("Tue", "Back and Triceps"),
            WorkoutItem("Wed", "Legs")
        )

        // Set up the RecyclerView with the adapter
        workoutAdapter = WorkoutAdapter(workoutList)
        binding.recyclerWorkouts.adapter = workoutAdapter
        binding.recyclerWorkouts.layoutManager = LinearLayoutManager(context)

        // Set up Floating Action Button click listener
        binding.fabAddWorkout.setOnClickListener {
            // Navigate to create a new workout screen
            findNavController().navigate(R.id.action_workoutScreen_to_createWorkoutFragment)
        }

        // Set up the search bar (currently no functionality; you'll add logic later)
        binding.searchBar.setOnEditorActionListener { v, actionId, event ->
            // Handle search logic here (optional)
            false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}
