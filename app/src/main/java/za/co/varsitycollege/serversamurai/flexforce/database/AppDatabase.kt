package za.co.varsitycollege.serversamurai.flexforce.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import za.co.varsitycollege.serversamurai.flexforce.service.Converters
import za.co.varsitycollege.serversamurai.flexforce.data.models.User
import za.co.varsitycollege.serversamurai.flexforce.data.dao.UserDao
import za.co.varsitycollege.serversamurai.flexforce.data.dao.WorkoutDao
import za.co.varsitycollege.serversamurai.flexforce.data.models.WorkoutEntity

@Database(entities = [User::class, WorkoutEntity::class], version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun workoutDao(): WorkoutDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS WorkoutEntity (
                        id INTEGER PRIMARY KEY NOT NULL,
                        workoutName TEXT NOT NULL,
                        duration INTEGER NOT NULL,
                        caloriesBurned INTEGER NOT NULL
                    )
                    """
                )
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}