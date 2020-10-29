package com.example.internetservice_plus_rest.utils

data class SynState private constructor(
    val status: String
) {
    companion object {
        val STARTING = SynState("STARTING")
        val RUNNING = SynState("RUNNING")
        val COMPLETE = SynState("COMPLETE")
        val FAILED = SynState("FAILED")
        val SUCCESS = SynState("SUCCESS")
        val FINISH = SynState("FINISH")
    }
}
