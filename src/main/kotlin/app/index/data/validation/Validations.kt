package app.index.data.validation

object Validations {
    object List {
        const val MIN_NAME_LENGTH = 1
        const val MAX_NAME_LENGTH = 100

        const val VALIDATIONS_SUMMARY = """
            - name min length: $MIN_NAME_LENGTH
            - name max length: $MAX_NAME_LENGTH
        """
    }

    object ListInvite {
        const val MIN_DESCRIPTION_LENGTH = 1
        const val MAX_DESCRIPTION_LENGTH = 500

        const val MINIMUM_USAGES = 1
        const val MAXIMUM_USAGES = 10000

        const val VALIDATIONS_SUMMARY = """
            - description min length: $MIN_DESCRIPTION_LENGTH
            - description max length: $MAX_DESCRIPTION_LENGTH
            - minimum usages: $MINIMUM_USAGES
            - maximum usages: $MAXIMUM_USAGES
        """
    }

    object Category {
        const val MIN_NAME_LENGTH = 1
        const val MAX_NAME_LENGTH = 100

        const val VALIDATIONS_SUMMARY = """
            - name min length: $MIN_NAME_LENGTH
            - name max length: $MAX_NAME_LENGTH
        """
    }

    object Item {
        const val MIN_NAME_LENGTH = 1
        const val MAX_NAME_LENGTH = 200

        const val MAX_LINK_LENGTH = 500

        const val MAX_NOTE_LENGTH = 3000

        const val VALIDATIONS_SUMMARY = """
            - name min length: $MIN_NAME_LENGTH
            - name max length: $MAX_NAME_LENGTH
            - link max length: $MAX_LINK_LENGTH
            - note max length: $MAX_NOTE_LENGTH
        """
    }

    object ItemContent {
        const val MIN_CONTENT_LENGTH = 0
        const val MAX_CONTENT_LENGTH = 20000

        const val VALIDATIONS_SUMMARY = """
            - content min length: $MIN_CONTENT_LENGTH
            - content max length: $MAX_CONTENT_LENGTH
        """
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

        const val VALIDATIONS_SUMMARY = """
            - name min length: $MIN_NAME_LENGTH
            - name max length: $MAX_NAME_LENGTH
            - description min length: $MIN_DESCRIPTION_LENGTH
            - description max length: $MAX_DESCRIPTION_LENGTH
            - max subtask count: $MAX_SUBTASK_COUNT
            - subtask name max length: $MAX_SUBTASK_NAME_LENGTH
            - max reminders count: $MAX_REMINDERS_COUNT
            - minimum priority: $MINIMUM_PRIORITY
            - maximum priority: $MAXIMUM_PRIORITY
        """
    }
}