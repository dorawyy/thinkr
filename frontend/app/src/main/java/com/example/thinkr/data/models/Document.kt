package com.example.thinkr.data.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Document(
    val documentId: String,
    val documentName: String,
    val uploadTime: String,
    val activityGenerationComplete: Boolean,
    @Transient
    val isUploading: Boolean = false,
    @Transient
    val documentContext: String = ""
)
