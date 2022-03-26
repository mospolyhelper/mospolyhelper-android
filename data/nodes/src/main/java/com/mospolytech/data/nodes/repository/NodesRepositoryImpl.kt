package com.mospolytech.data.nodes.repository

import com.mospolytech.data.base.consts.PrefConst
import com.mospolytech.data.base.local.PreferencesDS
import com.mospolytech.data.base.local.set
import com.mospolytech.data.nodes.api.NodesService
import com.mospolytech.domain.nodes.model.NodeContract
import com.mospolytech.domain.nodes.repository.NodesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class NodesRepositoryImpl(
    private val api: NodesService,
    private val preferencesDS: PreferencesDS
) : NodesRepository {
    override fun selectNode(url: String) = flow {
        emit(preferencesDS.set(url, PrefConst.SelectedNode))
    }.flowOn(Dispatchers.IO)

    override fun getSelectedNodeContract(): Flow<Result<NodeContract>> {
        TODO("Not yet implemented")
    }
}