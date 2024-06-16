package com.bangkit2024.educook.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface BookmarkDao {
    @Insert
    fun addBookmark(favoriteUser: BookmarkMenu)

    @Query("SELECT * FROM bookmark")
    fun getBookmark(): LiveData<List<BookmarkMenu>>

    @Query("SELECT count(*) FROM bookmark WHERE bookmark.id = :id")
    fun checkBookmark(id: String): LiveData<Int>

    @Query("DELETE FROM bookmark WHERE bookmark.id = :id")
    fun removeBookmark(id: String)
}



