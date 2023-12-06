package app.index_it.data.sources.db.core

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.statements.jdbc.JdbcConnectionImpl
import org.jetbrains.exposed.sql.transactions.TransactionManager

fun <T> Table.array(name: String, columnType: ColumnType): Column<Array<T>> =
    registerColumn(name, ArrayColumnType(columnType))

class ArrayColumnType(private val type: ColumnType) : ColumnType() {

    override fun sqlType(): String = buildString {
        append(type.sqlType())
        append("[]")
    }

    override fun valueFromDB(value: Any): Any {
        return when (value) {
            is java.sql.Array -> value.array
            is Array<*> -> value
            else -> error("Unexpected value of type Array: $value of ${value::class.qualifiedName}")
        }
    }

    override fun valueToDB(value: Any?): Any? {
        return if (value is Array<*>) {
            val jdbcConnection = (TransactionManager.current().connection as JdbcConnectionImpl).connection
            val sqlType = type.sqlType().split("(")[0]
            return jdbcConnection.createArrayOf(sqlType, value)
        } else {
            super.valueToDB(value)
        }
    }

    override fun notNullValueToDB(value: Any): Any {
        if (value is Array<*>) {
            if (value.isEmpty())
                return "'{}'"
            val jdbcConnection = (TransactionManager.current().connection as JdbcConnectionImpl).connection
            val sqlType = type.sqlType().split("(")[0]
            return jdbcConnection.createArrayOf(sqlType, value)
        }
        return super.notNullValueToDB(value)
    }

}