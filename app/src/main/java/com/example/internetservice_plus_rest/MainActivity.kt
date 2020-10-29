package com.example.internetservice_plus_rest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.internetservice_plus_rest.core.RetrofitAPI
import com.example.internetservice_plus_rest.repository.DedecRepository
import com.example.internetservice_plus_rest.viewModels.SyncVMFunctions
import com.example.internetservice_plus_rest.viewModels.SyncVMPFactory

class MainActivity : AppCompatActivity() {
    lateinit var modelFuntions : SyncVMFunctions
    lateinit var factory : SyncVMPFactory
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        factory = SyncVMPFactory(DedecRepository(RetrofitAPI().getRetrofitApi()))
        modelFuntions = ViewModelProvider(this,factory!!).get(SyncVMFunctions::class.java)
       // observerData()

       modelFuntions.syncClasificadores("x9h3sl",false)
       modelFuntions.syncActividades("x9h3sl",false)
       modelFuntions.syncAreasServicio("x9h3sl",false)
        modelFuntions.syncChecadores("x9h3sl",false)

        observerData()
    }

    private fun observerData() {
        modelFuntions.synState.observe(this,  Observer {
            Log.i("MainActivity synState ",it.toString())
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
}