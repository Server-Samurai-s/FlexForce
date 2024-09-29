package za.co.varsitycollege.serversamurai.flexforce

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import za.co.varsitycollege.serversamurai.flexforce.Exercise

class ExerciseAdapter(
    private var exercises: List<Exercise>,
    private val onExerciseSelected: (Exercise, Boolean) -> Unit // Callback for selected exercise and its selection state
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    // Tracks selected exercises
    private val selectedExercises: MutableSet<Exercise> = mutableSetOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]

        holder.tvExerciseName.text = exercise.name
        holder.tvSetsReps.text = "${exercise.sets} sets x ${exercise.reps} reps"
        holder.tvMuscleGroup.text = exercise.muscleGroup
        holder.tvEquipment.text = exercise.equipment


        // Highlight the item if it's selected
        if (selectedExercises.contains(exercise)) {
            holder.itemView.setBackgroundColor(holder.itemView.context.getColor(R.color.selected_background))
        } else {
            holder.itemView.setBackgroundColor(holder.itemView.context.getColor(android.R.color.transparent))
        }

        // Handle the click to select/deselect the exercise
        holder.itemView.setOnClickListener {
            val isSelected = selectedExercises.contains(exercise)
            if (isSelected) {
                selectedExercises.remove(exercise)
            } else {
                selectedExercises.add(exercise)
            }
            notifyItemChanged(position) // Update the item visual state
            onExerciseSelected(exercise, !isSelected)
        }
    }

    override fun getItemCount(): Int {
        return exercises.size
    }

    fun updateExercises(newExercises: List<Exercise>) {
        exercises = newExercises
        notifyDataSetChanged()
    }

    class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvExerciseName: TextView = itemView.findViewById(R.id.tv_exercise_name)
        val tvSetsReps: TextView = itemView.findViewById(R.id.tv_sets_reps)
        val tvMuscleGroup: TextView = itemView.findViewById(R.id.tv_muscle_group)
        val tvEquipment: TextView = itemView.findViewById(R.id.tv_equipment)
    }
}


