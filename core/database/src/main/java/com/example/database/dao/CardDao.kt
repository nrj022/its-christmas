package com.example.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.database.entity.CardEntity

@Dao
interface CardDao {
    @Query("SELECT * FROM cards")
    fun getAllCards(): List<CardEntity>
}