package za.co.varsitycollege.serversamurai.flexforce.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class FitnessEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Default value for id
    val currentBodyFat: Double,
    val currentWeight: Double,
    val dateSubmitted: String,
    val height: Double,
)