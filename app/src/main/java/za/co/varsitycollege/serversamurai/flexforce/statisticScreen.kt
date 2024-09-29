package za.co.varsitycollege.serversamurai.flexforce

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import za.co.varsitycollege.serversamurai.flexforce.data.firebaseHelper
import za.co.varsitycollege.serversamurai.flexforce.Models.FitnessData

class statisticScreen : Fragment() {
    private lateinit var editTextGoalWeight: EditText
    private lateinit var editTextBodyFatPercentage: EditText
    private lateinit var editTextHeightCm: EditText
    private lateinit var textViewBMI: TextView
    private val firebaseHelper = firebaseHelper()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_statistic_screen, container, false)
        initializeViews(view)
        return view
    }

    private fun initializeViews(view: View) {
        editTextGoalWeight = view.findViewById(R.id.editTxt_GoalWeight)
        editTextBodyFatPercentage = view.findViewById(R.id.editTxt_BodyFat)
        editTextHeightCm = view.findViewById(R.id.editTxt_Height)
        textViewBMI = view.findViewById(R.id.textViewBmiValue)

        view.findViewById<Button>(R.id.buttonSubmit).setOnClickListener {
            calculateAndDisplayBMI()
        }
    }

    private fun calculateAndDisplayBMI() {
        val weight = editTextGoalWeight.text.toString().toDoubleOrNull()
        val bodyFat = editTextBodyFatPercentage.text.toString().toDoubleOrNull()
        val height = editTextHeightCm.text.toString().toDoubleOrNull()

        if (weight != null && height != null) {
            val fitnessData = FitnessData(goalWeight = weight, bodyFatPercentage = bodyFat ?: 0.0, heightCm = height)
            textViewBMI.text = "BMI: ${fitnessData.bmi}"

            // Update to Firebase
            firebaseHelper.upsertFitnessData(fitnessData) { isSuccess ->
                if (isSuccess) {
                    Toast.makeText(context, "Data updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to update data", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "Please enter valid weight and height", Toast.LENGTH_SHORT).show()
        }
    }
}