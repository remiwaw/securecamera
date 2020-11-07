package com.rwawrzyniak.securephotos.core

import java.time.LocalDate
import java.time.LocalDateTime

class DateTimeProvider {
	private var fixedDatetime: LocalDateTime? = null

	fun getCurrentDateTime() = fixedDatetime ?: LocalDateTime.now()
	fun getCurrentDate() = fixedDatetime?.toLocalDate() ?: LocalDateTime.now().toLocalDate()

	fun convertEpochToLocalDate(epochDay: Long) = LocalDate.ofEpochDay(epochDay)

	// use only for tests
	fun setCurrentTime(time: LocalDateTime) {
		fixedDatetime = time
	}
}
