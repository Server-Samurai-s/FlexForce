package za.co.varsitycollege.serversamurai.flexforce.Models

class ApiDataModels {
    data class ExerciseResponse(val exercises: List<MuscleGroup>)

    data class MuscleGroup(
        val muscleGroup: String,
        val exercises: List<Exercise>
    )

    data class Exercise(
        val name: String,
        val sets: Int,
        val reps: Int,
        val equipment: String
    )

    data class ChallengeResponse(
    val Monday: List<ChallengeExercise>?,
    val Tuesday: List<ChallengeExercise>?,
    val Wednesday: List<ChallengeExercise>?,
    val Thursday: List<ChallengeExercise>?,
    val Friday: List<ChallengeExercise>?,
    val Saturday: List<ChallengeExercise>?,
    val Sunday: Any? // Can be a List or a message
)

data class ChallengeExercise(
    val name: String,
    val sets: Int,
    val reps: Int
)

    data class Response(
        val message: String
    )

    data class Challenge(
        val challengeId: String,
        val timePeriod: String,
        val challengeType: String,
        val tracking: String,
        val description: String
    )
}