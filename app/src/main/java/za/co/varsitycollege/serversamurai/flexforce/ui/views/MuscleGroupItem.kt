package za.co.varsitycollege.serversamurai.flexforce.ui.views

data class MuscleGroupItem(
    val name: String,
    val icon: Int,  // Drawable resource ID
    var isSelected: Boolean = false  // Keeps track of selection status
)
