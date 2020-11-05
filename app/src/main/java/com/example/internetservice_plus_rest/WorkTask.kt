package com.example.internetservice_plus_rest

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkInfo
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
        //revision que no ande trabajando //Mutex
        Thread.sleep(3000)
        val context = applicationContext
        try {
            Log.i("WorkTask","En Espera")
            Toast.makeText(applicationContext, "WorkTask", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Result.retry()
        }
        return Result.success()

    }


}