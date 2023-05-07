package ru.sulgik.auth.domain

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.sulgik.images.domain.ImageEmbedded

@Entity
class VendorEntity(
    val region: String,
    val realName: String,
    val vendor: String,
    val host: String,
    val devKey: String,
    @Embedded("logo")
    val logo: ImageEmbedded,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}