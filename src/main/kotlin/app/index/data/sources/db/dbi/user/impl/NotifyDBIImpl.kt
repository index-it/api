package app.index.data.sources.db.dbi.user.impl

import app.index.data.models.web.NotifyDto
import app.index.data.sources.db.dbi.user.NotifyDBI
import app.index.data.sources.db.schemas.web.NotifyEntity
import app.index.data.sources.db.schemas.web.ReleaseNotifyTable
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class NotifyDBIImpl : NotifyDBI {
    override suspend fun create(notifyDto: NotifyDto) {
        dbQuery {
            if (NotifyEntity.find { ReleaseNotifyTable.email eq notifyDto.email }.limit(1).firstOrNull() == null) {
                NotifyEntity.new {
                    email = notifyDto.email
                }
            }
        }
    }
}
