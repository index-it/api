package app.index_it.data.sources.mongo.lists

import app.index_it.core.logic.typedId.impl.IxId
import app.index_it.data.models.lists.ItemContentDto
import app.index_it.data.models.lists.ItemDto
import app.index_it.data.models.user.UserDto
import app.index_it.data.sources.mongo.MongoClient
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.ReturnDocument
import org.litote.kmongo.*

object ItemContentDBM {
    private val col = MongoClient.database.getCollection<ItemContentDto>("item-contents")

    init {
        col.ensureIndex(ItemContentDto::itemId)
        col.ensureIndex(ItemContentDto::userId)
    }

    fun create(itemContentDto: ItemContentDto) {
        col.save(itemContentDto)
    }

    fun get(userId: IxId<UserDto>, itemId: IxId<ItemDto>): ItemContentDto? {
        return col.findOne(and(ItemContentDto::itemId eq itemId, ItemContentDto::userId eq userId))
    }

    fun update(userId: IxId<UserDto>, itemId: IxId<ItemDto>, itemContentCreateOrUpdateRequest: ItemContentDto.ItemContentCreateOrUpdateRequest): ItemContentDto? {
        val properties: MutableList<SetTo<Any?>> = mutableListOf()

        properties.add(ItemContentDto::content setTo itemContentCreateOrUpdateRequest.content)

        return col.findOneAndUpdate(
            and(ItemContentDto::itemId eq itemId, ItemContentDto::userId eq userId),
            set(*properties.toTypedArray()),
            FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        )
    }

    fun delete(userId: IxId<UserDto>, itemId: IxId<ItemDto>) {
        col.deleteOne(
            and(ItemContentDto::itemId eq itemId, ItemContentDto::userId eq userId)
        )
    }

    fun deleteAllOfUser(userId: IxId<UserDto>) {
        col.deleteMany(ItemContentDto::userId eq userId)
    }

    fun deleteAllOfItems(userId: IxId<UserDto>, itemIds: List<IxId<ItemDto>>) {
        col.deleteMany(
            and(ItemContentDto::userId eq userId, ItemContentDto::itemId `in` itemIds)
        )
    }
}