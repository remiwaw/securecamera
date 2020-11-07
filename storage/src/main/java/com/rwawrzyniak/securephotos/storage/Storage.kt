package com.rwawrzyniak.securephotos.storage

import java.io.IOException

interface Storage {
    /**
     * Writes all contents fully
     */
    @Throws(IOException::class)
    fun write(byteArray: ByteArray)

    /**
     * Reads all contents fully
     */
    @Throws(IOException::class)
    fun read(): ByteArray

    /**
     * Removes the underlying storage file
     */
    fun remove(): Boolean

    /**
     * Identify this storage with a speaking name (e.g. the underlying file's basename)
     */
    fun identify(): String

    /**
     * Returns true if the underlying files exists, false otherwise
     */
    fun exists(): Boolean
}
