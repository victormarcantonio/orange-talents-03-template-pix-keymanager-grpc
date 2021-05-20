package br.com.zup.chave.exception

import io.micronaut.aop.Around
import io.micronaut.context.annotation.Type

@Around
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS,AnnotationTarget.FUNCTION,AnnotationTarget.FILE, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER )
@Type(ExceptionHandlerInterceptor::class)
annotation class ErrorHandler()
