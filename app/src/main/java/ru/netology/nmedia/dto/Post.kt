package ru.netology.nmedia.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import ru.netology.nmedia.enumeration.AttachmentType
@Parcelize
data class Post(
    val id: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    var likedByMe: Boolean = false,
    var likes: Int = 0,
    var shares: Int = 0,
    val video: String? = null,
    val attachment: @RawValue Attachment? = null,
) : Parcelable

data class Attachment(
    val url: String,
    val type: AttachmentType,
)