package com.example.internetservice_plus_rest

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.*
import java.lang.IllegalArgumentException
import java.util.concurrent.TimeUnit

class NetworkConnection(private val context:Context) : LiveData<Boolean>() {
    private var connectivityManager : ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private lateinit var statusTask : MutableLiveData<WorkInfo.State>
     var myPeriodicWorkRequest: PeriodicWorkRequest = PeriodicWorkRequest.Builder(WorkTask::class.java,5,TimeUnit.MINUTES).addTag("tarea_Work").build()


    override fun onActive() {
        super.onActive()
        postValue(false)
        updateConnection()
        when{
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                connectivityManager.registerDefaultNetworkCallback(connectivityManagerCallback())
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                lollipoopNetworRequest()
            }
            else -> {
                context.registerReceiver(
                    networkReciver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
                )
            }
        }
       startWorker()

    }

    fun startWorker() {
        Log.i("Tarea En Servicio ", "Anclada")
        //WorkManager.getInstance(context).enqueueUniquePeriodicWork("tarea_Work",ExistingPeriodicWorkPolicy.REPLACE,myPeriodicWorkRequest)
        WorkManager.getInstance(context).enqueue(myPeriodicWorkRequest)
    }
    fun stopWorker(){
        // se quita del workmanager la tarea
        Log.i("Main Tarea ","Se detiene Tarea")
        WorkManager.getInstance(context)
            .cancelAllWorkByTag(myPeriodicWorkRequest.id.toString())
    }

    override fun onInactive() {
        super.onInactive()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            connectivityManager.unregisterNetworkCallback(connectivityManagerCallback())
        }else{
            context.unregisterReceiver(networkReciver)
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun lollipoopNetworRequest(){
            var request = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
                connectivityManager.registerNetworkCallback(
                    request.build(),connectivityManagerCallback()
                )
    }

    private fun connectivityManagerCallback():ConnectivityManager.NetworkCallback{
        try {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                networkCallback = object : ConnectivityManager.NetworkCallback(){
                    override fun onLost(network: Network) {
                        super.onLost(network)
                        postValue(false)
                    }

                    override fun onAvailable(network: Network) {
                        super.onAvailable(network)
                        postValue(true)
                    }
                }
                return networkCallback
            }else{
                throw IllegalAccessError("Device Don't Support Internet Monitor")
            }
        }catch ( c: IllegalArgumentException){
            throw IllegalAccessError("Device Don't Support Internet Monitor")
        }

    }

    private val networkReciver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            updateConnection()
        }

    }

    private fun updateConnection(){
        val activeNetwok =  connectivityManager.activeNetworkInfo
       if(activeNetwok != null){
           postValue(activeNetwok.isConnected)
       }
    }



}