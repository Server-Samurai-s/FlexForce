package za.co.varsitycollege.serversamurai.flexforce

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import za.co.varsitycollege.serversamurai.flexforce.databinding.ItemMuscleGroupBinding

class MuscleGroupAdapter(
    private val muscleGroups: List<MuscleGroupItem>, // List of muscle groups
    private val onMuscleSelected: (MuscleGroupItem) -> Unit // Callback for when a muscle is selected
) : RecyclerView.Adapter<MuscleGroupAdapter.MuscleGroupViewHolder>() {

    inner class MuscleGroupViewHolder(val binding: ItemMuscleGroupBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(muscleGroup: MuscleGroupItem) {
            // Bind muscle group name
            binding.tvMuscleName.text = muscleGroup.name

            // Update the background based on whether the muscle is selected
            if (muscleGroup.isSelected) {
                binding.root.setBackgroundColor(Color.GRAY) // Highlight selected muscle
            } else {
                binding.root.setBackgroundColor(Color.TRANSPARENT) // Default background for unselected
            }

            // Handle muscle group selection on item click
            binding.root.setOnClickListener {
                // Trigger callback to toggle selection
                onMuscleSelected(muscleGroup)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MuscleGroupViewHolder {
        val binding = ItemMuscleGroupBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MuscleGroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MuscleGroupViewHolder, position: Int) {
        holder.bind(muscleGroups[position])
    }

    override fun getItemCount(): Int = muscleGroups.size
}


