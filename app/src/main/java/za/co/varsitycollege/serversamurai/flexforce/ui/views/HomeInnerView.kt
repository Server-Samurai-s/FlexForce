package za.co.varsitycollege.serversamurai.flexforce.ui.views

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import za.co.varsitycollege.serversamurai.flexforce.R
import za.co.varsitycollege.serversamurai.flexforce.data.models.FitnessEntryEntity
import za.co.varsitycollege.serversamurai.flexforce.data.models.GoalEntity
import za.co.varsitycollege.serversamurai.flexforce.database.AppDatabase
import java.text.SimpleDateFormat
import java.util.*

class HomeInnerView : Fragment() {
    private lateinit var database: AppDatabase
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var textViewCurrentStreak: TextView
    private lateinit var textViewDays: TextView
    private lateinit var textViewFavoriteWorkoutDay: TextView
    private lateinit var textViewFavoriteWorkoutName: TextView

    private lateinit var editTxtGoalWeight: EditText
    private lateinit var editTxtGoalBodyFat: EditText
    private lateinit var submitGoalButton: Button

    private lateinit var editTxtCurrentWeight: EditText
    private lateinit var editTxtCurrentBodyFat: EditText
    private lateinit var editTxtHeight: EditText
    private lateinit var submitFitnessButton: Button

