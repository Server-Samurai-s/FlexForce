package za.co.varsitycollege.serversamurai.flexforce.data.models

data class WorkoutRequest(
    val workoutName: String,
    val workoutDay: String,
    val exerciseEntities: List<ExerciseEntity>,
    val id: String? = null
)