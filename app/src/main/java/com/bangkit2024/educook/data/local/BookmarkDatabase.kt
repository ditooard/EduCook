package com.bangkit2024.educook.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [BookmarkMenu::class],
    version = 2 // Perbarui nomor versi
)
abstract class BookmarkDatabase : RoomDatabase() {
    companion object {
        private var INS: BookmarkDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Implementasikan skema migrasi di sini jika ada perubahan
                // Misalnya, menambahkan kolom baru:
                // database.execSQL("ALTER TABLE bookmark ADD COLUMN new_column TEXT")
            }
        }

        fun getDatabase(context: Context): BookmarkDatabase? {
            if (INS == null) {
                synchronized(BookmarkDatabase::class) {
                    INS = Room.databaseBuilder(
                        context.applicationContext,
                        BookmarkDatabase::class.java,
                        "db_user"
                    )
                        .addMigrations(MIGRATION_1_2)
                        .build()
                }
            }
            return INS
        }
    }

    abstract fun bookmarkDao(): BookmarkDao
}
