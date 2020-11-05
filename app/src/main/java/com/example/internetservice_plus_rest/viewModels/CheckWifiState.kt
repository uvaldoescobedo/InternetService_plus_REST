package com.example.internetservice_plus_rest.viewModels

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.internetservice_plus_rest.WorkTask
import java.lang.IllegalArgumentException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class CheckWifiState(private val context: Context) : LiveData<Boolean>() {
    private var connectivityManager:ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    var myPeriodicWorkRequest: PeriodicWorkRequest = PeriodicWorkRequest.Builder(
    WorkTask::class.java, 5,
    TimeUnit.MINUTES
    ).addTag("tarea_Work").build()

    init {
        checkNotNull(context)
        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    companion object{
        private var INSTANCE: CheckWifiState? = null

        fun getInstance(context: Context):CheckWifiState{
            if(INSTANCE == null){
                INSTANCE = CheckWifiState(context)
            }
            return INSTANCE!!
        }
    }

    override fun onActive() {
        super.onActive()
        postValue(false)
        Log.i("Tarea En Servicio ", "INICIADA")
        connectivityManager.registerDefaultNetworkCallback(connectivityManagerCallback())
        startWorker()
    }

    override fun onInactive() {
        super.onInactive()
        Log.i("Tarea En Servicio ", "INACTIVA")
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    fun startWorker() {
        Log.i("Tarea En Servicio ", "REINICIADA")
        // WorkManager.getInstance(context).enqueueUniquePeriodicWork("tarea_Work",ExistingPeriodicWorkPolicy.REPLACE,myPeriodicWorkRequest)
        WorkManager.getInstance(context).enqueue(myPeriodicWorkRequest)
    }

    fun stopWorker() {
        // se quita del workmanager la tarea
        Log.i("Tarea En Servicio ", "DETENIDA")
        WorkManager.getInstance(context)
            .cancelAllWorkByTag(myPeriodicWorkRequest.id.toString())
    }

    private fun connectivityManagerCallback(): ConnectivityManager.NetworkCallback {
        try {
            networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onLost(network: Network) {
                    super.onLost(network)
                    Log.i("TCheckWifiState ", "CAMBIO DE RED")
                    postValue(false)
                }
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    Log.i("TCheckWifiState ", "CAMBIO DE RED")
                    postValue(true)
                }
            }
            return networkCallback

        } catch (c: IllegalArgumentException) {
            throw IllegalAccessError("Device Don't Support Internet Monitor")
        }
    }
}