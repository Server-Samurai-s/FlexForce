package za.co.varsitycollege.serversamurai.flexforce.data.models

data class WorkoutRequest(
    val workoutName: String,
    val workoutDay: String,
    val exercises: List<Exercise>,
    val id: String? = null
)