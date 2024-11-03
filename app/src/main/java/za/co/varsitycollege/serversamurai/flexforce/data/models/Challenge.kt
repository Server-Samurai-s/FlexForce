package za.co.varsitycollege.serversamurai.flexforce.data.models

data class Challenge(
    val challengeId: String,
    val timePeriod: String,
    val challengeType: String,
    val tracking: String,
    val description: String
)