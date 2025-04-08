package com.mgd.painmapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.mgd.painmapp.databinding.ActivityLocationBinding

class LocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLocationBinding
    private lateinit var CVsave: CardView
    private lateinit var CVDelete: CardView
    private lateinit var IVfrente: MapViews
    private var idGeneradoLocation: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        CVsave = binding.CVsave
        CVDelete = binding.CVdelete
        IVfrente = binding.IVfrente
        idGeneradoLocation = intent.getLongExtra("ID", -1)

        CVsave.setOnClickListener {
            val intent = Intent(this, SensorialSurveyActivity::class.java).apply {
                putExtra("ID", idGeneradoLocation)
            }
            startActivity(intent)
        }
        CVDelete.setOnClickListener {
            IVfrente.deleteDrawing()
        }
    }
}