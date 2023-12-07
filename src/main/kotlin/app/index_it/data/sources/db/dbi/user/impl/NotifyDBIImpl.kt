package app.index_it.data.sources.db.dbi.user.impl

import app.index_it.data.models.web.NotifyDto
import app.index_it.data.sources.db.dbi.user.NotifyDBI
import app.index_it.data.sources.db.schemas.web.NotifyEntity
import app.index_it.data.sources.db.schemas.web.ReleaseNotifyTable

object NotifyDBIImpl : NotifyDBI {
    override suspend fun save(notifyDto: NotifyDto) {
        dbQuery {
            if (NotifyEntity.find { ReleaseNotifyTable.email eq notifyDto.email }.limit(1).firstOrNull() == null) {
                NotifyEntity.new {
                    email = notifyDto.email
                }
            }
        }
    }
}