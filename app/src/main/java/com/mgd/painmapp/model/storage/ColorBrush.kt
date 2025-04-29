package com.mgd.painmapp.model.storage

import android.content.Context
import androidx.core.content.ContextCompat
import com.mgd.painmapp.R

object  ColorBrush {
    val colorList = mutableListOf<Int>()
    fun initialize(context: Context) {
        colorList.clear()
        colorList.addAll(
            listOf(
                ContextCompat.getColor(context, R.color.dark_blue),
                ContextCompat.getColor(context, R.color.blue),
                ContextCompat.getColor(context, R.color.red),
                ContextCompat.getColor(context, R.color.pink),
                ContextCompat.getColor(context, R.color.yellow)
            )
        )
    }
}