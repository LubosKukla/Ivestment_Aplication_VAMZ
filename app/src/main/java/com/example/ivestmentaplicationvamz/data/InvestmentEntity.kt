package com.example.ivestmentaplicationvamz.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "investments")
data class InvestmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val principal: Double,
    val contribution: Double,
    val years: Int,
    val ratePercent: Double,
    val frequency: String,
    val timestamp: String,
    val simulationEnabled: Boolean,
    val inflationEnabled: Boolean,
    val inflationRate: Double?,
    val taxEnabled: Boolean,
    val taxRate: Double?
)
