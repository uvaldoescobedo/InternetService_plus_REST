package com.example.internetservice_plus_rest

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import com.example.internetservice_plus_rest.core.RetrofitAPI
import com.example.internetservice_plus_rest.databinding.ActivityMainBinding
import com.example.internetservice_plus_rest.repository.DedecRepository
import com.example.internetservice_plus_rest.utils.SyncState
import com.example.internetservice_plus_rest.viewModels.SyncVMFunctions
import com.example.internetservice_plus_rest.viewModels.SyncVMPFactory
import kotlinx.android.synthetic.main.activity_main.view.*
import okhttp3.internal.Util
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    lateinit var modelFuntions: SyncVMFunctions
    lateinit var factory: SyncVMPFactory
    lateinit var networkConnection: NetworkConnection
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = SyncVMPFactory(DedecRepository(RetrofitAPI().getRetrofitApi()))
        modelFuntions = ViewModelProvider(this, factory!!).get(SyncVMFunctions::class.java)

        taskEvery5minutes()
        
        networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this, Observer { isConnected ->
            if (isConnected) {
                Toast.makeText(this, "Conectado", Toast.LENGTH_SHORT).show()
                binding.buttonSyncNow.isEnabled = true
                binding.buttonSyncNow.text = "Sincronizar Ahora"

            } else {
                Toast.makeText(this, "OFFLINE", Toast.LENGTH_SHORT).show()
                binding.buttonSyncNow.isEnabled = false
                binding.buttonSyncNow.text = "Acercate a Una zona con Internet\nPara Sincronizar"
            }
        })

        binding.progressBar.visibility = View.GONE


        binding.buttonSyncNow.setOnClickListener {
            if (networkConnection.value == true) {
                Toast.makeText(this, "Estas Conectado", Toast.LENGTH_SHORT).show()
                stopPeriodicWorking()

            } else {
                Toast.makeText(this, "Estas OFFLINE", Toast.LENGTH_SHORT).show()
            }
        }
    }
    lateinit var periodicWorkRequest:PeriodicWorkRequest

    private fun taskEvery5minutes() {
        //aqui se inicia la tarrea para que espere n tiempo e intente sincronizar
        var constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .build()
        periodicWorkRequest = PeriodicWorkRequest.Builder( SampleWorker::class.java,15,TimeUnit.MINUTES)
            .setConstraints(constraints)
            .addTag("TaskVeryDeviceIsConnected")
            .setInitialDelay(0,TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(this).enqueue(periodicWorkRequest)
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(periodicWorkRequest.id).observe(this, Observer {
            Log.i("WorkInfo ", it.state.toString())
            if(it.state == WorkInfo.State.RUNNING){
                //hasta en este momento, se lanza el metodo para preguntar si hay internet y si si, se inicia el bajado de la info
                startTaskForSync()
            }
        })

    }

    private fun stopPeriodicWorking(){
        WorkManager.getInstance(this).cancelAllWorkByTag(periodicWorkRequest.id.toString())
        startTaskForSync()
    }

    private fun observerData() {
        //observadores de la peticion de retrofit
        modelFuntions.synState.observe(this, Observer {
            Log.i("MainActivity synState ", it.toString())
            when (it) {
                SyncState.STARTING -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.buttonSyncNow.text = "Sincronizacion en Curso Espere"
                    binding.buttonSyncNow.isEnabled = false
                }
                SyncState.RUNNING -> {
                    binding.progressBar.isShown
                }
                SyncState.COMPLETE -> {
                    binding.progressBar.visibility = View.GONE
                    binding.buttonSyncNow.text = "Sincronizar Ahora"
                    binding.buttonSyncNow.isEnabled = true
                    taskEvery5minutes()
                }

            }
        })
        modelFuntions.syncBadResponse.observe(this, Observer {
            Log.i("MainActivity Bad ", it.toString())
        })

        modelFuntions.actividadesSuccessResponse.observe(this, Observer {
            Log.i("MainActivity activis ", it.toString())
        })

        modelFuntions.areasServiceSuccessResponse.observe(this, Observer {
            Log.i("MainActivity areas ", it.toString())
        })
        modelFuntions.clasificadoresSuccessResponse.observe(this, Observer {
            Log.i("MainActivity Clasif ", it.toString())
        })

        modelFuntions.checadoresSuccessResponse.observe(this, Observer {
            Log.i("MainActivity checks ", it.toString())
        })

    }

    private fun startTaskForSync() {

        if(networkConnection.value == true){
            // se mandan a llamar de manera paralela por ahora las sincrionizaciones del servicio

            modelFuntions.syncClasificadores("x9h3sl", false)

            modelFuntions.syncActividades("x9h3sl", false)

            modelFuntions.syncAreasServicio("x9h3sl", false)

            modelFuntions.syncChecadores("x9h3sl", false)

            observerData()
        }
    }
}