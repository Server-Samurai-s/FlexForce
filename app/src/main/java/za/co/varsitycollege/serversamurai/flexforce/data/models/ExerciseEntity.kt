package za.co.varsitycollege.serversamurai.flexforce.data.models
import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val sets: Int,
    val reps: Int,
    val equipment: String,
    val muscleGroup: String
) : Parcelable {
    // Implement Parcelable methods
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeInt(sets)
        parcel.writeInt(reps)
        parcel.writeString(equipment)
        parcel.writeString(muscleGroup)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<ExerciseEntity> {
        override fun createFromParcel(parcel: Parcel): ExerciseEntity {
            return ExerciseEntity(parcel)
        }

        override fun newArray(size: Int): Array<ExerciseEntity?> {
            return arrayOfNulls(size)
        }
    }
}