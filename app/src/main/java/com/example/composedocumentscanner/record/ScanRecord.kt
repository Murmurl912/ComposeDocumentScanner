package com.example.composedocumentscanner.record

import android.graphics.Point
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class ScanRecord(
    val id: Long,
    val createAt: Long,
    val updateAt: Long,
    val order: Int,
    val photo: String?,
    val orignal: String?,
    val corner: List<Point>,
    val ratio: Float,
    val text: String?,
    val layout: String?,
    val status: Int = -1,
)

data class ScanRecordState (
    val record: ScanRecord,
    val selectionIndex: MutableState<Int?> = mutableStateOf(null),
    val selected: MutableState<Boolean> = mutableStateOf(false),
    val selection: MutableState<Boolean> = mutableStateOf(false),
)