package com.ahmedsamir.pulse.core.model

data class Follow(
    val followerId: String = "",
    val followingId: String = "",
    val createdAt: Long = System.currentTimeMillis()
)