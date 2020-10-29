package com.example.internetservice_plus_rest.utils

data class SyncState private constructor(
    val status: String
) {
    companion object {
        val STARTING = SyncState("STARTING")
        val RUNNING = SyncState("RUNNING")
        val COMPLETE = SyncState("COMPLETE")
        val FAILED = SyncState("FAILED")
        val SUCCESS = SyncState("SUCCESS")
        val FINISH = SyncState("FINISH")
    }
}
