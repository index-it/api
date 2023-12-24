package app.index.data.sources.db.dbi.user

import app.index.data.models.web.NotifyDto
import app.index.data.sources.db.dbi.DBI

interface NotifyDBI : DBI {
    suspend fun create(notifyDto: NotifyDto)
}
