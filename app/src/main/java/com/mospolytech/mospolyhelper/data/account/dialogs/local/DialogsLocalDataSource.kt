package com.mospolytech.mospolyhelper.data.account.dialogs.local

import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.data.utils.getFromJson
import com.mospolytech.mospolyhelper.data.utils.setAsJson
import com.mospolytech.mospolyhelper.domain.account.dialogs.model.DialogsModel
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.Result

class DialogsLocalDataSource(private val prefDataSource: SharedPreferencesDataSource) {

    fun get(): Result<DialogsModel> {
        return prefDataSource.getFromJson<DialogsModel>(PreferenceKeys.Dialogs)?.let {
                Result.success(it)
            } ?: let {
                Result.failure(Exception("Cant get local dialogs"))
            }
    }

    fun set(dialogsModel: DialogsModel) {
        if (prefDataSource.getFromJson<DialogsModel>(PreferenceKeys.Dialogs) != dialogsModel) {
            prefDataSource.setAsJson(PreferenceKeys.Dialogs, dialogsModel)
        }
    }
}