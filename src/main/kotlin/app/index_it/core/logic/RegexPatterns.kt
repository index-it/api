package app.index_it.core.logic

object RegexPatterns {
    val emailPattern = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])".toRegex()
    val colorPattern = "#[0-9a-fA-F]{6}".toRegex()
}
