package com.example.playingcard

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SaveDataDao {

    @Query("select * from Cards_table")
    suspend fun loadList(): List<Card>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(list: List<Card>)

    @Query("delete from Cards_table")
    suspend fun clear()
}