package za.co.varsitycollege.serversamurai.flexforce.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "fitness_entries")
data class FitnessEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String,  // Add this field
    val currentBodyFat: Double,
    val currentWeight: Double,
    val dateSubmitted: String,
    val height: Double
)