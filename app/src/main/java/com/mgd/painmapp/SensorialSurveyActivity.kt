package com.mgd.painmapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.mgd.painmapp.databinding.ActivitySensorialSurveyBinding

class SensorialSurveyActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySensorialSurveyBinding
    private lateinit var idGenerado : String
    private lateinit var CVsave : CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySensorialSurveyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idGenerado = intent.getStringExtra("ID").toString()

        CVsave.setOnClickListener {
            val intent = Intent(this, LocationActivity::class.java).apply {
                putExtra("ID", idGenerado) //Habra que inlcuir más informaicón aqui, derivada de consultas a la base de datos 
            }
            startActivity(intent)
        }

    }
}