package com.mospolytech.mospolyhelper.features.ui.schedule.appwidget

import android.content.Intent
import android.widget.RemoteViewsService

class LessonAdapterService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return LessonRemoteAdapter(applicationContext, intent)
    }
}