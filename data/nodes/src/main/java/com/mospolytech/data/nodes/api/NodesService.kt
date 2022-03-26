package com.mospolytech.data.nodes.api

import com.mospolytech.domain.nodes.model.NodeContract
import kotlinx.coroutines.flow.Flow
import retrofit2.http.*

interface NodesService {
    @GET
    fun getNodeContract(
        @Url url: String
    ): Flow<Result<NodeContract>>
}