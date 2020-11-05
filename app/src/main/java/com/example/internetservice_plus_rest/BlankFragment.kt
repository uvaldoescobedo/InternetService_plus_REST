package com.example.internetservice_plus_rest

import android.content.BroadcastReceiver
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.internetservice_plus_rest.core.RetrofitAPI
import com.example.internetservice_plus_rest.databinding.ActivityMainBinding
import com.example.internetservice_plus_rest.databinding.FragmentBlankBinding
import com.example.internetservice_plus_rest.repository.DedecRepository
import com.example.internetservice_plus_rest.utils.SyncState
import com.example.internetservice_plus_rest.viewModels.CheckWifiState
import com.example.internetservice_plus_rest.viewModels.SyncVMFunctions
import com.example.internetservice_plus_rest.viewModels.SyncVMPFactory

class BlankFragment : Fragment() {
    lateinit var binding : FragmentBlankBinding
    lateinit var networkConnection: CheckWifiState

    private var hayErroresSync: Boolean = false
    lateinit var modelFuntions: SyncVMFunctions
    lateinit var factory: SyncVMPFactory

    var count = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBlankBinding.inflate(layoutInflater)
        binding.fProgressBar.visibility = View.GONE
        networkConnection=  CheckWifiState.getInstance(activity!!.applicationContext)
        factory = SyncVMPFactory(DedecRepository(RetrofitAPI().getRetrofitApi()))
        modelFuntions = ViewModelProvider(this, factory!!).get(SyncVMFunctions::class.java)
        //Observadore dela connecxion a internet cambia el status del livedata segun estemos conectados
        observadoresNetWorService()

        binding.fButtonSyncNow.setOnClickListener {
            if (networkConnection.value == true) {
                 // Toast.makeText(activity!!.applicationContext, "Estas Conectado", Toast.LENGTH_SHORT).show()
                Log.i("NetworkConnection ", "Estas Conectado")
                stopPeriodicWorking()

            } else {
                Log.i("NetworkConnection ", "Estas OFFLINE")
              //  Toast.makeText(activity!!.applicationContext, "Estas OFFLINE", Toast.LENGTH_SHORT).show()
            }
        }




        return binding.root//inflater.inflate(R.layout.fragment_blank, container, false)
    }

    private fun stopPeriodicWorking() {
        networkConnection.stopWorker()
        startTaskForSync()
    }

    private fun startTaskForSync() {

        if (networkConnection.value == true) {
            Log.i("BlankFragment synState ", "Iniciando")
            // se mandan a llamar de manera paralela por ahora las sincrionizaciones del servicio
            //observadores de la peticion de retrofit
            count = 0
            modelFuntions.syncClasificadores("x9h3sl", false)
            modelFuntions.syncActividades("x9h3sl", false)
            modelFuntions.syncAreasServicio("x9h3sl", false)
            modelFuntions.syncChecadores("x9h3sl", false)

            modelFuntions.synState.observe(this, Observer {
                Log.i("BlankFragment synState ", it.toString())
                when (it) {
                    SyncState.STARTING -> {
                        binding.fProgressBar.visibility = View.VISIBLE
                        binding.fButtonSyncNow.isEnabled = false
                        binding.fButtonSyncNow.text = "Sincronizacion en Curso Espere"

                    }
                    SyncState.RUNNING -> {
                        binding.fProgressBar.isShown
                    }
                    SyncState.COMPLETE -> {
                        binding.fButtonSyncNow.isEnabled = true
                        binding.fProgressBar.visibility = View.GONE
                        binding.fButtonSyncNow.text = "Sincronizar Ahora"
                        escribirenBase()
                    }

                }
            })

            modelFuntions.clasificadoresSuccessResponse.observe(viewLifecycleOwner, Observer {
                Log.i("1/4 SyncClasificadores ", it.toString())
                if(it.isNotEmpty()){
                    count++
                }
            })
            modelFuntions.actividadesSuccessResponse.observe(viewLifecycleOwner, Observer {
                Log.i("2/4 SyncActividades ", it.toString())
                if(it.isNotEmpty()){
                    count++
                }
            })

            modelFuntions.areasServiceSuccessResponse.observe(viewLifecycleOwner, Observer {
                Log.i("3/4 SyncAreas ", it.toString())
                if(it.isNotEmpty()){
                    count++
                }
            })
            modelFuntions.checadoresSuccessResponse.observe(viewLifecycleOwner, Observer {
                Log.i("4/4 SyncChecadores ", it.toString())

                if(it.isNotEmpty()){
                    count++
                }
            })

            modelFuntions.syncBadResponse.observe(viewLifecycleOwner, Observer {
                Log.i("BlankFragment Bad ", it.toString())
                if(it.isNotEmpty()){
                    hayErroresSync = true
                }
            })
        }


    }

    private fun escribirenBase() {
        networkConnection.startWorker()
        Log.i("FRAG escribirenBase es 4? ", count.toString())
        if(!hayErroresSync && count ==4){
            //   Thread.sleep(7000)
            Toast.makeText(activity!!.applicationContext,"Todo bien se escriben en base de datos",Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(activity!!.applicationContext,"Error Intente de Nuevo",Toast.LENGTH_SHORT).show()
        }
    }


    private fun observadoresNetWorService() {
        networkConnection.observe(this, Observer { isConnected ->
            if (isConnected) {
                Log.i("FRAG NetworkConnection ", "Conectado")
                Toast.makeText(activity!!.applicationContext, "Conectado", Toast.LENGTH_SHORT).show()
                binding.fButtonSyncNow.isEnabled = true
                binding.fButtonSyncNow.text = "Sincronizar Ahora"


            } else {
                Log.i("FRAG NetworConnection ", "OFFLINE")
                Toast.makeText(activity!!.applicationContext, "OFFLINE", Toast.LENGTH_SHORT).show()
                binding.fButtonSyncNow.isEnabled = false
                binding.fButtonSyncNow.text = "Acercate a Una zona con Internet\nPara Sincronizar"
            }
        })

        //observer de la tarea en el servicio
        WorkManager.getInstance(activity!!.applicationContext)
            .getWorkInfoByIdLiveData(networkConnection.myPeriodicWorkRequest.id)
            .observe(viewLifecycleOwner, Observer {
              //  Log.i("Tarea En Servicio FRAG ", it.state.toString())
                if (it.state == WorkInfo.State.RUNNING) {
                    //hasta en este momento, se lanza el metodo para preguntar si hay internet y si si, se inicia el bajado de la info
                    binding.fButtonSyncNow.isEnabled = false
                    binding.fButtonSyncNow.text = "Hay Una Sincronizacion en Curso"
                }else{
                    binding.fButtonSyncNow.isEnabled = true
                    binding.fButtonSyncNow.text = "Sincronizar Ahora"
                }
            })
    }


}