package app.index.data.validation

object Validations {
    object List {
        const val MIN_NAME_LENGTH = 1
        const val MAX_NAME_LENGTH = 100
    }

    object Category {
        const val MIN_NAME_LENGTH = 1
        const val MAX_NAME_LENGTH = 100
    }

    object Item {
        const val MIN_NAME_LENGTH = 1
        const val MAX_NAME_LENGTH = 200

        const val MAX_LINK_LENGTH = 200
    }

    object ItemContent {
        const val MIN_CONTENT_LENGTH = 0
        const val MAX_CONTENT_LENGTH = 20000
    }

    object Task {
        const val MIN_NAME_LENGTH = 1
        const val MAX_NAME_LENGTH = 200

        const val MIN_DESCRIPTION_LENGTH = 1
        const val MAX_DESCRIPTION_LENGTH = 500

        const val MAX_SUBTASK_COUNT = 50
        const val MAX_SUBTASK_NAME_LENGTH = 200
        const val MAX_REMINDERS_COUNT = 10

        const val MINIMUM_PRIORITY = 0
        const val MAXIMUM_PRIORITY = 4
    }
}