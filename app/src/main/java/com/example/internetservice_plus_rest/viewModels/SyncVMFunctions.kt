package com.example.internetservice_plus_rest.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.internetservice_plus_rest.core.ActividadesResponse
import com.example.internetservice_plus_rest.core.ClasificadorResponse
import com.example.internetservice_plus_rest.dataSource.SyncActividadesDataSource
import com.example.internetservice_plus_rest.dataSource.SyncClasificadoresDataSource
import com.example.internetservice_plus_rest.repository.DedecRepository
import com.example.internetservice_plus_rest.utils.SyncState

class SyncVMFunctions (
    private val repository: DedecRepository
): ViewModel(){
    lateinit var syncBadResponse : MutableLiveData<String>
    lateinit var synState : MutableLiveData<SyncState>

    // Sincronizar Clasificadores
    private lateinit var synClasificadoresDataSource : SyncClasificadoresDataSource
    lateinit var clasificadoresSuccessResponse: MutableLiveData<List<ClasificadorResponse>>

    // Sincronizar Actividades
    private lateinit var syncActividadesDataSource : SyncActividadesDataSource
    lateinit var actividadesSuccessResponse: MutableLiveData<List<ActividadesResponse>>



    fun syncClasificadores(serverKey:String,isError:Boolean){
        synClasificadoresDataSource = repository.requestSyncClasificadores(serverKey,isError)
        synState = synClasificadoresDataSource.sycState
        clasificadoresSuccessResponse = synClasificadoresDataSource.clasificadoresSuccessResponse
        syncBadResponse = synClasificadoresDataSource.clasificadoresBadResponse
    }
    fun syncActividades(serverKey:String,isError:Boolean){
        syncActividadesDataSource = repository.requestSyncActividades(serverKey,isError)
        synState = syncActividadesDataSource.sycState
        actividadesSuccessResponse = syncActividadesDataSource.actividadesSuccessResponse
        syncBadResponse = syncActividadesDataSource.syncBadResponse
    }

}