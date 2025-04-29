package com.mgd.painmapp.controller.activities

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.mgd.painmapp.databinding.ActivityMainBinding
import com.mgd.painmapp.model.storage.ColorBrush
import com.mgd.painmapp.model.storage.saveColorIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var patientName : EditText
    private lateinit var researcherName : EditText
    private lateinit var cvNext : CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //color para dibujar y para guardar dibujo
        ColorBrush.initialize(this)
        CoroutineScope(Dispatchers.IO).launch {
            applicationContext.saveColorIndex(0) //al inicio de la app siempre empieza con el mismo color
        }
        initUI()
    }

    private fun initUI() {
        patientName = binding.etPatientName
        researcherName = binding.etResearcherName
        cvNext = binding.cvNext
        cvNext.setOnClickListener {
            if (patientName.text.isNotEmpty() && researcherName.text.isNotEmpty()) {
                val intent = Intent(this, ChooseActivity::class.java).apply {
                    putExtra("patient_name", patientName.text.toString())
                    putExtra("researcher_name", researcherName.text.toString())
                }
                startActivity(intent)
            }
        }
    }
}