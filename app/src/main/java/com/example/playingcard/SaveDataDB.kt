package com.example.playingcard

import android.content.Context
import androidx.room.*

@Database(entities = [Card::class], version = 1)
//@TypeConverters(Converters::class)
abstract class SaveDataDB: RoomDatabase() {
    abstract val dao: SaveDataDao
    companion object{
        private var INSTANCE: SaveDataDB? = null
        fun getInstance(context: Context): SaveDataDB{
            synchronized(this){
                var instance = INSTANCE
                if(instance==null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SaveDataDB::class.java,
                        "Cards.db"
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}