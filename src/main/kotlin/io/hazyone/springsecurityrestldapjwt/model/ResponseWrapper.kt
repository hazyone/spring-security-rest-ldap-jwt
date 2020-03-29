package io.hazyone.springsecurityrestldapjwt.model

data class ResponseWrapper(val data: Any = "", val metadata: Any, val errors: List<ErrorMessage>) {
}