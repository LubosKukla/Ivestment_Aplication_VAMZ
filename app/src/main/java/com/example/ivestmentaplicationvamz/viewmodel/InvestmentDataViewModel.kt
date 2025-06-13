package com.example.ivestmentaplicationvamz.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ivestmentaplicationvamz.data.AppDatabase
import com.example.ivestmentaplicationvamz.data.InvestmentEntity
import com.example.ivestmentaplicationvamz.data.InvestmentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InvestmentDataViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = InvestmentRepository(
        AppDatabase.getInstance(application).investmentDao()
    )


    suspend fun insert(inv: InvestmentEntity): Long =
        repository.insert(inv)


    val allInvestments: StateFlow<List<InvestmentEntity>> =
        repository.getAll()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    fun delete(inv: InvestmentEntity, onComplete: (Int) -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val count = repository.delete(inv)
            withContext(Dispatchers.Main) { onComplete(count) }
        }
    }
}
