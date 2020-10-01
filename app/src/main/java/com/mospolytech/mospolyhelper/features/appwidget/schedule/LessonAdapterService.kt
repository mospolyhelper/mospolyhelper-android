package com.mospolytech.mospolyhelper.features.appwidget.schedule

import android.content.Intent
import android.widget.RemoteViewsService

class LessonAdapterService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return LessonRemoteAdapter(applicationContext, intent)
    }
}