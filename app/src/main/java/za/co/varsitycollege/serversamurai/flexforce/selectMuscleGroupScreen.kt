package za.co.varsitycollege.serversamurai.flexforce

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.ViewSwitcher
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SelectMuscleGroupScreen : Fragment() {

    private lateinit var viewSwitcher: ViewSwitcher
    private lateinit var selectedMuscles: MutableList<String>
    private lateinit var adapter: MuscleGroupAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_select_muscle_group_screen, container, false)

        viewSwitcher = view.findViewById(R.id.viewSwitcher)
        selectedMuscles = mutableListOf()

        // Handle ViewSwitcher button
        val switchViewButton = view.findViewById<Button>(R.id.btn_switch_view)
        switchViewButton.setOnClickListener {
            viewSwitcher.showNext()  // Switch between front and back views
        }

        // Click listeners for front view muscle groups
        view.findViewById<View>(R.id.clickable_forearms_front).setOnClickListener {
            handleMuscleSelection("Forearms (Front)")
        }

        view.findViewById<View>(R.id.clickable_biceps).setOnClickListener {
            handleMuscleSelection("Biceps")
        }

        // Click listeners for back view muscle groups
        view.findViewById<View>(R.id.clickable_forearms_back).setOnClickListener {
            handleMuscleSelection("Forearms (Back)")
        }

        view.findViewById<View>(R.id.clickable_upper_back).setOnClickListener {
            handleMuscleSelection("Upper Back")
        }

        // Handle Clear button
        view.findViewById<TextView>(R.id.tv_clear).setOnClickListener {
            clearSelections()
        }

        // Handle Apply button
        view.findViewById<TextView>(R.id.tv_apply).setOnClickListener {
            applySelections()
        }

        return view
    }

    private fun handleMuscleSelection(muscleName: String) {
        // Toggle muscle selection
        if (selectedMuscles.contains(muscleName)) {
            selectedMuscles.remove(muscleName)
        } else {
            selectedMuscles.add(muscleName)
        }

        // Update Apply button text with selected count
        updateApplyButton()
    }

    private fun updateApplyButton() {
        val applyButton = view?.findViewById<TextView>(R.id.tv_apply)
        applyButton?.text = if (selectedMuscles.isNotEmpty()) {
            "APPLY (${selectedMuscles.size})"
        } else {
            "APPLY"
        }
    }

    private fun clearSelections() {
        // Clear all selections
        selectedMuscles.clear()
        updateApplyButton()
    }

    private fun applySelections() {
        // Handle applying the selected muscles (e.g., pass data back to previous fragment or activity)
        Toast.makeText(context, "Selected Muscles: ${selectedMuscles.joinToString()}", Toast.LENGTH_SHORT).show()
    }
}


