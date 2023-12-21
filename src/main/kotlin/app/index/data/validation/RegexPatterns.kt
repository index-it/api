package app.index.data.validation

object RegexPatterns {
    val emailPattern = """\w+@\w+\.\w+""".toRegex()
    val passwordPatterns = "(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*\$".toRegex()
    val colorPattern = "#[0-9a-fA-F]{6}".toRegex()
}
