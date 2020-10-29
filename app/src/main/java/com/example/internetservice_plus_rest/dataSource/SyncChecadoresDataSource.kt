package com.example.internetservice_plus_rest.dataSource

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.internetservice_plus_rest.core.*
import com.example.internetservice_plus_rest.utils.SyncState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SyncChecadoresDataSource(
        private val retrofitApi: PeticionesAPI,
        private val serverKey: String,
        private val error: Boolean
) {
    val sycState = MutableLiveData<SyncState>()
    val checadoresSuccessResponse = MutableLiveData<List<ChecadoresResponse>>()
    val syncBadResponse = MutableLiveData<String>()

    init {
        requesSyncClasificadores()
        sycState.value = SyncState.STARTING
    }

    private fun requesSyncClasificadores() {
        sycState.value = SyncState.RUNNING

        retrofitApi.getChecadores(SyncDataSet(serverKey, error)).enqueue(object : Callback<List<ChecadoresResponse>> {
            override fun onFailure(call: Call<List<ChecadoresResponse>>, t: Throwable) {
                sycState.value = SyncState.FAILED
                sycState.value = SyncState.COMPLETE
            }

            override fun onResponse(call: Call<List<ChecadoresResponse>>, response: Response<List<ChecadoresResponse>>) {
                if (response.code() == 400 || response.code() == 500) {
                    sycState.value = SyncState.FAILED
                    Log.v("Checadores WhitError", response.body().toString())
                    Log.v("Error code", response.code().toString())
                    syncBadResponse.value = response.message()
                } else {
                    sycState.value = SyncState.SUCCESS
                    Log.v("Checadores Success", response.body().toString())
                    var c = response.body()
                    checadoresSuccessResponse.value = c
                }
                sycState.value = SyncState.COMPLETE
            }
        })
    }
}