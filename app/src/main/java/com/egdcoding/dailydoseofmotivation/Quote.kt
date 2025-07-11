package com.egdcoding.dailydoseofmotivation

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quotes")
data class Quote(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val isFavorite: Boolean = false,
    val likeOrWritten: String
)
