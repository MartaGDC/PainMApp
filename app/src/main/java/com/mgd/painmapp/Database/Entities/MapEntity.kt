package com.mgd.painmapp.Database.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="map_table")
data class MapEntity {
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name="idMap") val idMap: Long = 0,
    @ColumnInfo(name="totalPercentage") val name : Float,

}