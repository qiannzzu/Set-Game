package com.example.playingcard

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Cards_table")
class Card(
    val rank:String = "",
    val shape:String = "",
    val shade:String = "",
    val cardColor:String = "",
    var choose:Boolean = false,
    var onBoard:Boolean = false,
    var history: Int = -1
) {
    @PrimaryKey(autoGenerate = false) var id: String = rank+shape+shade+cardColor
}