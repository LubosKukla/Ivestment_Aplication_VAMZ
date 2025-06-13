package com.example.ivestmentaplicationvamz.data

import kotlinx.coroutines.flow.Flow

class InvestmentRepository(
    private val dao: InvestmentDao
) {
    suspend fun insert(inv: InvestmentEntity): Long =
        dao.insert(inv)

    fun getAll(): Flow<List<InvestmentEntity>> =
        dao.getAll()

    suspend fun delete(inv: InvestmentEntity): Int =
        dao.delete(inv)
}
