package com.mgd.painmapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.mgd.painmapp.databinding.ActivityLocationBinding

class LocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLocationBinding
    private lateinit var CVsave: CardView
    private var idGenerado: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        CVsave = binding.CVsave
        idGenerado = intent.getLongExtra("ID", 0)

        CVsave.setOnClickListener {
            val intent = Intent(this, SensorialSurveyActivity::class.java).apply {
                putExtra("ID", idGenerado)
            }
            startActivity(intent)
        }
    }
}