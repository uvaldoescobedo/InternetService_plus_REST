package com.example.internetservice_plus_rest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import com.example.internetservice_plus_rest.viewModels.CheckWifiState
import com.example.internetservice_plus_rest.viewModels.SyncVMFunctions
import com.example.internetservice_plus_rest.viewModels.SyncVMPFactory
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private var hayErroresSync: Boolean = false
    lateinit var modelFuntions: SyncVMFunctions
    lateinit var factory: SyncVMPFactory
    lateinit var networkConnection: CheckWifiState
    lateinit var binding: ActivityMainBinding
    var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        networkConnection=  CheckWifiState.getInstance(applicationContext)
        factory = SyncVMPFactory(DedecRepository(RetrofitAPI().getRetrofitApi()))
        modelFuntions = ViewModelProvider(this, factory!!).get(SyncVMFunctions::class.java)

        //Observadore dela connecxion a internet cambia el status del livedata segun estemos conectados
        observadoresNetWorService()

        binding.progressBar.visibility = View.GONE

    }

    private fun observadoresNetWorService() {
        networkConnection.observe(this, Observer { isConnected ->
            if (isConnected) {
                Log.i("Main NetworkConnection ", "Conectado")
                Toast.makeText(this, "Conectado", Toast.LENGTH_SHORT).show()
                binding.txtSalidaInfoMain.text = "Esperando Sincronizar"

            } else {
                Log.i("Main NetworConnection ", "OFFLINE")
                Toast.makeText(this, "OFFLINE", Toast.LENGTH_SHORT).show()
                binding.txtSalidaInfoMain.text = "Acercate a Una zona con Internet\nPara Sincronizar"
            }
        })

        //observer de la tarea en el servicio
        WorkManager.getInstance(this)
            .getWorkInfoByIdLiveData(networkConnection.myPeriodicWorkRequest.id)
            .observe(this, Observer {
                Log.i("Tarea En Servicio Main ", it.state.toString())
                if (it.state == WorkInfo.State.RUNNING) {
                    binding.txtSalidaInfoMain.text = "Sincronizacion en Curso"
                    //hasta en este momento, se lanza el metodo para preguntar si hay internet y si si, se inicia el bajado de la info
                    stopPeriodicWorking()
                }
            })
    }
    private fun stopPeriodicWorking() {
        networkConnection.stopWorker()
        startTaskForSync()
    }



    private fun startTaskForSync() {

        if (networkConnection.value == true) {
            Log.i("MainActivity synState ", "Iniciando")
            // se mandan a llamar de manera paralela por ahora las sincrionizaciones del servicio
            //observadores de la peticion de retrofit
            count = 0
            modelFuntions.syncClasificadores("x9h3sl", false)
            modelFuntions.syncActividades("x9h3sl", false)
            modelFuntions.syncAreasServicio("x9h3sl", false)
            modelFuntions.syncChecadores("x9h3sl", false)

            modelFuntions.synState.observe(this, Observer {
                Log.i("MainActivity synState ", it.toString())
                when (it) {
                    SyncState.STARTING -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.txtSalidaInfoMain.text = "Sincronizacion en Curso Espere"

                    }
                    SyncState.RUNNING -> {
                        binding.progressBar.isShown
                    }
                    SyncState.COMPLETE -> {
                        binding.progressBar.visibility = View.GONE
                        binding.txtSalidaInfoMain.text = "Sincronizar Ahora"
                        escribirenBase()
                    }

                }
            })

            modelFuntions.clasificadoresSuccessResponse.observe(this, Observer {
                Log.i("1/4 SyncClasificadores ", it.toString())
                if(it.isNotEmpty()){
                 count++
                }
            })
            modelFuntions.actividadesSuccessResponse.observe(this, Observer {
                Log.i("2/4 SyncActividades ", it.toString())
                if(it.isNotEmpty()){
                    count++
                }
            })

            modelFuntions.areasServiceSuccessResponse.observe(this, Observer {
                Log.i("3/4 SyncAreas ", it.toString())
                if(it.isNotEmpty()){
                    count++
                }
            })
            modelFuntions.checadoresSuccessResponse.observe(this, Observer {
                Log.i("4/4 SyncChecadores ", it.toString())

                if(it.isNotEmpty()){
                    count++
                }
            })

            modelFuntions.syncBadResponse.observe(this, Observer {
                Log.i("MainActivity Bad ", it.toString())
               if(it.isNotEmpty()){
                   hayErroresSync = true
               }
            })
        }
    }

    private fun escribirenBase() {
        networkConnection.startWorker()
        Log.i("Main escribirenBase es 4? ", count.toString())
        if(!hayErroresSync && count ==4){
         //   Thread.sleep(7000)
           Toast.makeText(applicationContext,"Todo bien se escriben en base de datos",Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(applicationContext,"Error Intente de Nuevo",Toast.LENGTH_SHORT).show()
        }
    }
}