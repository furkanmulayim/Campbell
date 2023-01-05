package com.campbell.campbell.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Camp(

    @ColumnInfo(name = "name")
    @SerializedName("name")
    val campCountryName: String?,

    @ColumnInfo(name = "comment")
    @SerializedName("comment")
    val campAciklamasi: String?,

    @ColumnInfo(name = "ulasim")
    @SerializedName("ulasim")
    val ulasim: String?,

    @ColumnInfo(name = "olanak")
    @SerializedName("olanak")
    val olanak: String?,

    @ColumnInfo(name = "resimi")
    @SerializedName("resimi")
    val pngUrl: String?,

    @ColumnInfo(name = "enlem")
    @SerializedName("enlem")
    val enlem: String?,

    @ColumnInfo(name = "boylam")
    @SerializedName("boylam")
    val boylam: String?
) : Parcelable {

    @PrimaryKey(autoGenerate = true)
    var uuid: Int = 0

}