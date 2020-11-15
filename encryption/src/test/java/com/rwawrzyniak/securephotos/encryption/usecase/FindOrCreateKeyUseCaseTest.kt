package com.rwawrzyniak.securephotos.encryption.usecase

import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.nhaarman.mockitokotlin2.*
import com.rwawrzyniak.securephotos.core.android.ext.toBase64
import com.rwawrzyniak.securephotos.encryption.CryptoParameters
import com.rwawrzyniak.securephotos.encryption.CryptoParameters.ENCRYPTED_SECRET_SHARED_PREFERENCES_KEY
import com.rwawrzyniak.securephotos.encryption.CryptoParameters.ENCRYPTION_ALGORITHM_AES
import com.rwawrzyniak.securephotos.encryption.SecretKeyGenerator
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@RunWith(RobolectricTestRunner::class)
internal class FindOrCreateKeyUseCaseTest {

	@Mock
	private lateinit var sharedPreferences: EncryptedSharedPreferences
	@Mock
	private lateinit var secretKeyGenerator: SecretKeyGenerator

	@Mock
	private lateinit var secretKey: SecretKey

	@Mock
	private lateinit var editor: SharedPreferences.Editor

	@Before
	fun setUp() {
		MockitoAnnotations.openMocks(this)
	}

	@Test
	fun shouldCreateKey() {
		val secretKeyAsByteArray = "base64secretKey".toByteArray()

	editor.stub {
			on { putString(anyOrNull(), anyOrNull()) } doReturn editor
		}

		sharedPreferences.stub {
			on { contains(any()) } doReturn false
			on { getString(any(), anyOrNull()) } doReturn "clearPassword"
			on { edit() } doReturn editor
		}

		secretKeyGenerator.stub {
			on { createSecretKey("clearPassword") } doReturn secretKey
		}

		secretKey.stub {
			on { encoded } doReturn secretKeyAsByteArray
		}


		val sut = sut()
		assertThat(sut.findOrCreateKey()).isEqualTo(secretKey)

		verify(sharedPreferences).contains(KEY_NAME)
		verify(editor).putString(
			KEY_NAME,
			"YmFzZTY0c2VjcmV0S2V5\n"
		)
	}

	@Test
	fun shouldFindKey() {
		val keyAsByteArray = ByteArray(10)
		val base64EncodedKey: String = keyAsByteArray.toBase64()
		sharedPreferences.stub {
			on { contains(any()) } doReturn true
			on { getString(any(), anyOrNull()) } doReturn base64EncodedKey
		}

		val sut = sut()
		assertThat(sut.findOrCreateKey()).isEqualTo(SecretKeySpec(keyAsByteArray, ENCRYPTION_ALGORITHM_AES))
	}

	private fun sut() = FindOrCreateKeyUseCase(secretKeyGenerator, sharedPreferences)

	companion object {
		private const val KEY_NAME = ENCRYPTED_SECRET_SHARED_PREFERENCES_KEY
	}
}
