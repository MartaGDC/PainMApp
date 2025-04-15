package com.mgd.painmapp.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mgd.painmapp.R
import com.mgd.painmapp.model.database.Symptom

class SymptomsAdapter (var symptoms: List<Symptom>, private var context: Context):RecyclerView.Adapter<SymptomsViewHolder>(){
    fun updateList(list: List<Symptom>){
        symptoms = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymptomsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_symptom, parent, false)
        return SymptomsViewHolder(view, context)
    }

    override fun getItemCount() = symptoms.size

    override fun onBindViewHolder(holder: SymptomsViewHolder, position: Int) {
        holder.render(symptoms[position], position)
    }


}