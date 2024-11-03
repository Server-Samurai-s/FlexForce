package za.co.varsitycollege.serversamurai.flexforce.data.models

data class ChallengeResponse(
    val Monday: List<ChallengeExercise>?,
    val Tuesday: List<ChallengeExercise>?,
    val Wednesday: List<ChallengeExercise>?,
    val Thursday: List<ChallengeExercise>?,
    val Friday: List<ChallengeExercise>?,
    val Saturday: List<ChallengeExercise>?,
    val Sunday: Any? // Can be a List or a message
)