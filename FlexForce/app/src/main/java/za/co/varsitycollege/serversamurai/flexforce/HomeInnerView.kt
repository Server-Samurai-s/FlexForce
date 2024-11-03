package za.co.varsitycollege.serversamurai.flexforce

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import java.util.*

class HomeInnerView : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

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

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

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
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users")
                .document(userId)
                .collection("workouts")
                .orderBy("completionDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(10) // Fetch last 10 completed workouts
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val workouts = querySnapshot.documents
                    if (workouts.isNotEmpty()) {
                        var streak = 0
                        val today = getStartOfDay(Date())

                        // Using forEachIndexed to iterate through the workouts
                        workouts.forEachIndexed { index, document ->
                            val workoutDate = (document["completionDate"] as? Timestamp)?.toDate()

                            workoutDate?.let {
                                val workoutStartOfDay = getStartOfDay(it)
                                val expectedDay = Date(today.time - streak * 86400000L)

                                Log.d("WORKOUT_STREAK", "Workout Date: $workoutStartOfDay, Expected Day: $expectedDay")

                                if (streak == 0 && workoutStartOfDay == today) {
                                    streak += 1
                                    Log.d("WORKOUT_STREAK", "Workout today found. Streak incremented to: $streak")
                                } else if (streak > 0 && workoutStartOfDay == expectedDay) {
                                    streak += 1
                                    Log.d("WORKOUT_STREAK", "Workout for consecutive day found. Streak incremented to: $streak")
                                } else {
                                    Log.d("WORKOUT_STREAK", "Workout streak broken.")
                                    return@forEachIndexed
                                }
                            } ?: run {
                                Log.d("WORKOUT_STREAK", "Workout completion date not found or invalid.")
                            }
                        }

                        currentStreak = streak
                        updateStreakUI()
                    } else {
                        Log.d("WORKOUT_STREAK", "No completed workouts found.")
                        updateStreakUI()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("WORKOUT_STREAK", "Error fetching workouts: ${exception.message}")
                }
        } else {
            Log.e("WORKOUT_STREAK", "User not authenticated.")
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
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users")
                .document(userId)
                .collection("workouts")
                .orderBy("completionCount", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(1) // Get the workout with the highest completion count
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val favoriteWorkout = querySnapshot.documents[0]
                        val workoutDay = favoriteWorkout.getString("workoutDay") ?: "N/A"
                        val workoutName = favoriteWorkout.getString("workoutName") ?: "N/A"
                        textViewFavoriteWorkoutDay.text = workoutDay.substring(0, 3)
                        textViewFavoriteWorkoutName.text = workoutName
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("FAVORITE_WORKOUT", "Error fetching favorite workout: ${e.message}")
                }
        }
    }

    // Function to fetch the most recent fitness entry
    private fun fetchLatestFitnessEntry() {
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
                            editTxtCurrentWeight.hint = "${latestEntry["currentWeight"]} Kg"
                            editTxtCurrentBodyFat.hint = "${latestEntry["currentBodyFat"]} %"
                            editTxtHeight.hint = "${latestEntry["height"]} cm"
                        } else {
                            Toast.makeText(context, "No fitness data found.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error fetching fitness data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Function to fetch the most recent goal data
    private fun fetchLatestGoalData() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users")
                .document(userId)
                .collection("userDetails")
                .document("goals")
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val goalWeight = document.getString("goalWeight") ?: ""
                        val goalBodyFat = document.getString("goalBodyFat") ?: ""
                        editTxtGoalWeight.hint = "$goalWeight Kg"
                        editTxtGoalBodyFat.hint = "$goalBodyFat %"
                    } else {
                        Toast.makeText(context, "No goal data found.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error fetching goal data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Function to store goal data
    private fun storeGoalData() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val goalWeight = editTxtGoalWeight.text.toString()
            val goalBodyFat = editTxtGoalBodyFat.text.toString()

            if (goalWeight.isEmpty() || goalBodyFat.isEmpty()) {
                Toast.makeText(context, "Please fill in both goal fields", Toast.LENGTH_SHORT).show()
                return
            }

            val goalData = hashMapOf(
                "goalWeight" to goalWeight,
                "goalBodyFat" to goalBodyFat,
                "dateSet" to Timestamp.now()
            )

            firestore.collection("users")
                .document(userId)
                .collection("userDetails")
                .document("goals")
                .set(goalData)
                .addOnSuccessListener {
                    Toast.makeText(context, "Goal data saved successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error saving goal data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Function to store fitness data
    private fun storeFitnessData() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val weight = editTxtCurrentWeight.text.toString()
            val bodyFat = editTxtCurrentBodyFat.text.toString()
            val height = editTxtHeight.text.toString()

            if (weight.isEmpty() || bodyFat.isEmpty() || height.isEmpty()) {
                Toast.makeText(context, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
                return
            }

            val fitnessData = hashMapOf(
                "currentWeight" to weight,
                "currentBodyFat" to bodyFat,
                "height" to height,
                "dateSubmitted" to Timestamp.now()
            )

            firestore.collection("users")
                .document(userId)
                .collection("userDetails")
                .document("details")
                .update("fitnessEntries", FieldValue.arrayUnion(fitnessData))
                .addOnSuccessListener {
                    Toast.makeText(context, "Fitness data saved successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error saving fitness data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
