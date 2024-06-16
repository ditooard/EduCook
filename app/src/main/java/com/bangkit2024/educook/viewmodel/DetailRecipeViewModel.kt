package com.bangkit2024.educook.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.bangkit2024.educook.data.local.BookmarkDao
import com.bangkit2024.educook.data.local.BookmarkDatabase
import com.bangkit2024.educook.data.local.BookmarkMenu
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailRecipeViewModel(application: Application) : AndroidViewModel(application) {
    private var daoBookmark: BookmarkDao?
    private var dbBookmark: BookmarkDatabase?

    init {
        dbBookmark = BookmarkDatabase.getDatabase(application)
        daoBookmark = dbBookmark?.bookmarkDao()
    }

    fun addBookmark(bookmarkMenu: BookmarkMenu) {
        CoroutineScope(Dispatchers.IO).launch {
            daoBookmark?.addBookmark(bookmarkMenu)
        }
    }

    fun checkBookmark(id: String): LiveData<Int>? = daoBookmark?.checkBookmark(id)

    fun removeBookmark(id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            daoBookmark?.removeBookmark(id)
        }
    }
}
