package com.example.objectdetection.data

data class PhotoUI(
    val id: String,
    val imageUrl: String,
    val photoName: String
)

fun Photo.toUIModel(query: String): PhotoUI {
    return PhotoUI(
        id = this.id,
        imageUrl = this.urls?.small ?: "",
        photoName = query
    )
}
