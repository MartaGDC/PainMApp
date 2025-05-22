package com.mgd.painmapp.view.adapters

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mgd.painmapp.R
import com.mgd.painmapp.model.database.Symptom
import com.mgd.painmapp.databinding.ItemSymptomBinding
import com.mgd.painmapp.model.storage.ColorBrush.colorList
import java.util.Locale

class SymptomsViewHolder (view: View, private val context: Context):RecyclerView.ViewHolder(view) {
    private val binding = ItemSymptomBinding.bind(view)
    fun render(symptom: Symptom, index: Int) {
        binding.tvNumSymptom.text = String.format(Locale.getDefault(), "%d", index + 1) //para solucionar el warning de manejo de números
        binding.cvSymptom.strokeColor = colorList[index%colorList.size]
        binding.tvScore.text = String.format(Locale.getDefault(), "%.1f", symptom.intensity)
        if (symptom.symptom == context.getString(R.string.other)){
            binding.tvSymptom.text = symptom.symptomOtherText
        }
        else{
            binding.tvSymptom.text = symptom.symptom
        }
    }
}