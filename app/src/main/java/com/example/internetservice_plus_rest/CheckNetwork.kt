package com.example.internetservice_plus_rest

import android.app.IntentService
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import androidx.work.WorkManager

class CheckNetwork : Service() {

    companion object{
        lateinit var networkConnection: NetworkConnection
        var s = MutableLiveData<Boolean>()
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
      //  Toast.makeText(applicationContext,"Hola desde Servicio", Toast.LENGTH_SHORT).show()
        s.value = networkConnection.value
        setStatusToActivity()
        super.onCreate()
    }


    fun setStatusToActivity(){
        var intentOut = Intent("DataService")
        intentOut.putExtra("status",s.value)
        sendBroadcast(intentOut)
    }
}