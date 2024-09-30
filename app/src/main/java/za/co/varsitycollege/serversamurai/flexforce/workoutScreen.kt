package za.co.varsitycollege.serversamurai.flexforce

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

class workoutScreen : Fragment() {

    private var _binding: FragmentWorkoutScreenBinding? = null
    private val binding get() = _binding!!
    private lateinit var workoutAdapter: WorkoutAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWorkoutScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        fetchUserWorkouts()
    }

    private fun fetchUserWorkouts() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "default_user_id"
        ApiClient.retrofitService.getUserWorkouts(userId).enqueue(object : Callback<List<WorkoutRequest>> {
            override fun onResponse(call: Call<List<WorkoutRequest>>, response: Response<List<WorkoutRequest>>) {
                if (response.isSuccessful) {
                    val workouts = response.body() ?: emptyList()
                    updateWorkouts(workouts)
                } else {
                    Log.e("API_ERROR", "Failed to fetch workouts: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<WorkoutRequest>>, t: Throwable) {
                Log.e("API_ERROR", "Error fetching workouts", t)
            }
        })
    }

    private fun updateWorkouts(workouts: List<WorkoutRequest>) {
        val workoutItems = workouts.map { workout ->
            WorkoutItem(id = workout.id ?: "", day = workout.workoutDay, name = workout.workoutName)
        }
        workoutAdapter.updateWorkoutList(workoutItems)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}