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
    private fun validate(obj: JsonObject): Boolean {
        val path = obj.getString("path")
        val op = obj.getString("op")
        val value = obj.getValue("/value")
        when {
            path == "/name" && op == "replace" -> {}
            path == "/description" && op == "replace" -> {}
            path == "/visibility" && op == "replace" -> {}
            path == "/tickerSymbols" && op == "replace" && value is JsonArray && value.asJsonArray().lastIndex < 20 -> {}
            Regex("^/tickerSymbols(?:/(?:[0-9]+|-))?$").matches(path) && (op == "add" || op == "remove") -> {}
            else -> return false
        }
        return true
    }

    private fun validateTotalLength(ops: JsonArray) : Boolean {
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
            context.buildConstraintViolationWithTemplate("Json patch operations cannot exceed 20").addConstraintViolation()
            return false
        }
        for (op in value.toJsonArray()) {
            val opObj = op as JsonObject
            if (!validate(opObj)) {
                context.buildConstraintViolationWithTemplate("$op contains invalid operations").addConstraintViolation()
                return false
            }
        }
        return true
    }
}
