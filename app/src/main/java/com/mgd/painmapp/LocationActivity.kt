package com.mgd.painmapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.mgd.painmapp.databinding.ActivityLocationBinding

class LocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLocationBinding
    private lateinit var save: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        save = binding.save
        save.setOnClickListener {
            val intent = Intent(this, SensorialSurveyActivity::class.java)
            startActivity(intent)
        }

    }
}