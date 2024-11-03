package za.co.varsitycollege.serversamurai.flexforce

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import za.co.varsitycollege.serversamurai.flexforce.service.AppDatabase

class DuringWorkoutScreen : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var duringExerciseAdapter: DuringExerciseAdapter
    private lateinit var finishWorkoutButton: Button
    private lateinit var workoutTitleTextView: TextView
    private lateinit var duringWorkoutBackBtn: ImageView

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var database: AppDatabase
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

        // Initialize Room database
        database = AppDatabase.getDatabase(requireContext())

        // Initialize views
        workoutTitleTextView = view.findViewById(R.id.workoutTitle)
        recyclerView = view.findViewById(R.id.recyclerViewExercises)
        finishWorkoutButton = view.findViewById(R.id.finishWorkoutButton)
        duringWorkoutBackBtn = view.findViewById(R.id.duringWorkoutBackBtn)

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Fetch exercises for the selected workout using workoutId
        fetchLocalExercises(workoutId)

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

    private fun fetchLocalExercises(workoutId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val workout = database.workoutDao().getWorkout(workoutId)
            CoroutineScope(Dispatchers.Main).launch {
                val localExercises = workout?.exercises ?: emptyList()
                updateExercises(localExercises.map { exercise ->
                    DuringExerciseItem(
                        exerciseName = exercise.name,
                        sets = exercise.sets,
                        reps = exercise.reps,
                        muscleGroup = exercise.muscleGroup,
                        equipment = exercise.equipment
                    )
                })
                if (requireContext().isConnected()) {
                    fetchRemoteExercises(workoutId)
                }
            }
        }
    }

    private fun fetchRemoteExercises(workoutId: String) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users")
                .document(userId)
                .collection("workouts")
                .document(workoutId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val workoutName = document.getString("workoutName") ?: ""
                        workoutTitleTextView.text = workoutName

                        val exercises = document.get("exercises") as? List<Map<String, Any>>
                        exercises?.let {
                            val exerciseItems = exercises.mapNotNull { exerciseMap ->
                                val exerciseName = exerciseMap["name"] as? String ?: return@mapNotNull null
                                val sets = (exerciseMap["sets"] as? Long)?.toInt() ?: return@mapNotNull null
                                val reps = (exerciseMap["reps"] as? Long)?.toInt() ?: return@mapNotNull null
                                val muscleGroup = exerciseMap["muscleGroup"] as? String ?: return@mapNotNull null
                                val equipment = exerciseMap["equipment"] as? String ?: return@mapNotNull null

                                DuringExerciseItem(
                                    exerciseName = exerciseName,
                                    sets = sets,
                                    reps = reps,
                                    muscleGroup = muscleGroup,
                                    equipment = equipment
                                )
                            }
                            updateExercises(exerciseItems)
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

    private fun updateExercises(exercises: List<DuringExerciseItem>) {
        duringExerciseAdapter = DuringExerciseAdapter(exercises)
        recyclerView.adapter = duringExerciseAdapter
    }

    private fun incrementWorkoutCompletionCount() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val workoutRef = firestore.collection("users")
                .document(userId)
                .collection("workouts")
                .document(workoutId)

            val currentDate = com.google.firebase.Timestamp.now()

            val updates = mapOf(
                "completionCount" to FieldValue.increment(1),
                "completionDate" to currentDate
            )

            workoutRef.update(updates)
                .addOnSuccessListener {
                    Toast.makeText(context, "Workout completed successfully!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_duringWorkoutScreen_to_nav_workout)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error completing workout: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun Context.isConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}