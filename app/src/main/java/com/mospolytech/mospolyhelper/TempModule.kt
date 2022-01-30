package com.mospolytech.mospolyhelper

import android.content.Context
import com.mospolytech.data.base.local.DataVersionLocalDS
import com.mospolytech.data.base.model.DataVersion
import com.mospolytech.data.schedule.model.ScheduleDao
import com.mospolytech.data.schedule.model.ScheduleSourceDao
import com.mospolytech.data.schedule.model.ScheduleSourceFullDao
import org.kodein.db.DB
import org.kodein.db.OpenPolicy
import org.kodein.db.impl.open
import org.kodein.db.orm.kotlinx.KotlinxSerializer
import org.koin.dsl.module
import kotlin.io.path.*

val tempModule = module {
    single { PathProvider(get()) }
    single { buildDB(get()) }
}

class PathProvider(
    private val context: Context
) {
    fun getAbsolutePath(): String =
        context.filesDir.absolutePath
}

fun buildDB(pathProvider: PathProvider): DB {
    val root = pathProvider.getAbsolutePath()
    val path = Path(root, "kodein", "db")
    if (path.notExists()) {
        path.createDirectories()
    }
    return DB.open(
        path.pathString,
        KotlinxSerializer {
            +ScheduleDao.serializer()
            +ScheduleSourceDao.serializer()
            +DataVersion.serializer()
            +ScheduleSourceFullDao.serializer()
        },
        OpenPolicy.OpenOrCreate
    )
}