package com.mgd.painmapp

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.mgd.painmapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var patientName : EditText
    private lateinit var researcherName : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {
        patientName = binding.PatientName
        researcherName = binding.ResearcherName
        researcherName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (patientName.text.isNotEmpty() && researcherName.text.isNotEmpty()) {
                    val intent = Intent(this, ChooseActivity::class.java).apply {
                        putExtra("PATIENT_NAME", patientName.text.toString())
                        putExtra("RESEARCHER_NAME", researcherName.text.toString())
                    }
                    startActivity(intent)
                }
                true
            } else {
                false
            }
        }
    }
}