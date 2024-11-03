package za.co.varsitycollege.serversamurai.flexforce

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import za.co.varsitycollege.serversamurai.flexforce.databinding.FragmentWorkoutScreenBinding
import za.co.varsitycollege.serversamurai.flexforce.service.ApiClient
import za.co.varsitycollege.serversamurai.flexforce.service.WorkoutRequest
import android.text.Editable
import android.text.TextWatcher
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import za.co.varsitycollege.serversamurai.flexforce.Models.WorkoutEntity
import za.co.varsitycollege.serversamurai.flexforce.service.AppDatabase

class workoutScreen : Fragment() {

    private var _binding: FragmentWorkoutScreenBinding? = null
    private val binding get() = _binding!!
    private lateinit var workoutAdapter: WorkoutAdapter
    private lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWorkoutScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the Room database using the singleton pattern
        database = AppDatabase.getDatabase(requireContext())

        // Initialize the RecyclerView with an empty list initially
        workoutAdapter = WorkoutAdapter(emptyList(), this)
        binding.recyclerWorkouts.adapter = workoutAdapter
        binding.recyclerWorkouts.layoutManager = LinearLayoutManager(context)

        // Floating Action Button click listener
        binding.fabAddWorkout.setOnClickListener {
            findNavController().navigate(R.id.action_workoutScreen_to_createWorkoutFragment)
        }

        // Set up the search bar listener to filter the workout list
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // After text has changed, filter the workout list
                workoutAdapter.updateWorkoutList(workoutAdapter.workoutList.filter { workout -> workout.name.contains(s.toString(), ignoreCase = true) })
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.workoutsProfileBtn.setOnClickListener{
            findNavController().navigate(R.id.action_nav_workout_to_nav_profile)
        }

        // Fetch and display the workouts
        fetchLocalWorkouts()
    }

    private fun fetchLocalWorkouts() {
        CoroutineScope(Dispatchers.IO).launch {
            val localWorkouts = database.workoutDao().getAllWorkouts()
            CoroutineScope(Dispatchers.Main).launch {
                updateWorkouts(localWorkouts.map { workout ->
                    WorkoutItem(id = workout.id.toString(), day = workout.workoutDay, name = workout.workoutName)
                })
                if (requireContext().isConnected()) {
                    syncLocalWorkoutsToRemote(localWorkouts)
                    fetchRemoteWorkouts()
                }
            }
        }
    }

    private fun syncLocalWorkoutsToRemote(localWorkouts: List<WorkoutEntity>) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.getIdToken(true)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val idToken = task.result?.token
                ApiClient.setAuthToken(idToken ?: "")

                val userId = user.uid
                localWorkouts.forEach { workout ->
                    // Check if the workout already exists on the remote server
                    ApiClient.retrofitService.getUserWorkouts(userId).enqueue(object : Callback<List<WorkoutRequest>> {
                        override fun onResponse(call: Call<List<WorkoutRequest>>, response: Response<List<WorkoutRequest>>) {
                            if (response.isSuccessful) {
                                val remoteWorkouts = response.body() ?: emptyList()
                                val workoutExists = remoteWorkouts.any { it.workoutName == workout.workoutName && it.workoutDay == workout.workoutDay }

                                if (!workoutExists) {
                                    // If the workout does not exist, add it to the remote server
                                    val workoutRequest = WorkoutRequest(workoutName = workout.workoutName, workoutDay = workout.workoutDay, exercises = workout.exercises)
                                    ApiClient.retrofitService.addWorkout(userId, workoutRequest).enqueue(object : Callback<Void> {
                                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                            if (response.isSuccessful) {
                                                Log.d("SYNC_SUCCESS", "Workout synced successfully: ${workout.workoutName}")
                                            } else {
                                                Log.e("SYNC_ERROR", "Failed to sync workout: ${response.message()}")
                                            }
                                        }

                                        override fun onFailure(call: Call<Void>, t: Throwable) {
                                            Log.e("SYNC_ERROR", "Error syncing workout: ${t.message}", t)
                                        }
                                    })
                                }
                            } else {
                                Log.e("API_ERROR", "Failed to fetch remote workouts: ${response.message()}")
                            }
                        }

                        override fun onFailure(call: Call<List<WorkoutRequest>>, t: Throwable) {
                            Log.e("API_ERROR", "Error fetching remote workouts", t)
                        }
                    })
                }
            } else {
                Log.e("AUTH_ERROR", "Error getting ID token: ${task.exception?.message}")
            }
        }
    }

    private fun fetchRemoteWorkouts() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "default_user_id"
        ApiClient.retrofitService.getUserWorkouts(userId).enqueue(object : Callback<List<WorkoutRequest>> {
            override fun onResponse(call: Call<List<WorkoutRequest>>, response: Response<List<WorkoutRequest>>) {
                if (response.isSuccessful) {
                    val workouts = response.body() ?: emptyList()
                    updateWorkouts(workouts.map { workout ->
                        WorkoutItem(id = workout.id ?: "", day = workout.workoutDay, name = workout.workoutName)
                    })
                } else {
                    Log.e("API_ERROR", "Failed to fetch workouts: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<WorkoutRequest>>, t: Throwable) {
                Log.e("API_ERROR", "Error fetching workouts", t)
            }
        })
    }

    private fun Context.isConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun updateWorkouts(workouts: List<WorkoutItem>) {
        workoutAdapter.updateWorkoutList(workouts)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}