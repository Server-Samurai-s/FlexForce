package za.co.varsitycollege.serversamurai.flexforce

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import za.co.varsitycollege.serversamurai.flexforce.databinding.ItemWorkoutBinding

data class WorkoutItem(val day: String, val name: String)

class WorkoutAdapter(private var workoutList: List<WorkoutItem>) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    private var filteredWorkoutList: List<WorkoutItem> = workoutList

    inner class WorkoutViewHolder(private val binding: ItemWorkoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(workout: WorkoutItem) {
            binding.tvDayOfWeek.text = workout.day
            binding.tvWorkoutName.text = workout.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val binding = ItemWorkoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WorkoutViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        holder.bind(filteredWorkoutList[position])
    }

    override fun getItemCount(): Int {
        return filteredWorkoutList.size
    }

    // Function to update the workout list and notify the adapter
    fun updateWorkoutList(newWorkouts: List<WorkoutItem>) {
        workoutList = newWorkouts
        filteredWorkoutList = newWorkouts
        notifyDataSetChanged()
    }

    // Function to filter the workout list based on the search query
    fun filter(query: String) {
        filteredWorkoutList = if (query.isEmpty()) {
            workoutList
        } else {
            workoutList.filter { it.name.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }
}

