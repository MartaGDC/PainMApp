package com.mgd.painmapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.mgd.painmapp.databinding.ActivityLocationBinding

class LocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLocationBinding
    private lateinit var cvSave: CardView
    private lateinit var cvDelete: CardView
    private lateinit var mvFront: MapViews
    private lateinit var mvBack: MapViews
    private var idGeneradoLocation: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        cvSave = binding.cvSave
        cvDelete = binding.cvDelete
        mvFront = binding.mvFront
        mvBack = binding.mvBack
        idGeneradoLocation = intent.getLongExtra("ID", -1)

        cvSave.setOnClickListener {
            val porcentajeFrente = mvFront.calcularPorcentaje(calculo="total")
            val porcentajeEspalda = mvBack.calcularPorcentaje(calculo="total")
            val porcentajeTotal = (porcentajeFrente + porcentajeEspalda) / 2
            val porcentajedchaFrente = mvFront.calcularPorcentaje(calculo="derecha")
            val porcentajedchaEspalda = mvBack.calcularPorcentaje(calculo="izquierda")
            val porcentajedchaTotal = (porcentajedchaFrente + porcentajedchaEspalda) / 2
            val porcentajeizdaFrente = mvFront.calcularPorcentaje(calculo="izquierda")
            val porcentajeizdaEspalda = mvBack.calcularPorcentaje(calculo="derecha")
            val porcentajeizdaTotal = (porcentajeizdaFrente + porcentajeizdaEspalda) / 2
            val intent = Intent(this, SensorialSurveyActivity::class.java).apply {
                putExtra("ID", idGeneradoLocation)
            }
            startActivity(intent)
        }
        cvDelete.setOnClickListener {
            mvFront.deleteDrawing()
            mvBack.deleteDrawing()
        }
    }


}