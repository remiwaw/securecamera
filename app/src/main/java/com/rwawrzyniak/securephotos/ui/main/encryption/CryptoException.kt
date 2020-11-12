package com.rwawrzyniak.securephotos.ui.main.encryption

class CryptoException(private val errorMessage: String, private val ex: Exception) : Exception(ex)
