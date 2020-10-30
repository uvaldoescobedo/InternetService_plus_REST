package com.example.internetservice_plus_rest

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.work.Data
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.time.LocalDateTime

class SampleWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val currentDateTime = LocalDateTime.now()
        Log.i("Worked","Inicializado ")
           try {
               Log.i("Working", "Corriendo Tarea at $currentDateTime")

               return Result.success()
           }catch (e: InterruptedException){
               Log.i("W-Error",e.printStackTrace().toString())
               return Result.failure()
           }finally {
               Log.i("Working", "Tarea Terminada $currentDateTime")
           }
    }
}