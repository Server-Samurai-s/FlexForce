package za.co.varsitycollege.serversamurai.flexforce.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import za.co.varsitycollege.serversamurai.flexforce.R
import za.co.varsitycollege.serversamurai.flexforce.databinding.DuringItemExerciseBinding

data class DuringExerciseItem(
    val exerciseName: String,
    val sets: Int,
    val reps: Int,
    val muscleGroup: String,
    val equipment: String
)

class DuringExerciseAdapter(private var exerciseList: List<DuringExerciseItem>) : RecyclerView.Adapter<DuringExerciseAdapter.ExerciseViewHolder>() {

    private val completedExercises = mutableSetOf<Int>()

    inner class ExerciseViewHolder(private val binding: DuringItemExerciseBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(exercise: DuringExerciseItem) {
            // Set muscle group initial in the circular view
            binding.tvMuscleGroup.text = exercise.muscleGroup
            // Set exercise name, sets, reps, and equipment
            binding.tvExerciseName.text = exercise.exerciseName
            binding.tvSetsReps.text = "${exercise.sets} sets | ${exercise.reps} reps"
            binding.tvEquipment.text = exercise.equipment

            // Get the adapter position using adapterPosition
            val position = adapterPosition

            if (completedExercises.contains(position)) {
                binding.ivTodo.setImageResource(R.drawable.ic_check) // Checked icon
            } else {
                binding.ivTodo.setImageResource(R.drawable.ic_todo) // Todo icon
            }

            // Mark exercise as completed/uncompleted on click
            binding.ivTodo.setOnClickListener {
                if (completedExercises.contains(position)) {
                    completedExercises.remove(position)
                } else {
                    completedExercises.add(position)
                }
                notifyItemChanged(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val binding = DuringItemExerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExerciseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        holder.bind(exerciseList[position])
    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }

    // Function to update the exercise list and notify the adapter
    fun updateExerciseList(newExercises: List<DuringExerciseItem>) {
        exerciseList = newExercises
        notifyDataSetChanged()
    }

    // Function to check if all exercises are completed
    fun areAllExercisesCompleted(): Boolean {
        return completedExercises.size == exerciseList.size
    }
}
