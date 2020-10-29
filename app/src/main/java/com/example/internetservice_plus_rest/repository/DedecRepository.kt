package com.example.internetservice_plus_rest.repository

import com.example.internetservice_plus_rest.core.PeticionesAPI
import com.example.internetservice_plus_rest.dataSource.SyncClasificadoresDataSource

class DedecRepository(val retrofitApi: PeticionesAPI) {

    fun requestSyncClasificadores(serverKey:String, isError:Boolean): SyncClasificadoresDataSource {

        return SyncClasificadoresDataSource(retrofitApi, serverKey, isError)
    }
}