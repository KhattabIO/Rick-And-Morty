package com.slayer.data.dto


import com.slayer.domain.repositories.models.Character
import com.squareup.moshi.Json

data class CharacterResult(
    @Json(name = "created")
    val created: String?,
    @Json(name = "episode")
    val episode: List<String?>?,
    @Json(name = "gender")
    val gender: String?,
    @Json(name = "id")
    val id: Int?,
    @Json(name = "image")
    val image: String?,
    @Json(name = "location")
    val location: Location?,
    @Json(name = "name")
    val name: String?,
    @Json(name = "origin")
    val origin: Origin?,
    @Json(name = "species")
    val species: String?,
    @Json(name = "status")
    val status: String?,
    @Json(name = "type")
    val type: String?,
    @Json(name = "url")
    val url: String?
) {
    companion object {
        fun CharacterResult.toCharacter(): Character {
            return Character(
                id = id ?: -1,
                image = image ?: "",
                name = name ?: "",
                type = name ?: "",
                state = status ?: ""
            )
        }
    }
}