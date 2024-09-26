package za.co.varsitycollege.serversamurai.flexforce

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import za.co.varsitycollege.serversamurai.flexforce.databinding.FragmentCreateWorkoutBinding

class CreateWorkoutFragment : Fragment() {
    private lateinit var binding: FragmentCreateWorkoutBinding

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

        // Handle button click to navigate to SelectMuscleGroupFragment
        binding.btnCreateWorkout.setOnClickListener {
            findNavController().navigate(R.id.action_createWorkoutFragment_to_selectExerciseScreen)
        }
    }
}
