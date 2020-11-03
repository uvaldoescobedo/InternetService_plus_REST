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
import com.example.internetservice_plus_rest.viewModels.SyncVMFunctions
import com.example.internetservice_plus_rest.viewModels.SyncVMPFactory
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private var hayErroresSync: Boolean = false
    lateinit var modelFuntions: SyncVMFunctions
    lateinit var factory: SyncVMPFactory
  //  lateinit var networkConnection: NetworkConnection
    lateinit var binding: ActivityMainBinding
    lateinit var serviceBroadcast:BroadcastReceiver
    var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var i =Intent(this,CheckNetwork::class.java)
        startService(i)
         serviceBroadcast = object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                Toast.makeText(applicationContext, "from Service "+ intent?.extras?.get("status"), Toast.LENGTH_SHORT).show()
            }
        }
        this.registerReceiver(serviceBroadcast, IntentFilter("DataService"))




        factory = SyncVMPFactory(DedecRepository(RetrofitAPI().getRetrofitApi()))
        modelFuntions = ViewModelProvider(this, factory!!).get(SyncVMFunctions::class.java)

        //Observadore dela connecxion a internet cambia el status del livedata segun estemos conectados
       // CheckNetwork.networkConnection = NetworkConnection(applicationContext)
        observadoresNetWorService()
        binding.progressBar.visibility = View.GONE

        binding.buttonSyncNow.setOnClickListener {
            if (CheckNetwork.networkConnection.value == true) {
                Toast.makeText(this, "Estas Conectado", Toast.LENGTH_SHORT).show()
                stopPeriodicWorking()

            } else {
                Toast.makeText(this, "Estas OFFLINE", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observadoresNetWorService() {
        CheckNetwork.networkConnection.observe(this, Observer { isConnected ->
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
        //observer de la tarea en el servicio
        WorkManager.getInstance(this)
            .getWorkInfoByIdLiveData(CheckNetwork.networkConnection.myPeriodicWorkRequest.id)
            .observe(this, Observer {
                Log.i("Tarea En Servicio Main ", it.state.toString())
                if (it.state == WorkInfo.State.RUNNING) {
                    //hasta en este momento, se lanza el metodo para preguntar si hay internet y si si, se inicia el bajado de la info
                    startTaskForSync()
                }
            })
    }

    private fun stopPeriodicWorking() {
        CheckNetwork.networkConnection.stopWorker()
        startTaskForSync()
    }

    private fun startTaskForSync() {

        if (CheckNetwork.networkConnection.value == true) {
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
                        CheckNetwork.networkConnection.startWorker() // hay que moverlo a otro lado cuando todas las Sync son completadas correctamente
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
                escribirenBase()
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
        if(!hayErroresSync && count ==4){
           Toast.makeText(applicationContext,"Todo bien se escriben en base de datos",Toast.LENGTH_SHORT).show()
        }
    }
}