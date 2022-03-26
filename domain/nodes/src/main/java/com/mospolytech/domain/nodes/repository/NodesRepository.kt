package com.mospolytech.domain.nodes.repository

import com.mospolytech.domain.nodes.model.NodeContract
import kotlinx.coroutines.flow.Flow

interface NodesRepository {
    fun selectNode(url: String): Flow<Result<Unit>>
    fun getSelectedNodeContract(): Flow<Result<NodeContract>>
}