package com.rwawrzyniak.securephotos.core.test

import androidx.annotation.VisibleForTesting
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.rules.TestWatcher
import org.junit.runner.Description

// source: https://proandroiddev.com/from-rxjava-to-kotlin-flow-testing-42f1641d8433

@InternalCoroutinesApi
fun <T> Flow<T>.test(scope: CoroutineScope): TestObserver<T> {
    return TestObserver(scope, this)
}

@InternalCoroutinesApi
class TestObserver<T>(
    scope: CoroutineScope,
    flow: Flow<T>
) {
    private val values = mutableListOf<T>()

	private val job: Job = scope.launch {
        flow.collect { values.add(it) }
    }
    fun assertNoValues(): TestObserver<T> {
        assertEquals(emptyList<T>(), this.values)
        return this
    }
    fun assertValues(vararg values: T): TestObserver<T> {
        assertEquals(values.toList(), this.values)
        return this
    }
    fun finish() {
        job.cancel()
    }
}

@VisibleForTesting fun app() = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
