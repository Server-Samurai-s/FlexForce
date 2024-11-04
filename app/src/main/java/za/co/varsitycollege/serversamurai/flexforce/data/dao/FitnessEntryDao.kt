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
    fun insert(fitnessEntry: FitnessEntryEntity)

    @Query("SELECT * FROM FitnessEntryEntity ORDER BY dateSubmitted DESC LIMIT 1")
    fun getLatestEntry(): FitnessEntryEntity?

}