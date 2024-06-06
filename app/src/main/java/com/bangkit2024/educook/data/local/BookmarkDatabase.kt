package com.bangkit2024.educook.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [BookmarkMenu::class],
    version = 1
)
abstract class BookmarkDatabase : RoomDatabase() {
    companion object {
        private var INS: BookmarkDatabase? = null

        fun getDatabase(context: Context): BookmarkDatabase? {
            if (INS == null) {
                synchronized(BookmarkDatabase::class) {
                    INS = Room.databaseBuilder(
                        context.applicationContext,
                        BookmarkDatabase::class.java,
                        "db_user"
                    ).build()
                }
            }
            return INS
        }
    }

    abstract fun bookmarkDao(): BookmarkDao
}