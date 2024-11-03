package za.co.varsitycollege.serversamurai.flexforce

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.fragment.findNavController
import za.co.varsitycollege.serversamurai.flexforce.databinding.ItemWorkoutBinding

data class WorkoutItem(val id: String, val day: String, val name: String) // Include ID

class WorkoutAdapter(
    var workoutList: List<WorkoutItem>,
    private val fragment: Fragment // Pass the fragment reference
) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    inner class WorkoutViewHolder(private val binding: ItemWorkoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(workout: WorkoutItem) {
            binding.tvDayOfWeek.text = workout.day
            binding.tvWorkoutName.text = workout.name

            // Set onClickListener for the workout item
            binding.root.setOnClickListener {
                // Create a bundle to pass the workout ID (not name)
                val bundle = Bundle()
                bundle.putString("workoutId", workout.id) // Pass the workoutId to the bundle

                // Use the fragment reference to navigate to the workout detail screen with bundle
                fragment.findNavController().navigate(R.id.action_nav_workout_to_duringWorkoutScreen, bundle)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val binding = ItemWorkoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WorkoutViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        holder.bind(workoutList[position])
    }

    override fun getItemCount(): Int {
        return workoutList.size
    }

    // Function to update the workout list and notify the adapter
    fun updateWorkoutList(newWorkouts: List<WorkoutItem>) {
        workoutList = newWorkouts
        notifyDataSetChanged()
    }
}
