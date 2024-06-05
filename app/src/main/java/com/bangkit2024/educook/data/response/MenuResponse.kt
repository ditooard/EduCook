package com.bangkit2024.educook.data.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class MenuResponse(
    var error: String,
    var message: String,
    var listStory: List<DetailMenu>
)

@Parcelize
data class DetailMenu(
    var id: String,
    var name: String,
    var description: String,
    var photoUrl: String,
    var createdAt: String,
    var lat: Double,
    var lon: Double
) : Parcelable