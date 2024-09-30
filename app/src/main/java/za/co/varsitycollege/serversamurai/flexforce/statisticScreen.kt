package za.co.varsitycollege.serversamurai.flexforce

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.pow

class statisticScreen : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    // UI elements for fitness data
    private lateinit var bmiValueTextView: TextView
    private lateinit var bmiStatusTextView: TextView
    private lateinit var bmiProgressBar: ProgressBar
    private lateinit var weightTextView: TextView
    private lateinit var bodyFatTextView: TextView

    // UI elements for goal data
    private lateinit var weightProgressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_statistic_screen, container, false)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Find views for fitness data
        bmiValueTextView = view.findViewById(R.id.textViewBmiValue)
        bmiStatusTextView = view.findViewById(R.id.textViewBmiStatus)
        bmiProgressBar = view.findViewById(R.id.progressBarBmi)
        weightTextView = view.findViewById(R.id.currentWeightValue)
        bodyFatTextView = view.findViewById(R.id.currentBodyFatValue)

        // Find views for goal data
        weightProgressBar = view.findViewById(R.id.progressBarWeight)

        // Fetch fitness and goal data
        fetchFitnessData()
        fetchGoalData()

        // Profile button action
        val profileBtn: ImageButton = view.findViewById(R.id.user_profileBtn)
        profileBtn.setOnClickListener {
            findNavController().navigate(R.id.action_nav_stats_to_nav_stats_view)
            Toast.makeText(context, "Profile btn clicked", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun fetchFitnessData() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users")
                .document(userId)
                .collection("userDetails")
                .document("details")
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val fitnessEntries = document.get("fitnessEntries") as? List<Map<String, Any>>
                        if (!fitnessEntries.isNullOrEmpty()) {
                            // Get the latest fitness entry
                            val latestEntry = fitnessEntries.last()
                            val weight = latestEntry["currentWeight"].toString().toDoubleOrNull()
                            val height = latestEntry["height"].toString().toDoubleOrNull()
                            val bodyFat = latestEntry["currentBodyFat"].toString().toDoubleOrNull()

                            // Check if weight, height, and body fat data are available
                            if (weight != null && height != null && height > 0 && bodyFat != null) {
                                calculateAndDisplayBMI(weight, height)
                                displayBodyStatistics(weight, bodyFat)
                            } else {
                                Toast.makeText(context, "Invalid weight, height, or body fat data.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "No fitness data found.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "User profile not found.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error fetching user data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchGoalData() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users")
                .document(userId)
                .collection("userDetails")
                .document("goals")
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Use .toString() to ensure compatibility and then convert to Double
                        val goalWeightString = document.get("goalWeight").toString()
                        val goalBodyFatString = document.get("goalBodyFat").toString()

                        // Safely convert to Double, and handle potential errors
                        val goalWeight = goalWeightString.toDoubleOrNull()
                        val goalBodyFat = goalBodyFatString.toDoubleOrNull()

                        // Check if the conversion succeeded and update the UI
                        if (goalWeight != null && goalBodyFat != null) {
                            displayGoalStatistics(goalWeight, goalBodyFat)
                        } else {
                            Toast.makeText(context, "Invalid goal data format.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Goal data not found.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error fetching goal data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }


    private fun calculateAndDisplayBMI(weight: Double, height: Double) {
        // Convert height to meters
        val heightInMeters = height / 100

        // Calculate BMI
        val bmi = weight / (heightInMeters.pow(2))

        // Update BMI value text
        bmiValueTextView.text = String.format("BMI: %.2f", bmi)

        // Update BMI status and progress bar
        val bmiStatus = when {
            bmi < 18.5 -> "Underweight"
            bmi < 24.9 -> "Normal"
            bmi < 29.9 -> "Overweight"
            else -> "Obese"
        }
        bmiStatusTextView.text = "Status: $bmiStatus"

        // Update progress bar
        bmiProgressBar.progress = bmi.toInt()
    }

    private fun displayBodyStatistics(weight: Double, bodyFat: Double) {
        // Set weight and body fat statistics
        weightTextView.text = String.format("%.1f kg", weight)
        bodyFatTextView.text = String.format("%.1f%%", bodyFat)
    }

    private fun displayGoalStatistics(goalWeight: Double, goalBodyFat: Double) {
        // Set the goal weight and body fat values

        // Assuming you already have the current weight and body fat, calculate progress
        val currentWeight = weightTextView.text.toString().replace(" kg", "").toDoubleOrNull()
        val currentBodyFat = bodyFatTextView.text.toString().replace("%", "").toDoubleOrNull()

        // Calculate the progress towards the goal weight if available
        if (currentWeight != null) {
            val weightProgress = ((currentWeight / goalWeight) * 100).toInt()
            weightProgressBar.progress = weightProgress
        }

    }
}
