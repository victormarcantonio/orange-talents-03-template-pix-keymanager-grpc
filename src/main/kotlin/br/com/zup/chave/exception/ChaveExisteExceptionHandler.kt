package br.com.zup.chave.exception

import io.grpc.Status
import javax.inject.Singleton

@Singleton
class ChaveExisteExceptionHandler: ExceptionHandler<ChaveExisteException> {
    override fun handle(e: ChaveExisteException): ExceptionHandler.StatusWithDetails {
        return ExceptionHandler.StatusWithDetails(
            Status.ALREADY_EXISTS
                .withDescription(e.message)
                .withCause(e)
        )
    }

    override fun supports(e: Exception): Boolean {
        return e is ChaveExisteException
    }
}