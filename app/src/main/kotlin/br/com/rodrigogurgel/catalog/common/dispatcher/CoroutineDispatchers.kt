package br.com.rodrigogurgel.catalog.common.dispatcher

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.slf4j.MDCContext
import java.util.concurrent.Executors

val controllerDispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher() + MDCContext()
