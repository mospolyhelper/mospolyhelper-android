package com.mospolytech.data.base.repository

import kotlinx.coroutines.flow.Flow

interface ClipboardRepository {
    fun getText(): Flow<String>
    fun setText(label: String, value: String)
}