package xyz.saarthakdevelopsstuff.stock_dashboard_api.validation.validator

import jakarta.json.JsonArray
import jakarta.json.JsonObject
import jakarta.json.JsonPatch
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.stereotype.Component
import xyz.saarthakdevelopsstuff.stock_dashboard_api.validation.annotation.ValidWatchListJsonPatch


@Component
class WatchListJsonPatchValidator : ConstraintValidator<ValidWatchListJsonPatch, JsonPatch> {
    enum class WatchListPatchOperationType {
        NAME,
        DESCRIPTION,
        VISIBILITY,
        TICKER_SYMBOL,
        UNKNOWN
    }

    private fun validateAllowedOperations(obj: JsonObject): WatchListPatchOperationType {
        val path = obj.getString("path")
        val op = obj.getString("op")
        val value = obj.getValue("/value")
        return when {
            path == "/name" && op == "replace" -> {
                WatchListPatchOperationType.NAME
            }

            path == "/description" && op == "replace" -> {
                WatchListPatchOperationType.DESCRIPTION
            }

            path == "/visibility" && op == "replace" -> {
                WatchListPatchOperationType.VISIBILITY
            }

            path == "/tickerSymbols" && op == "replace" && value is JsonArray && value.asJsonArray().lastIndex < 20 -> {
                WatchListPatchOperationType.TICKER_SYMBOL
            }

            Regex("^/tickerSymbols(?:/(?:[0-9]+|-))?$").matches(path) && (op == "add" || op == "remove") -> {
                WatchListPatchOperationType.TICKER_SYMBOL
            }

            else -> WatchListPatchOperationType.UNKNOWN
        }
    }

    private fun validateTotalLength(ops: JsonArray): Boolean {
        return ops.lastIndex < 20
    }

    override fun isValid(
        value: JsonPatch?, context: ConstraintValidatorContext
    ): Boolean {
        if (value == null) {
            context.buildConstraintViolationWithTemplate("Json patch value cannot be null")
            return false
        }
        val ops = value.toJsonArray()
        if (!validateTotalLength(ops)) {
            context.buildConstraintViolationWithTemplate("Json patch operations cannot exceed 20")
                .addConstraintViolation()
            return false
        }
        for (op in value.toJsonArray()) {
            val opObj = op as JsonObject
            val patchOperationType = validateAllowedOperations(opObj)
            if (patchOperationType == WatchListPatchOperationType.UNKNOWN) {
                context.buildConstraintViolationWithTemplate("$op contains invalid operations").addConstraintViolation()
                return false
            }
            if (patchOperationType == WatchListPatchOperationType.TICKER_SYMBOL) {
                context.buildConstraintViolationWithTemplate("$op contains ticker symbols. Please use the dedicated methods for patching ticker symbols")
                    .addConstraintViolation()
                return false
            }
        }
        return true
    }
}
