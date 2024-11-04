package za.co.varsitycollege.serversamurai.flexforce.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import za.co.varsitycollege.serversamurai.flexforce.data.models.FitnessEntryEntity
import za.co.varsitycollege.serversamurai.flexforce.data.models.UserEntity

@Dao
interface FitnessEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entry: FitnessEntryEntity)

    @Query("SELECT * FROM fitness_entries WHERE userEmail = :email ORDER BY dateSubmitted DESC LIMIT 1")
    fun getLatestEntryForUser(email: String): FitnessEntryEntity?

    @Query("SELECT * FROM fitness_entries WHERE userEmail = :email")
    fun getAllEntriesForUser(email: String): List<FitnessEntryEntity>
}