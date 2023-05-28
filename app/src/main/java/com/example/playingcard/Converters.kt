package com.example.playingcard

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromCardList(mutableList_in: MutableList<Card>): MutableList<Card>{
        val output = mutableList_in.toMutableList()
        output.toList()
        return output
    }

    @TypeConverter
    fun toCardList(list_in: List<Card>): MutableList<Card>{
        return list_in.toMutableList()
    }
}