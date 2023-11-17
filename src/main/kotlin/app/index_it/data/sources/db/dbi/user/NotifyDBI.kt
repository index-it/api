package app.index_it.data.sources.db.dbi.user

import app.index_it.data.models.web.NotifyDto
import app.index_it.data.sources.db.dbi.DBI

interface NotifyDBI : DBI {
    suspend fun save(notifyDto: NotifyDto)
}