package com.example.ivestmentaplicationvamz.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface InvestmentDao {
    @Insert
    fun insert(inv: InvestmentEntity): Long

    @Query("SELECT * FROM investments ORDER BY timestamp DESC")
    fun getAll(): Flow<List<InvestmentEntity>>

    @Delete
    fun delete(inv: InvestmentEntity): Int
}
