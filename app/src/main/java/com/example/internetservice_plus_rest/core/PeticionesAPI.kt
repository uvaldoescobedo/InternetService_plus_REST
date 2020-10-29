package com.example.internetservice_plus_rest.core

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface PeticionesAPI {
    @POST("SincronizarClasificadores")
    fun getClasificadores(@Body ourdataSet : SyncDataSet ) :  Call<List<ClasificadorResponse>>

    @POST("SincronizarClasificadores")
    fun getActividades(@Body ourdataSet : SyncDataSet ) :  Call<List<ActividadesResponse>>

//     @POST("SincronizarClasificadores")
  //  fun getClasificadores(@Body ourdataSet : OurDataSet) :  Call<Any>
}