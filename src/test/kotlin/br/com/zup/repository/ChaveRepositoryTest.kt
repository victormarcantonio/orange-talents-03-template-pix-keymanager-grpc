package br.com.zup.repository

import br.com.zup.chave.Chave
import br.com.zup.chave.ChaveRepository
import br.com.zup.chave.registra.Conta
import br.com.zup.chave.registra.TipoChave
import br.com.zup.chave.registra.TipoConta
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import javax.inject.Singleton

@MicronautTest(transactional = false)
class ChaveRepositoryTest (
    val repository: ChaveRepository
){


    @BeforeEach
    fun setup(){
        repository.deleteAll()
    }


    @Test
    internal fun `deve cadastrar chave pix`() {
        repository.save(
            Chave(
                UUID.randomUUID(),
                TipoChave.CELULAR,
                "111.111.11-11",
                TipoConta.CONTA_CORRENTE,
                Conta(
                    "Itaú",
                    "12345",
                    "Victor Marcatonio",
                    "111.111.111-11",
                    "1", "123"
                )
            )
        )


        assertEquals(1, repository.count())
    }

    @Test
    internal fun `deve retornar true caso chave já exista`() {
        repository.save(
            Chave(
                UUID.randomUUID(),
                TipoChave.CELULAR,
                "111.111.11-11",
                TipoConta.CONTA_CORRENTE,
                Conta(
                    "Itaú",
                    "12345",
                    "Victor Marcatonio",
                    "111.111.111-11",
                    "1", "123"
                )
            )
        )
        assertTrue(repository.existsById("111.111.11-11"))
    }
}