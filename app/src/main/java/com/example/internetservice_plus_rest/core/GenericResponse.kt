package com.example.internetservice_plus_rest.core

import com.google.gson.annotations.SerializedName

data class ClasificadorResponse (
    @SerializedName("Id") val id : String,
    @SerializedName("Nombre") val nombre : String,
    @SerializedName("Usuario") val usuario : String,
    @SerializedName("Password") val password : String
)
