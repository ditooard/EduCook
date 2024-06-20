package com.bangkit2024.educook.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "bookmark")
data class BookmarkMenu(
    @PrimaryKey val id: String,
    val title: String,
    val directions: String,
    val ingredients: String,
    val createdAt: String,
    val updatedAt: String?,
    val imageId: String?,
    val idUser: String?,
    val imageUrl: String?
) : Serializable
