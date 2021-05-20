package br.com.zup.chave.exception.handler

import br.com.zup.chave.exception.ChaveNaoPertenceException
import br.com.zup.chave.exception.ExceptionHandler
import io.grpc.Status
import javax.inject.Singleton

@Singleton
class ChaveNaoPertenceExceptionHandler: ExceptionHandler<ChaveNaoPertenceException> {

    override fun handle(e: ChaveNaoPertenceException): ExceptionHandler.StatusWithDetails {
        return ExceptionHandler.StatusWithDetails(
            Status.PERMISSION_DENIED
                .withDescription(e.message)
                .withCause(e)
        )
    }

    override fun supports(e: Exception): Boolean {
        return e is ChaveNaoPertenceException
    }
}