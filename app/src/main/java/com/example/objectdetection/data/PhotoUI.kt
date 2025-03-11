package com.example.objectdetection.data

data class PhotoUI(
    val id: String,
    val imageUrl: String,
    val description: String?
)

fun Photo.toUIModel(): PhotoUI {
    return PhotoUI(
        id = this.id,
        imageUrl = this.urls?.small ?: "",
        description = this.description ?: "unknown"
    )
}
