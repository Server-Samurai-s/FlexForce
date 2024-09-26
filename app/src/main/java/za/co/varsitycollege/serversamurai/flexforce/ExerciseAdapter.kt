package za.co.varsitycollege.serversamurai.flexforce

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import za.co.varsitycollege.serversamurai.flexforce.databinding.ItemExerciseBinding

class ExerciseAdapter(
    private val exercises: List<Exercises>,
    private val addExercise: (Exercises) -> Unit
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    inner class ExerciseViewHolder(val binding: ItemExerciseBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(exercise: Exercises) {
            // Binding data to the views
            binding.tvExerciseName.text = exercise.name
            binding.tvExerciseCategory.text = exercise.category
            binding.ivExerciseIcon.setImageResource(exercise.icon)

            // Handle the "+" button click to add the exercise
            binding.btnAddExercise.setOnClickListener {
                addExercise(exercise)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val binding = ItemExerciseBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ExerciseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        holder.bind(exercises[position])
    }

    override fun getItemCount(): Int = exercises.size
}

