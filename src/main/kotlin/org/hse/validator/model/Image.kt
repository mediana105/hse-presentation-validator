package org.hse.validator.model

import org.apache.poi.sl.usermodel.PictureData

data class Image(
    val data: List<Byte>,
    var fileName: String? = null,
    var type: PictureData.PictureType? = null,
    var width: Double = 0.0,
    var height: Double = 0.0
)