package za.co.varsitycollege.serversamurai.flexforce.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import za.co.varsitycollege.serversamurai.flexforce.data.dao.ExerciseDao
import za.co.varsitycollege.serversamurai.flexforce.data.dao.FitnessEntryDao
import za.co.varsitycollege.serversamurai.flexforce.data.dao.GoalDao
import za.co.varsitycollege.serversamurai.flexforce.service.Converters
import za.co.varsitycollege.serversamurai.flexforce.data.models.UserEntity
import za.co.varsitycollege.serversamurai.flexforce.data.dao.UserDao
import za.co.varsitycollege.serversamurai.flexforce.data.dao.WorkoutDao
import za.co.varsitycollege.serversamurai.flexforce.data.models.ExerciseEntity
import za.co.varsitycollege.serversamurai.flexforce.data.models.WorkoutEntity
import za.co.varsitycollege.serversamurai.flexforce.data.models.GoalEntity
import za.co.varsitycollege.serversamurai.flexforce.data.models.FitnessEntryEntity

@Database(
    entities = [UserEntity::class, GoalEntity::class, FitnessEntryEntity::class, WorkoutEntity::class, ExerciseEntity::class],
    version = 6,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun fitnessEntryDao(): FitnessEntryDao
    abstract fun goalDao(): GoalDao
    abstract fun exerciseDao(): ExerciseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "flexforce-database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate database in the background
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.let { database ->
                                    prepopulateDatabase(database.exerciseDao())
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun prepopulateDatabase(exerciseDao: ExerciseDao) {
            val exercises = listOf(
                ExerciseEntity(
                    name = "Bench Press",
                    sets = 3,
                    reps = 12,
                    equipment = "Barbell",
                    muscleGroup = "Chest"
                ),
                ExerciseEntity(
                    name = "Squats",
                    sets = 4,
                    reps = 10,
                    equipment = "Barbell",
                    muscleGroup = "Legs"
                ),
                ExerciseEntity(
                    name = "Deadlift",
                    sets = 3,
                    reps = 8,
                    equipment = "Barbell",
                    muscleGroup = "Back"
                ),
                ExerciseEntity(
                    name = "Pull-ups",
                    sets = 3,
                    reps = 10,
                    equipment = "Bodyweight",
                    muscleGroup = "Back"
                ),
                ExerciseEntity(
                    name = "Push-ups",
                    sets = 3,
                    reps = 15,
                    equipment = "Bodyweight",
                    muscleGroup = "Chest"
                ),
                ExerciseEntity(
                    name = "Shoulder Press",
                    sets = 3,
                    reps = 12,
                    equipment = "Dumbbells",
                    muscleGroup = "Shoulders"
                )
            )
            exerciseDao.insertAll(exercises)
        }
    }
}
