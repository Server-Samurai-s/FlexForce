package za.co.varsitycollege.serversamurai.flexforce

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import za.co.varsitycollege.serversamurai.flexforce.databinding.ItemWorkoutBinding

data class WorkoutItem(val day: String, val name: String)

class WorkoutAdapter(private val workoutList: List<WorkoutItem>) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    inner class WorkoutViewHolder(private val binding: ItemWorkoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(workout: WorkoutItem) {
            binding.textDay.text = workout.day
            binding.textWorkoutName.text = workout.name
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
}
