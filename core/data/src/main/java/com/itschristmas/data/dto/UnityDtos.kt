package com.itschristmas.data.dto

import kotlinx.serialization.Serializable

// 공용 하위 데이터 (Unity 구조체 대응)
@Serializable
data class Vector3Dto(
    val x: Float,
    val y: Float,
    val z: Float
)

@Serializable
data class ColorDto(
    val r: Float,
    val g: Float,
    val b: Float,
    val a: Float
)

// 전체 씬 데이터
@Serializable
data class SceneDto(
    val objects: List<ObjectDto> = emptyList(),
    val texts: List<TextDto> = emptyList()
)

@Serializable
data class ObjectDto(
    val id: String,
    val prefabName: String,
    val position: Vector3Dto, // Unity: {"x":0.0, "y":0.0, "z":0.0}
    val scale: Int
)

@Serializable
data class TextDto(
    val id: String,
    val position: Vector3Dto,
    val textContent: String,
    val fontFamilyName: String,
    val fontSize: Float,
    val color: ColorDto,      // Unity: {"r":1.0, "g":1.0, "b":1.0, "a":1.0}
    val textAlignInt: Int
)

@Serializable
data class UpdateDto<T>(
    val id: String,
    val value: T
)