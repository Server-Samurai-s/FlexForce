package za.co.varsitycollege.serversamurai.flexforce

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import za.co.varsitycollege.serversamurai.flexforce.databinding.FragmentWorkoutScreenBinding


class workoutScreen : Fragment() {
    private lateinit var binding: FragmentWorkoutScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWorkoutScreenBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle button click to navigate to CreateWorkoutFragment
        binding.fabAddWorkout.setOnClickListener {
            findNavController().navigate(R.id.action_workoutScreen_to_createWorkoutFragment)
        }
    }
}