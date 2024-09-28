package za.co.varsitycollege.serversamurai.flexforce

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import za.co.varsitycollege.serversamurai.flexforce.service.Exercise

class ExerciseAdapter(
    private var exercises: List<za.co.varsitycollege.serversamurai.flexforce.service.Exercise>,
    private val onExerciseSelected: (Exercise) -> Unit // Callback for selected exercise
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.tvExerciseName.text = exercise.name
        holder.tvSetsReps.text = "${exercise.sets} sets x ${exercise.reps} reps"

        // Handling the "Back" button press, you can navigate or filter accordingly
//        holder.btnBackMuscle.setOnClickListener {
//            // Handle the back muscle button press if necessary
//        }

        // Handling the "+" button press to select the exercise
        holder.btnAddExercise.setOnClickListener {
            onExerciseSelected(exercise)
        }
    }

    override fun getItemCount(): Int {
        return exercises.size
    }

    class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvExerciseName: TextView = itemView.findViewById(R.id.tv_exercise_name)
        val tvSetsReps: TextView = itemView.findViewById(R.id.tv_sets_reps)
//        val btnBackMuscle: Button = itemView.findViewById(R.id.btn_back_muscle)
        val btnAddExercise: Button = itemView.findViewById(R.id.btn_add_exercise)
    }

    fun updateExercises(newExercises: List<Exercise>) {
        exercises = newExercises
        notifyDataSetChanged()
    }
}

