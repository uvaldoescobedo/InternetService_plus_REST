package com.example.internetservice_plus_rest.dataSource

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.internetservice_plus_rest.core.*
import com.example.internetservice_plus_rest.utils.SyncState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SyncActividadesDataSource(
        private val retrofitApi: PeticionesAPI,
        private val serverKey: String,
        private val error: Boolean
) {
    val sycState = MutableLiveData<SyncState>()
    val actividadesSuccessResponse = MutableLiveData<List<ActividadesResponse>>()
    val syncBadResponse = MutableLiveData<String>()

    init {
        requesSyncClasificadores()
        sycState.value = SyncState.STARTING
    }

    private fun requesSyncClasificadores() {
        sycState.value = SyncState.RUNNING

        retrofitApi.getActividades(SyncDataSet(serverKey, error)).enqueue(object : Callback<List<ActividadesResponse>> {
            override fun onFailure(call: Call<List<ActividadesResponse>>, t: Throwable) {
                sycState.value = SyncState.FAILED
                sycState.value = SyncState.COMPLETE
            }

            override fun onResponse(call: Call<List<ActividadesResponse>>, response: Response<List<ActividadesResponse>>) {
                if (response.code() == 400 || response.code() == 500) {
                    sycState.value = SyncState.FAILED
                    Log.v("ActiviSuccess WhitError", response.body().toString())
                    Log.v("Error code", response.code().toString())
                    syncBadResponse.value = response.message()
                } else {
                    sycState.value = SyncState.SUCCESS
                    Log.v("Actividades Success", response.body().toString())
                    var c = response.body()
                    actividadesSuccessResponse.value = c
                }
                sycState.value = SyncState.COMPLETE
            }
        })
    }
}