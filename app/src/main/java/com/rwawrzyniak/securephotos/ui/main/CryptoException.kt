package com.rwawrzyniak.securephotos.ui.main

class CryptoException(private val errorMessage: String, private val ex: Exception) : Exception(ex)

