package com.example.campbell.service

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.campbell.model.Camp

@Database(entities = arrayOf(Camp::class), version = 1)
abstract class CampDatabase : RoomDatabase() {

    abstract fun campDAo(): CampDao
    //singelton yapısı kullanılacak

    companion object {
        @Volatile
        private var instance: CampDatabase? = null

        private val lock = Any()

        /** instance var mı yok mu kontrol ettik eğer yoksa
        senkronize bir şekilde instance oluşturuluyor */

        operator fun invoke(context: Context) = instance ?: synchronized(lock) {
            instance ?: makeDatabase(context).also {
                instance = it
            }
        }

        private fun makeDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext, CampDatabase::class.java, "countrydatabase"
        ).build()
    }

}