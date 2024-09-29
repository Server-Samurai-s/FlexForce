package za.co.varsitycollege.serversamurai.flexforce.Models

class ApiDataModels {
    data class Workout(
        val exercise1: Exercise,
        val exercise2: Exercise,
        val exercise3: Exercise
    )

    data class Exercise(
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
        val tracking: String
    )
}
