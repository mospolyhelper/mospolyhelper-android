package com.mospolytech.domain.nodes.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NodeContract(
    @SerialName("version")
    val version: Int,
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("image")
    val image: String,
    @SerialName("api")
    val api: NodeApi
)

@Serializable
data class NodeApi(
    @SerialName("urls")
    val urls: Map<String, String>,
    @SerialName("endpoints")
    val endpoints: Map<NodeEndpoints, String>
)