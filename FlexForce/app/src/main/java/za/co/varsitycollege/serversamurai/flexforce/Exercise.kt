package za.co.varsitycollege.serversamurai.flexforce

import android.os.Parcel
import android.os.Parcelable

data class Exercise(
    val name: String,
    val sets: Int,
    val reps: Int,
    val muscleGroup: String,
    val equipment: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(sets)
        parcel.writeInt(reps)
        parcel.writeString(muscleGroup)
        parcel.writeString(equipment)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Exercise> {
        override fun createFromParcel(parcel: Parcel): Exercise {
            return Exercise(parcel)
        }

        override fun newArray(size: Int): Array<Exercise?> {
            return arrayOfNulls(size)
        }
    }
}
