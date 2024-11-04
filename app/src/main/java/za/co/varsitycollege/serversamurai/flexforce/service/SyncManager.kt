package za.co.varsitycollege.serversamurai.flexforce.service

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import za.co.varsitycollege.serversamurai.flexforce.data.models.ExerciseEntity
import za.co.varsitycollege.serversamurai.flexforce.data.models.UserEntity
import za.co.varsitycollege.serversamurai.flexforce.database.AppDatabase
import java.util.UUID

class SyncManager(private val context: Context) {
    private val database: AppDatabase = AppDatabase.getDatabase(context)
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val apiService = ApiClient.retrofitService
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)

    fun syncData() {
        if (!isConnected()) {
            Log.d("SyncManager", "No internet connection, skipping sync")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // First sync exercises from API
                syncExercisesFromApi()

                // Then sync user data if logged in
                syncUserData()
            } catch (e: Exception) {
                Log.e("SyncManager", "Error during sync", e)
            }
        }
    }

    private suspend fun syncExercisesFromApi() {
        Log.d("SyncManager", "Syncing exercises from API")
        try {
            val response = apiService.getExercisesByMuscles(MuscleRequest(emptyList())).execute()

            if (response.isSuccessful && response.body() != null) {
                val responseBody = response.body()!!
                val allExercises = mutableListOf<ExerciseEntity>()

                responseBody.exercises.forEach { muscleGroup ->
                    muscleGroup.exercises.forEach { exercise ->
                        allExercises.add(
                            ExerciseEntity(
                                id = UUID.randomUUID().toString(),
                                name = exercise.name,
                                sets = exercise.sets,
                                reps = exercise.reps,
                                equipment = exercise.equipment,
                                muscleGroup = muscleGroup.muscleGroup
                            )
                        )
                    }
                }

                // Delete all existing exercises first
                database.exerciseDao().deleteAllExercises()
                // Then insert the new ones
                database.exerciseDao().insertAll(allExercises)
                Log.d("SyncManager", "Exercises synced from API successfully: ${allExercises.size} exercises")
            } else {
                Log.e("SyncManager", "Failed to fetch exercises from API: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("SyncManager", "Error syncing exercises from API", e)
        }
    }

    private suspend fun syncUserData() {
        val userEmail = sharedPreferences.getString("USER_EMAIL", null)
        val userPassword = sharedPreferences.getString("USER_PASSWORD", null)

        if (userEmail == null || userPassword == null) {
            Log.d("SyncManager", "No user credentials found, skipping user sync")
            return
        }

        try {
            // Ensure user is authenticated, creating account if necessary
            if (!ensureUserAuthenticated(userEmail, userPassword)) {
                Log.e("SyncManager", "Failed to authenticate user")
                return
            }

            // Get current user from Room
            val user = database.userDao().getUser(userEmail, userPassword)
            if (user == null) {
                Log.d("SyncManager", "User not found in local database")
                return
            }

            // Sync user profile first
            syncUserProfile(user)

            // Sync all other data
            syncWorkouts(user.email)
            syncFitnessEntries(user.email)
            syncGoals(user.email)

            Log.d("SyncManager", "All data synced successfully for user: ${user.email}")
        } catch (e: Exception) {
            Log.e("SyncManager", "Error syncing user data", e)
        }
    }

    private suspend fun ensureUserAuthenticated(email: String, password: String): Boolean {
        if (auth.currentUser != null) {
            return true
        }

        return try {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                Log.d("SyncManager", "User signed in successfully")
                true
            } catch (e: Exception) {
                if (e.message?.contains("user may have been deleted") == true ||
                    e.message?.contains("no user record") == true) {
                    auth.createUserWithEmailAndPassword(email, password).await()
                    Log.d("SyncManager", "New user account created")
                    true
                } else {
                    Log.e("SyncManager", "Authentication failed", e)
                    false
                }
            }
        } catch (e: Exception) {
            Log.e("SyncManager", "Failed to authenticate or create user", e)
            false
        }
    }

    private suspend fun syncUserProfile(user: UserEntity) {
        try {
            if (auth.currentUser == null) {
                Log.e("SyncManager", "User not authenticated")
                return
            }

            firestore.collection("users")
                .document(user.email)
                .set(user)
                .await()
            Log.d("SyncManager", "User profile synced: ${user.email}")
        } catch (e: Exception) {
            Log.e("SyncManager", "Error syncing user profile", e)
        }
    }

    private suspend fun syncWorkouts(userEmail: String) {
        try {
            if (auth.currentUser == null) {
                Log.e("SyncManager", "User not authenticated")
                return
            }

            val workouts = database.workoutDao().getAllWorkouts(userEmail)
            workouts?.forEach { workout ->
                firestore.collection("users")
                    .document(userEmail)
                    .collection("workouts")
                    .document(workout.id.toString())
                    .set(workout)
                    .await()
            }
            Log.d("SyncManager", "Workouts synced for user: $userEmail")
        } catch (e: Exception) {
            Log.e("SyncManager", "Error syncing workouts", e)
        }
    }

    private suspend fun syncFitnessEntries(userEmail: String) {
        try {
            if (auth.currentUser == null) {
                Log.e("SyncManager", "User not authenticated")
                return
            }

            val fitnessEntries = database.fitnessEntryDao().getAllEntriesForUser(userEmail)
            fitnessEntries.forEach { entry ->
                firestore.collection("users")
                    .document(userEmail)
                    .collection("fitness_entries")
                    .document(entry.id.toString())
                    .set(entry)
                    .await()
            }
            Log.d("SyncManager", "Fitness entries synced for user: $userEmail")
        } catch (e: Exception) {
            Log.e("SyncManager", "Error syncing fitness entries", e)
        }
    }

    private suspend fun syncGoals(userEmail: String) {
        try {
            if (auth.currentUser == null) {
                Log.e("SyncManager", "User not authenticated")
                return
            }

            val goals = database.goalDao().getAllGoalsForUser(userEmail)
            goals.forEach { goal ->
                firestore.collection("users")
                    .document(userEmail)
                    .collection("goals")
                    .document(goal.id.toString())
                    .set(goal)
                    .await()
            }
            Log.d("SyncManager", "Goals synced for user: $userEmail")
        } catch (e: Exception) {
            Log.e("SyncManager", "Error syncing goals", e)
        }
    }

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}