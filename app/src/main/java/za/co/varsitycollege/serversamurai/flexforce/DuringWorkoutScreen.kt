package za.co.varsitycollege.serversamurai.flexforce

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class DuringWorkoutScreen : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var duringExerciseAdapter: DuringExerciseAdapter
    private lateinit var finishWorkoutButton: Button
    private lateinit var workoutTitleTextView: TextView
    private lateinit var duringWorkoutBackBtn: ImageView

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var workoutId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_during_workout, container, false)

        // Retrieve the workout ID and workout name from the arguments bundle
        workoutId = arguments?.getString("workoutId") ?: "Unknown Workout"

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize views
        workoutTitleTextView = view.findViewById(R.id.workoutTitle)
        recyclerView = view.findViewById(R.id.recyclerViewExercises)
        finishWorkoutButton = view.findViewById(R.id.finishWorkoutButton)
        duringWorkoutBackBtn = view.findViewById(R.id.duringWorkoutBackBtn)

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Fetch exercises for the selected workout using workoutId
        fetchExercises(workoutId)

        // Handle "Finish Workout" button click
        finishWorkoutButton.setOnClickListener {
            if (duringExerciseAdapter.areAllExercisesCompleted()) {
                // Increment the completion count if all exercises are completed
                incrementWorkoutCompletionCount()
            } else {
                Toast.makeText(context, "Please complete all exercises before finishing", Toast.LENGTH_SHORT).show()
            }
        }

        duringWorkoutBackBtn.setOnClickListener{
            findNavController().popBackStack()
        }

        return view
    }

    private fun fetchExercises(workoutId: String) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users")
                .document(userId)
                .collection("workouts")
                .document(workoutId)  // Use workoutId to find the specific workout
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Retrieve the workout name and set it in the workout title TextView
                        val workoutName = document.getString("workoutName") ?: ""
                        workoutTitleTextView.text = workoutName  // Dynamically set the workout name

                        // Safely cast exercises to a List<Map<String, Any>> and filter them
                        val exercises = document.get("exercises") as? List<Map<String, Any>>
                        exercises?.let {
                            val exerciseItems = exercises.mapNotNull { exerciseMap ->
                                // Extract exercise details safely
                                val exerciseName = exerciseMap["name"] as? String ?: return@mapNotNull null
                                val sets = (exerciseMap["sets"] as? Long)?.toInt() ?: return@mapNotNull null
                                val reps = (exerciseMap["reps"] as? Long)?.toInt() ?: return@mapNotNull null
                                val muscleGroup = exerciseMap["muscleGroup"] as? String ?: return@mapNotNull null
                                val equipment = exerciseMap["equipment"] as? String ?: return@mapNotNull null

                                // Return an exercise item
                                DuringExerciseItem(
                                    exerciseName = exerciseName,
                                    sets = sets,
                                    reps = reps,
                                    muscleGroup = muscleGroup,
                                    equipment = equipment
                                )
                            }
                            // Initialize adapter with fetched exercises
                            duringExerciseAdapter = DuringExerciseAdapter(exerciseItems)
                            recyclerView.adapter = duringExerciseAdapter
                        } ?: run {
                            Toast.makeText(context, "No exercises found", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Workout not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error fetching exercises: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun incrementWorkoutCompletionCount() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val workoutRef = firestore.collection("users")
                .document(userId)
                .collection("workouts")
                .document(workoutId)

            // Get the current date
            val currentDate = com.google.firebase.Timestamp.now()

            // Create a map with the fields to update
            val updates = mapOf(
                "completionCount" to FieldValue.increment(1),  // Increment completion count
                "completionDate" to currentDate  // Store the date when the workout was completed
            )

            // Update Firestore with the new fields
            workoutRef.update(updates)
                .addOnSuccessListener {
                    Toast.makeText(context, "Workout completed successfully!", Toast.LENGTH_SHORT).show()
                    // Navigate to another screen if needed
                    findNavController().navigate(R.id.action_duringWorkoutScreen_to_nav_workout)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error completing workout: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

}
