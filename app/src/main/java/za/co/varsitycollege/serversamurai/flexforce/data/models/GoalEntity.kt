package za.co.varsitycollege.serversamurai.flexforce.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Default value for id
    val dateSet: String,
    val goalBodyFat: Double,
    val goalWeight: Double,
)