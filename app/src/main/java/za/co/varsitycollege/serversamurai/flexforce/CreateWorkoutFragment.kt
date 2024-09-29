package za.co.varsitycollege.serversamurai.flexforce

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import za.co.varsitycollege.serversamurai.flexforce.databinding.FragmentCreateWorkoutBinding

class CreateWorkoutFragment : Fragment() {
    private lateinit var binding: FragmentCreateWorkoutBinding

    private lateinit var workoutName: String
    private lateinit var selectedDay: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCreateWorkoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up spinner options (for day selection)
        val daysOfWeek = arrayOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, daysOfWeek)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerDay.adapter = adapter

        // Handle button click to navigate to SelectMuscleGroupFragment
        binding.btnCreateWorkout.setOnClickListener {
            workoutName = binding.etWorkoutName.text.toString()
            selectedDay = binding.spinnerDay.selectedItem.toString()
            Log.e("Workout Name", workoutName)
            Log.e("Selected Day", selectedDay)

            if (workoutName.isBlank()) {
                Toast.makeText(context, "Please enter a workout name", Toast.LENGTH_SHORT).show()
            } else {
                val bundle = Bundle().apply {
                    putString("workoutName", workoutName)
                    putString("selectedDay", selectedDay)
                }
                // Pass the workout name and day to the next fragment (SelectExerciseScreen)
                findNavController().navigate(R.id.action_createWorkoutFragment_to_selectExerciseScreen, bundle)
            }
        }
    }
}
