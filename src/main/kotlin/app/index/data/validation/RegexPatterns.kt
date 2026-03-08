package app.index.data.validation

object RegexPatterns {
    val emailPattern = """(?!\.)(?!.*\.\.)([a-z0-9_'+\-\.]*)[a-z0-9_'+\-]@([a-z0-9][a-z0-9\-]*\.)+[a-z]{2,}""".toRegex()
    val passwordPatterns = "(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*\$".toRegex()
    val colorPattern = "#[0-9a-fA-F]{6}".toRegex()
}
