package app.index_it.core.exceptions

class AuthenticationException(override val message: String? = null) : RuntimeException(message)
class AuthorizationException(override val message: String? = null) : RuntimeException(message)
