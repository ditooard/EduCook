package com.bangkit2024.educook.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.bangkit2024.educook.data.local.BookmarkDao
import com.bangkit2024.educook.data.local.BookmarkDatabase
import com.bangkit2024.educook.data.local.BookmarkMenu

class BookmarkViewModel(application: Application) : AndroidViewModel(application)  {

    private var daoBookmark: BookmarkDao?
    private var dbBookmark: BookmarkDatabase?

    init {
        dbBookmark = BookmarkDatabase.getDatabase(application)
        daoBookmark = dbBookmark?.bookmarkDao()
    }

    fun getBookmarkList(): LiveData<List<BookmarkMenu>>? {
        return daoBookmark?.getBookmark()
    }
}
