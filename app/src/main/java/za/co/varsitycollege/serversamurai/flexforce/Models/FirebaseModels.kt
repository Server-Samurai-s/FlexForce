package za.co.varsitycollege.serversamurai.flexforce.Models

data class FitnessData(
    val goalWeight: Double,
    val bodyFatPercentage: Double,
    val heightCm: Double,
) {
    val bmi: Double
        get() = calculateBMI(goalWeight, heightCm)

    private fun calculateBMI(weight: Double, heightCm: Double): Double {
        val heightM = heightCm / 100
        return weight / (heightM * heightM)
    }
}