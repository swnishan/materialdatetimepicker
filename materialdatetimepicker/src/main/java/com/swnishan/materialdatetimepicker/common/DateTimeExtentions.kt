package com.swnishan.materialdatetimepicker.common

import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId

fun LocalDate.toLong() = LocalDateTime.of(this.year, this.monthValue, this.dayOfMonth, 0, 0, 0)
    .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

fun Long.toLocalDate(): LocalDate = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()