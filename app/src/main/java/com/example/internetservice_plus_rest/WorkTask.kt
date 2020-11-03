package com.example.internetservice_plus_rest

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.internetservice_plus_rest.core.RetrofitAPI
import com.example.internetservice_plus_rest.repository.DedecRepository
import com.example.internetservice_plus_rest.viewModels.SyncVMFunctions
import com.example.internetservice_plus_rest.viewModels.SyncVMPFactory

class WorkTask(context: Context, workerParams: WorkerParameters) :Worker(context, workerParams){
    lateinit var modelFuntions: SyncVMFunctions
    lateinit var factory: SyncVMPFactory

    override fun doWork(): Result {
        Thread.sleep(3000)
        val context = applicationContext
        try {
            Log.i("WOrkTAsk","Ejecutada")
            IniciarSyncronizacion(context)
        } catch (e: Exception) {
            Result.retry()
        }
        return Result.success()

    }

    private fun IniciarSyncronizacion(context: Context) {
        factory = SyncVMPFactory(DedecRepository(RetrofitAPI().getRetrofitApi()))
        modelFuntions = ViewModelProvider(context, factory!!).get(SyncVMFunctions::class.java)

    }
}