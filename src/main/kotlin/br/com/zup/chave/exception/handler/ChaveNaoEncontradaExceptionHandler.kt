package br.com.zup.chave.exception.handler

import br.com.zup.chave.exception.ChaveNaoEncontradaException
import br.com.zup.chave.exception.ExceptionHandler
import io.grpc.Status
import javax.inject.Singleton

@Singleton
class ChaveNaoEncontradaExceptionHandler: ExceptionHandler<ChaveNaoEncontradaException> {

    override fun handle(e: ChaveNaoEncontradaException): ExceptionHandler.StatusWithDetails {
        return ExceptionHandler.StatusWithDetails(
            Status.NOT_FOUND
                .withDescription(e.message)
                .withCause(e)
        )
    }

    override fun supports(e: Exception): Boolean {
        return e is ChaveNaoEncontradaException
    }
}