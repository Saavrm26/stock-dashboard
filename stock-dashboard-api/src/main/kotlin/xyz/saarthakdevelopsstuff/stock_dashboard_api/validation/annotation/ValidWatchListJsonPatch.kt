package xyz.saarthakdevelopsstuff.stock_dashboard_api.validation.annotation

import jakarta.validation.Constraint
import xyz.saarthakdevelopsstuff.stock_dashboard_api.validation.validator.WatchListJsonPatchValidator
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.VALUE_PARAMETER
import kotlin.reflect.KClass

@Target(VALUE_PARAMETER)
@Retention(RUNTIME)
@Constraint(validatedBy = [WatchListJsonPatchValidator::class])
annotation class ValidWatchListJsonPatch(
    val message: String = "Invalid JSON patch for watch list",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Any>> = []
)
