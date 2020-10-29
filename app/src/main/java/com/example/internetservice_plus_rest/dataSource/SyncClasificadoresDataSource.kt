package com.example.internetservice_plus_rest.dataSource

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.internetservice_plus_rest.core.*
import com.example.internetservice_plus_rest.utils.SynState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SyncClasificadoresDataSource(
        private val retrofitApi: PeticionesAPI,
        private val serverKey: String,
        private val error: Boolean
) {
    val sycState = MutableLiveData<SynState>()
    val clasificadoresSuccessResponse = MutableLiveData<List<ClasificadorResponse>>()
    val clasificadoresBadResponse = MutableLiveData<String>()

    init {
        requesSyncClasificadores()
        sycState.value = SynState.STARTING
    }

    private fun requesSyncClasificadores() {
        sycState.value = SynState.RUNNING

        retrofitApi.getClasificadores(OurDataSet(serverKey, error)).enqueue(object : Callback<List<ClasificadorResponse>> {
            override fun onFailure(call: Call<List<ClasificadorResponse>>, t: Throwable) {
                sycState.value = SynState.FAILED
                sycState.value = SynState.COMPLETE
            }

            override fun onResponse(call: Call<List<ClasificadorResponse>>, response: Response<List<ClasificadorResponse>>) {
                if (response.code() == 400 || response.code() == 500) {
                    sycState.value = SynState.FAILED
                    Log.v("Success WhitError", response.body().toString())
                    Log.v("Error code", response.code().toString())
                    clasificadoresBadResponse.value = response.message()
                } else {
                    sycState.value = SynState.SUCCESS
                    Log.v("Success", response.body().toString())
                    var c = response.body()
                    clasificadoresSuccessResponse.value = c
                }
                sycState.value = SynState.COMPLETE
            }
        })
    }
}