package za.co.varsitycollege.serversamurai.flexforce

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MuscleGroupAdapter(
    private val muscleGroups: List<SelectMuscleGroupScreen.MuscleGroup>,
    private val selectedMuscles: List<String> // Pass the selected muscles from the fragment
) : RecyclerView.Adapter<MuscleGroupAdapter.MuscleGroupViewHolder>() {

    inner class MuscleGroupViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val groupName: TextView = view.findViewById(R.id.tv_muscle_group_name)
        val muscleList: LinearLayout = view.findViewById(R.id.layout_muscles)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MuscleGroupViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_muscle_group, parent, false)
        return MuscleGroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: MuscleGroupViewHolder, position: Int) {
        val muscleGroup = muscleGroups[position]
        holder.groupName.text = muscleGroup.name

        // Populate muscles dynamically
        holder.muscleList.removeAllViews()
        for (muscle in muscleGroup.muscles) {
            val muscleTextView = TextView(holder.itemView.context).apply {
                text = muscle
                setPadding(16, 8, 16, 8)
                setTextColor(Color.WHITE)

                // Highlight the muscle if it's selected
                if (selectedMuscles.contains(muscle)) {
                    setBackgroundColor(Color.YELLOW) // or any highlight color
                } else {
                    setBackgroundColor(Color.TRANSPARENT)
                }
            }
            holder.muscleList.addView(muscleTextView)
        }
    }

    override fun getItemCount(): Int = muscleGroups.size
}






