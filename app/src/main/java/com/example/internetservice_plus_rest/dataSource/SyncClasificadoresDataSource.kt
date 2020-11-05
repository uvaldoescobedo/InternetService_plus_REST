package com.example.internetservice_plus_rest.dataSource

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.internetservice_plus_rest.core.*
import com.example.internetservice_plus_rest.utils.SyncState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SyncClasificadoresDataSource(
        private val retrofitApi: PeticionesAPI,
        private val serverKey: String,
        private val error: Boolean
) {
    val sycState = MutableLiveData<SyncState>()
    val clasificadoresSuccessResponse = MutableLiveData<List<ClasificadorResponse>>()
    val clasificadoresBadResponse = MutableLiveData<String>()

    init {
        requesSyncClasificadores()
        sycState.value = SyncState.STARTING
    }

    private fun requesSyncClasificadores() {
        sycState.value = SyncState.RUNNING

        retrofitApi.getClasificadores(SyncDataSet(serverKey, error)).enqueue(object : Callback<List<ClasificadorResponse>> {
            override fun onFailure(call: Call<List<ClasificadorResponse>>, t: Throwable) {
                Log.v("Clasi Error code", t.message.toString())
                sycState.value = SyncState.FAILED
                sycState.value = SyncState.COMPLETE
            }

            override fun onResponse(call: Call<List<ClasificadorResponse>>, response: Response<List<ClasificadorResponse>>) {
                if (response.code() == 400 || response.code() == 500) {
                    sycState.value = SyncState.FAILED
                    Log.v("Clasi Success WhitError", response.body().toString())
                    Log.v("Clasi Error code", response.code().toString())
                    clasificadoresBadResponse.value = response.message()
                } else {
                    sycState.value = SyncState.SUCCESS
                    Log.v("Clasificadores Success", response.body().toString())
                    var c = response.body()
                    clasificadoresSuccessResponse.value = c
                }
                sycState.value = SyncState.COMPLETE
            }
        })
    }
}