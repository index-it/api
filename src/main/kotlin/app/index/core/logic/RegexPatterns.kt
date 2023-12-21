package app.index.core.logic

object RegexPatterns {
    val emailPattern = """\w+@\w+\.\w+""".toRegex()
    val passwordPatterns = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])".toRegex()
    val colorPattern = "#[0-9a-fA-F]{6}".toRegex()
}
