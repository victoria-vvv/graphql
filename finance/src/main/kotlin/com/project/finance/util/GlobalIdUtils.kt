package com.project.finance.util

import java.util.*

object GlobalIdUtils {

    fun toGlobalId(typeName: String, id: String): String {
      return  Base64.getEncoder().encodeToString("$typeName:$id".toByteArray())
    }

    fun fromGlobalId(globalId: String): Pair<String, String> {
        val decoded = String(Base64.getDecoder().decode(globalId))
        val parts = decoded.split(":", limit = 2)
        require(parts.size == 2) { "Invalid global ID format" }
        return parts[0] to parts[1]
    }
}
