package com.mgd.painmapp

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.mgd.painmapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var patientName : EditText
    private lateinit var researcherName : EditText
    private lateinit var CVSiguiente : CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {
        patientName = binding.PatientName
        researcherName = binding.ResearcherName
        CVSiguiente = binding.CVSiguiente
        CVSiguiente.setOnClickListener {
            if (patientName.text.isNotEmpty() && researcherName.text.isNotEmpty()) {
                val intent = Intent(this, ChooseActivity::class.java).apply {
                    putExtra("PATIENT_NAME", patientName.text.toString())
                    putExtra("RESEARCHER_NAME", researcherName.text.toString())
                }
                startActivity(intent)
            }
        }
    }
}