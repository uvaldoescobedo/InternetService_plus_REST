package com.example.internetservice_plus_rest.core

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface PeticionesAPI {
    @POST("SincronizarClasificadores")
    fun getClasificadores(@Body ourdataSet : OurDataSet ) :  Call<List<ClasificadorResponse>>

//    @POST("SincronizarClasificadores")
  //  fun getClasificadores(@Body ourdataSet : OurDataSet) :  Call<Any>
}