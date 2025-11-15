package com.example.database

import androidx.room.TypeConverter
import com.example.database.entity.AssetType
import com.example.database.entity.ElementType
import com.example.database.entity.TextAlign

class Converters {

    @TypeConverter
    fun fromAssetType(type: AssetType): String = type.name

    @TypeConverter
    fun toAssetType(value: String): AssetType = AssetType.valueOf(value)

    @TypeConverter
    fun fromElementType(type: ElementType): String = type.name

    @TypeConverter
    fun toElementType(value: String): ElementType = ElementType.valueOf(value)

    @TypeConverter
    fun fromTextAlign(type: TextAlign?): String? = type?.name

    @TypeConverter
    fun toTextAlign(value: String?): TextAlign? = value?.let { TextAlign.valueOf(it) }
}