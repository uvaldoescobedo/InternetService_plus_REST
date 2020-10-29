package com.example.internetservice_plus_rest.core

import com.google.gson.annotations.SerializedName

data class ClasificadorResponse (

    @SerializedName("Id") val id : String,
    @SerializedName("Nombre") val nombre : String,
    @SerializedName("Usuario") val usuario : String,
    @SerializedName("Password") val password : String
)

data class ActividadesResponse (

        @SerializedName("Id") val id : String,
        @SerializedName("IdTabuladorDefault") val idTabuladorDefault : String,
        @SerializedName("IdAreaServicio") val idAreaServicio : String,
        @SerializedName("CodigoArea") val codigoArea : String,
        @SerializedName("Descripcion") val descripcion : String,
        @SerializedName("RequiereAutorizacion") val requiereAutorizacion : Boolean,
        @SerializedName("CantidadDecimales") val cantidadDecimales : Int,
        @SerializedName("UnidadVelocidad") val unidadVelocidad : String,
        @SerializedName("VelocidadEstandar") val velocidadEstandar : Double,
        @SerializedName("SemaforoAmarillo") val semaforoAmarillo : Double,
        @SerializedName("SemaforoVerde") val semaforoVerde : Double,
        @SerializedName("CapturaDestajo") val capturaDestajo : Boolean,
        @SerializedName("FechaTransplante") val fechaTransplante : String
)

data class AreasServicioResponse (

    @SerializedName("Id") val id : String,
    @SerializedName("CodigoBarras") val codigoBarras : String,
    @SerializedName("Descripcion") val descripcion : String,
    @SerializedName("Tabla") val tabla : String,
    @SerializedName("NumeroProyecto") val numeroProyecto : String
)