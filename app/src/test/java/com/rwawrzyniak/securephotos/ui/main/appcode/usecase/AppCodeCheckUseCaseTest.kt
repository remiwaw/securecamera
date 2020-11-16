package com.rwawrzyniak.securephotos.ui.main.appcode.usecase

import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.nhaarman.mockitokotlin2.*
import com.rwawrzyniak.securephotos.core.android.Constants
import com.rwawrzyniak.securephotos.core.android.Constants.PLAIN_TEXT_PASSWORD
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

internal class AppCodeCheckUseCaseTest {

	@Mock
	private lateinit var sharedPreferences: EncryptedSharedPreferences

	@Mock
	private lateinit var editor: SharedPreferences.Editor

	@Before
	fun setUp() {
		MockitoAnnotations.openMocks(this)
		sharedPreferences.stub {
			on { edit() } doReturn editor
		}

		editor.stub {
			on { putString(anyOrNull(), anyOrNull()) } doReturn editor
		}
	}

    @Test
    fun shouldInitializeAppCodeIfNoneAndCheck() {
		sharedPreferences.stub {
			on { contains(any()) } doReturn false
			on { getString(any(), anyOrNull()) } doReturn PLAIN_TEXT_PASSWORD
		}
		val sut = sut()
		assertThat(sut.isCorrectAppCode(Constants.PLAIN_TEXT_PASSWORD)).isTrue()
		assertThat(sut.isCorrectAppCode("IncorrectPass")).isFalse()

		verify(sharedPreferences).contains(Constants.ENCRYPTED_PREFS_APP_CODE_KEY)
		verify(editor).putString(
			Constants.ENCRYPTED_PREFS_APP_CODE_KEY,
			Constants.PLAIN_TEXT_PASSWORD
		)
    }

	@Test
	fun shouldGetAppCodeFromPreferencesAndCheck() {
		sharedPreferences.stub {
			on { contains(any()) } doReturn true
			on { getString(any(), anyOrNull()) } doReturn PLAIN_TEXT_PASSWORD
		}
		val sut = sut()
		assertThat(sut.isCorrectAppCode(Constants.PLAIN_TEXT_PASSWORD)).isTrue()
		assertThat(sut.isCorrectAppCode("IncorrectPass")).isFalse()

		verify(sharedPreferences).contains(Constants.ENCRYPTED_PREFS_APP_CODE_KEY)
		verify(editor, never()).putString(
			Constants.ENCRYPTED_PREFS_APP_CODE_KEY,
			Constants.PLAIN_TEXT_PASSWORD
		)
	}

	private fun sut() = AppCodeCheckUseCase(sharedPreferences)
}
