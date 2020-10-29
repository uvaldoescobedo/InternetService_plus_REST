package com.example.internetservice_plus_rest.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.internetservice_plus_rest.repository.DedecRepository

class SyncVMPFactory (var repository: DedecRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SyncVMFunctions(repository) as T
    }
}