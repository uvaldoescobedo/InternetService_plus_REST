package com.example.internetservice_plus_rest.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.internetservice_plus_rest.core.ClasificadorResponse
import com.example.internetservice_plus_rest.dataSource.SyncClasificadoresDataSource
import com.example.internetservice_plus_rest.repository.DedecRepository
import com.example.internetservice_plus_rest.utils.SynState

class SyncVMFunctions (
    private val repository: DedecRepository
): ViewModel(){
    // Sincronizar Clasificadores
    private lateinit var synClasificadoresDataSource : SyncClasificadoresDataSource
    lateinit var synState : MutableLiveData<SynState>
    lateinit var clasificadoresSuccessResponse: MutableLiveData<List<ClasificadorResponse>>
    lateinit var clasificadoresBadResponse : MutableLiveData<String>


    fun syncClasificadores(serverKey:String,isError:Boolean){
        synClasificadoresDataSource = repository.requestSyncClasificadores(serverKey,isError)
        synState=synClasificadoresDataSource.sycState
        clasificadoresSuccessResponse= synClasificadoresDataSource.clasificadoresSuccessResponse
        clasificadoresBadResponse = synClasificadoresDataSource.clasificadoresBadResponse
    }

}