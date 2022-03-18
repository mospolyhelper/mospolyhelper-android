package com.mospolytech.data.base.repository

import android.content.ClipData
import android.content.ClipboardManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ClipboardRepositoryImpl(
    private val manager: ClipboardManager
) : ClipboardRepository {
    private val scope = CoroutineScope(Dispatchers.IO + Job())

    private val flow = MutableSharedFlow<String>()

    private val clipListener: () -> Unit = {
        scope.launch {
            flow.emit(getCurrentText())
        }
    }

    init {
        manager.addPrimaryClipChangedListener(clipListener)
    }

    private fun getCurrentText(): String {
        return manager.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
    }

    override fun getText(): Flow<String> = flow.asSharedFlow()
        .onStart { emit(getCurrentText()) }

    override fun setText(label: String, value: String) {
        val clip = ClipData.newPlainText(label, value)
        manager.setPrimaryClip(clip)
    }
}