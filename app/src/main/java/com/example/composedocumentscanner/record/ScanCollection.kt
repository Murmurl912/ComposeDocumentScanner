package com.example.composedocumentscanner.record

data class ScanCollection(
    val id: Long,
    val title: String,
    val cover: String,
    val photoCount: Int = 0,
    val status: Int = -1,
    val createAt: Long,
    val updateAt: Long,
)

data class ScanCollectionState(
    val collection: ScanCollection,
)