    private var currentStreak = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_inner_view, container, false)

        // Initialize Room database
        database = AppDatabase.getDatabase(requireContext())
        sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)

        // Initialize TextViews
        textViewCurrentStreak = view.findViewById(R.id.textViewCurrentStreak)
        textViewDays = view.findViewById(R.id.textViewDays)
        textViewFavoriteWorkoutDay = view.findViewById(R.id.tv_favorite_workout_day)
        textViewFavoriteWorkoutName = view.findViewById(R.id.tv_favorite_workout_name)

        // Initialize EditTexts and Buttons for Goals
        editTxtGoalWeight = view.findViewById(R.id.editTxt_GoalWeight)
        editTxtGoalBodyFat = view.findViewById(R.id.editTxt_GoalBodyFat)
        submitGoalButton = view.findViewById(R.id.buttonSubmitGoals)

        // Initialize EditTexts and Buttons for Fitness
        editTxtCurrentWeight = view.findViewById(R.id.editTxt_CurrentWeight)
        editTxtCurrentBodyFat = view.findViewById(R.id.editTxt_CurrentBodyFat)
        editTxtHeight = view.findViewById(R.id.editTxt_Height)
        submitFitnessButton = view.findViewById(R.id.buttonSubmitFitness)

        // Fetch the most recent fitness and goal data
        fetchLatestFitnessEntry()
        fetchLatestGoalData()

        // Fetch workout streak
        calculateWorkoutStreak()

        // Fetch favorite workout
        fetchFavoriteWorkout()

        // Handle Submit Button Click for goals
        submitGoalButton.setOnClickListener {
            storeGoalData()
        }

        // Handle Submit Button Click for fitness data
        submitFitnessButton.setOnClickListener {
            storeFitnessData()
        }

        return view
    }

    // Function to calculate the current workout streak
    private fun calculateWorkoutStreak() {
        val userEmail = sharedPreferences.getString("USER_EMAIL", "") ?: return

        lifecycleScope.launch(Dispatchers.IO) {
            val workouts = database.workoutDao().getAllWorkouts(userEmail)
            if (workouts != null) {
                if (workouts.isNotEmpty()) {
                    var streak = 0
                    val today = getStartOfDay(Date())

                    workouts.forEach { workout ->
                        val workoutDate = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH).parse(workout.completionDate)
                        workoutDate?.let {
                            val workoutStartOfDay = getStartOfDay(it)
                            val expectedDay = Date(today.time - streak * 86400000L)

                            if (streak == 0 && workoutStartOfDay == today) {
                                streak += 1
                            } else if (streak > 0 && workoutStartOfDay == expectedDay) {
                                streak += 1
                            } else {
                                return@forEach
                            }
                        }
                    }

                    currentStreak = streak
                    withContext(Dispatchers.Main) {
                        updateStreakUI()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        updateStreakUI()
                    }
                }
            }
        }
    }

    // Helper function to get the start of the day for a given date
    private fun getStartOfDay(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    // Function to update the streak in the UI
    private fun updateStreakUI() {
        textViewDays.text = "$currentStreak days"
    }

    // Fetch the workout with the largest completion count
    private fun fetchFavoriteWorkout() {
        val userEmail = sharedPreferences.getString("USER_EMAIL", "") ?: return

        lifecycleScope.launch(Dispatchers.IO) {
            val favoriteWorkout = database.workoutDao().getFavouriteWorkout(userEmail)
            favoriteWorkout?.let {
                withContext(Dispatchers.Main) {
                    textViewFavoriteWorkoutDay.text = it.workoutDay.substring(0, 3)
                    textViewFavoriteWorkoutName.text = it.workoutName
                }
            }
        }
    }

    // Function to fetch the most recent fitness entry
    private fun fetchLatestFitnessEntry() {
        val userEmail = sharedPreferences.getString("USER_EMAIL", "") ?: return

        lifecycleScope.launch(Dispatchers.IO) {
            val latestEntry = database.fitnessEntryDao().getLatestEntryForUser(userEmail)
            latestEntry?.let {
                withContext(Dispatchers.Main) {
                    editTxtCurrentWeight.hint = "${it.currentWeight} Kg"
                    editTxtCurrentBodyFat.hint = "${it.currentBodyFat} %"
                    editTxtHeight.hint = "${it.height} cm"
                }
            }
        }
    }

    // Function to fetch the most recent goal data
    private fun fetchLatestGoalData() {
        val userEmail = sharedPreferences.getString("USER_EMAIL", "") ?: return

        lifecycleScope.launch(Dispatchers.IO) {
            val latestGoal = database.goalDao().getAllGoalsForUser(userEmail).firstOrNull()
            latestGoal?.let {
                withContext(Dispatchers.Main) {
                    editTxtGoalWeight.hint = "${it.goalWeight} Kg"
                    editTxtGoalBodyFat.hint = "${it.goalBodyFat} %"
                }
            }
        }
    }

    // Function to store goal data
    private fun storeGoalData() {
        val userEmail = sharedPreferences.getString("USER_EMAIL", "") ?: return

        val goalWeight = editTxtGoalWeight.text.toString()
        val goalBodyFat = editTxtGoalBodyFat.text.toString()

        if (goalWeight.isEmpty() || goalBodyFat.isEmpty()) {
            Toast.makeText(context, "Please fill in both goal fields", Toast.LENGTH_SHORT).show()
            return
        }

        val goal = GoalEntity(
            userEmail = userEmail,
            goalDate = Date().toString(),
            goalWeight = goalWeight.toDouble(),
            goalBodyFat = goalBodyFat.toDouble()
        )

        lifecycleScope.launch(Dispatchers.IO) {
            database.goalDao().insertGoal(goal)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Goal data saved successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to store fitness data
    private fun storeFitnessData() {
        val userEmail = sharedPreferences.getString("USER_EMAIL", "") ?: return

        val weight = editTxtCurrentWeight.text.toString()
        val bodyFat = editTxtCurrentBodyFat.text.toString()
        val height = editTxtHeight.text.toString()

        if (weight.isEmpty() || bodyFat.isEmpty() || height.isEmpty()) {
            Toast.makeText(context, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
            return
        }

        val statistic = FitnessEntryEntity(
            userEmail = userEmail,
            currentBodyFat = bodyFat.toDouble(),
            currentWeight = weight.toDouble(),
            dateSubmitted = Date().toString(),
            height = height.toDouble()
        )

        lifecycleScope.launch(Dispatchers.IO) {
            database.fitnessEntryDao().insert(statistic)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Fitness data saved successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }
}