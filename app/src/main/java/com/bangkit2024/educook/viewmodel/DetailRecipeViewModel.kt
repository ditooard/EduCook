package com.bangkit2024.educook.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.bangkit2024.educook.data.local.BookmarkDao
import com.bangkit2024.educook.data.local.BookmarkDatabase
import com.bangkit2024.educook.data.local.BookmarkMenu
import com.bangkit2024.educook.data.response.DetailMenu
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailRecipeViewModel(application: Application) : AndroidViewModel(application) {
    private var daoUser: BookmarkDao?
    private var dbUser: BookmarkDatabase?

    val menuList = MutableLiveData<DetailMenu>()

    init {
        dbUser = BookmarkDatabase.getDatabase(application)
        daoUser = dbUser?.bookmarkDao()
    }

    fun addBookmark(bookmarkMenu: BookmarkMenu) {
        CoroutineScope(Dispatchers.IO).launch {
            daoUser?.addBookmark(bookmarkMenu)
        }
    }

    fun checkBookmark(id: String) = daoUser?.checkBookmark(id)

    fun removeBookmark(id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            daoUser?.removeBookmark(id)
        }
    }
}
