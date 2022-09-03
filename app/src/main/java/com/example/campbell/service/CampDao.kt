package com.example.campbell.service

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.campbell.model.Camp

@Dao
interface CampDao {
    /** Data Access Object */
    @Insert
    suspend fun insertAll(vararg camp: Camp): List<Long>

    @Query("SELECT * FROM camp")
    suspend fun getAllCamp(): List<Camp>

    @Query("SELECT * FROM camp WHERE uuid = :campId")
    suspend fun getCamp(campId: Int): Camp

    @Query("DELETE FROM camp")
    suspend fun deleteAll()

}