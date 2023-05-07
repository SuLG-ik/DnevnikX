package ru.sulgik.auth.domain

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import ru.sulgik.images.domain.ImageEmbedded

@Entity(
    indices = [
        Index(
            value = ["accountId"],
            unique = true,
        )
    ],
    foreignKeys = [
        ForeignKey(
            entity = AuthVendorEntity::class,
            parentColumns = ["region"],
            childColumns = ["region"],
        )
    ]
)
class AuthEntity(
    @PrimaryKey val accountId: String,
    val token: String,
    @ColumnInfo(defaultValue = "NULL")
    val region: String? = null,
)

class AuthWithVendor(
    @Embedded
    val auth: AuthEntity,
    @Relation(
        parentColumn = "region",
        entityColumn = "region",
    )
    val vendor: AuthVendorEntity?,
)

@Entity(
    indices = [
        Index(
            value = ["region"],
            unique = true,
        )
    ]
)
class AuthVendorEntity(
    @PrimaryKey
    val region: String,
    val vendor: String,
    val realName: String,
    val host: String,
    val devKey: String,
    @Embedded("logo")
    val logo: ImageEmbedded,
)