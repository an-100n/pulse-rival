package com.pulserival.identity.dto

import com.pulserival.identity.entity.Sex
import java.time.LocalDate

data class UpdateUserProfileCommand(
    val sex: Sex? = null,
    val heightCm: Double? = null,
    val weightKg: Double? = null,
    val birthDate: LocalDate? = null,
    val timezone: String? = null
)
