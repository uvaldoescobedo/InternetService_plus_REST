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
    lateinit var modelFuntions : SyncVMFunctions
    lateinit var factory : SyncVMPFactory
    lateinit var networkConnection: NetworkConnection
    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = SyncVMPFactory(DedecRepository(RetrofitAPI().getRetrofitApi()))
        modelFuntions = ViewModelProvider(this,factory!!).get(SyncVMFunctions::class.java)

        networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this, Observer {
                isConnected->
            if(isConnected){
                Toast.makeText(this,"Conectado",Toast.LENGTH_SHORT).show()
                binding.buttonSyncNow.isEnabled = true
                startTaskForSync()
            }else{
                Toast.makeText(this,"OFFLINE",Toast.LENGTH_SHORT).show()
                binding.buttonSyncNow.isEnabled = false
            }
        })


        binding.progressBar.visibility = View.GONE

        binding.buttonSyncNow.setOnClickListener {
            if(networkConnection.value == true){
                Toast.makeText(this,"Estas Conectado",Toast.LENGTH_SHORT).show()
                startTaskForSync()
            }else{
                Toast.makeText(this,"Estas OFFLINE",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observerData() {
        modelFuntions.synState.observe(this,  Observer {
            Log.i("MainActivity synState ",it.toString())
            when(it){
                SyncState.STARTING -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.buttonSyncNow.isEnabled = false
                }
                SyncState.RUNNING -> {
                    binding.progressBar.isShown
                }
                SyncState.COMPLETE -> {
                    binding.progressBar.visibility = View.GONE
                    binding.buttonSyncNow.isEnabled = true
                }

            }
        })
        modelFuntions.syncBadResponse.observe(this,  Observer {
            Log.i("MainActivity Bad ",it.toString())
        })
        
        modelFuntions.actividadesSuccessResponse.observe(this,  Observer {
            Log.i("MainActivity activis ",it.toString())
        })

        modelFuntions.areasServiceSuccessResponse.observe(this,  Observer {
            Log.i("MainActivity areas ",it.toString())
        })
        modelFuntions.clasificadoresSuccessResponse.observe(this,  Observer {
            Log.i("MainActivity Clasif ",it.toString())
        })

        modelFuntions.checadoresSuccessResponse.observe(this,  Observer {
            Log.i("MainActivity checks ",it.toString())
        })

    }

    private fun startTaskForSync() {
        modelFuntions.syncClasificadores("x9h3sl",false)

        modelFuntions.syncActividades("x9h3sl",false)

        modelFuntions.syncAreasServicio("x9h3sl",false)

        modelFuntions.syncChecadores("x9h3sl",false)

        observerData()
    }

}