package za.co.varsitycollege.serversamurai.flexforce

import android.graphics.BitmapFactory
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
import java.io.File

class statisticScreen : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    // UI elements for fitness data
    private lateinit var bmiValueTextView: TextView
    private lateinit var bmiStatusTextView: TextView
    private lateinit var bmiProgressBar: ProgressBar
    private lateinit var weightTextView: TextView
    private lateinit var goalWeightTextView: TextView
    private lateinit var bodyFatTextView: TextView

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
        goalWeightTextView = view.findViewById(R.id.goalWeightValue)
        bodyFatTextView = view.findViewById(R.id.currentBodyFatValue)

        // Profile button action
        val profileBtn: ImageButton = view.findViewById(R.id.user_profileBtn)
        loadProfileImage(profileBtn)

        profileBtn.setOnClickListener {
            findNavController().navigate(R.id.action_nav_stats_to_nav_stats_view)
            Toast.makeText(context, "Profile btn clicked", Toast.LENGTH_SHORT).show()
        }

        // Fetch fitness and goal data
        fetchFitnessData()
        fetchGoalData()

        return view
    }

    private fun loadProfileImage(profileButton: ImageButton) {
        // Load the locally stored profile image
        val file = File(requireContext().filesDir, "profile_image.jpg")
        if (file.exists()) {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            profileButton.setImageBitmap(bitmap)
        } else {
            // Set a default image if the profile image isn't available
            profileButton.setImageResource(R.drawable.profilepic)
        }
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
                            val latestEntry = fitnessEntries.last()
                            val weight = latestEntry["currentWeight"].toString().toDoubleOrNull()
                            val height = latestEntry["height"].toString().toDoubleOrNull()
                            val bodyFat = latestEntry["currentBodyFat"].toString().toDoubleOrNull()

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
                        val goalWeightString = document.getString("goalWeight")
                        val goalWeight = goalWeightString?.toDoubleOrNull()

                        if (goalWeight != null) {
                            goalWeightTextView.text = String.format("(%.1f kg)", goalWeight)
                        } else {
                            Toast.makeText(context, "Invalid goal weight data.", Toast.LENGTH_SHORT).show()
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
        val heightInMeters = height / 100
        val bmi = weight / (heightInMeters.pow(2))
        bmiValueTextView.text = String.format("BMI: %.2f", bmi)

        val bmiStatus = when {
            bmi < 18.5 -> "Underweight"
            bmi < 24.9 -> "Normal"
            bmi < 29.9 -> "Overweight"
            else -> "Obese"
        }
        bmiStatusTextView.text = "Status: $bmiStatus"
        bmiProgressBar.progress = bmi.toInt()
    }

    private fun displayBodyStatistics(weight: Double, bodyFat: Double) {
        weightTextView.text = String.format("%.1f kg", weight)
        bodyFatTextView.text = String.format("%.1f%%", bodyFat)
    }
}
