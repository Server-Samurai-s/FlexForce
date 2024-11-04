package za.co.varsitycollege.serversamurai.flexforce.ui.fragments.stats

import android.content.Context
import android.content.SharedPreferences
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import za.co.varsitycollege.serversamurai.flexforce.R
import za.co.varsitycollege.serversamurai.flexforce.database.AppDatabase
import java.io.File
import kotlin.math.pow

class StatisticScreen : Fragment() {
    private lateinit var database: AppDatabase
    private lateinit var sharedPreferences: SharedPreferences

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

        // Initialize Room database
        database = AppDatabase.getDatabase(requireContext())
        sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)

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
        val userEmail = sharedPreferences.getString("USER_EMAIL", "") ?: return

        lifecycleScope.launch(Dispatchers.IO) {
            val latestEntry = database.fitnessEntryDao().getLatestEntryForUser(userEmail)
            latestEntry?.let {
                val weight = it.currentWeight
                val height = it.height
                val bodyFat = it.currentBodyFat

                if (weight != null && height != null && height > 0 && bodyFat != null) {
                    withContext(Dispatchers.Main) {
                        calculateAndDisplayBMI(weight, height)
                        displayBodyStatistics(weight, bodyFat)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Invalid weight, height, or body fat data.", Toast.LENGTH_SHORT).show()
                    }
                }
            } ?: withContext(Dispatchers.Main) {
                Toast.makeText(context, "No fitness data found.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchGoalData() {
        val userEmail = sharedPreferences.getString("USER_EMAIL", "") ?: return

        lifecycleScope.launch(Dispatchers.IO) {
            val latestGoal = database.goalDao().getAllGoalsForUser(userEmail).firstOrNull()
            latestGoal?.let {
                val goalWeight = it.goalWeight
                withContext(Dispatchers.Main) {
                    goalWeightTextView.text = String.format("(%.1f kg)", goalWeight)
                }
            } ?: withContext(Dispatchers.Main) {
                Toast.makeText(context, "Goal data not found.", Toast.LENGTH_SHORT).show()
            }
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