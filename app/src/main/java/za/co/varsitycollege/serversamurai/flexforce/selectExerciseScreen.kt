package za.co.varsitycollege.serversamurai.flexforce

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController

class selectExerciseScreen : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_select_exercise_screen, container, false)

        // Get the Muscle Group button from the layout
        val btnSelectMuscleGroup = view.findViewById<Button>(R.id.btn_muscle_group)

        // Set an OnClickListener on the button
        btnSelectMuscleGroup.setOnClickListener {
            // Navigate to the Select Muscle Group screen
            findNavController().navigate(R.id.action_selectExerciseScreen_to_selectMuscleGroupScreen)
        }

        return view
    }
}
