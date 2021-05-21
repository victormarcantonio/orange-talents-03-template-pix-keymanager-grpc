package br.com.zup.endpoint.lista

import br.com.zup.ListaPixRequest
import br.com.zup.PixKeyManagerConsultaServiceGrpc
import br.com.zup.PixKeyManagerListaServiceGrpc
import br.com.zup.chave.*
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.stub.AbstractBlockingStub
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import javax.inject.Singleton
import org.hamcrest.Matcher
import org.hamcrest.Matchers.hasSize

@MicronautTest(transactional = false)
class ListaChaveEndpointTest (
    val chaveRepository: ChaveRepository,
    val grpcClient: PixKeyManagerListaServiceGrpc.PixKeyManagerListaServiceBlockingStub){


    lateinit var CHAVE_EXISTENTE: Chave

    val chave = Chave(
        UUID.fromString("5b5d5460-ec9e-4c30-add5-1c2fefa6c3bf"),
        TipoChave.ALEATORIA,
        "3dd1491a-ae63-4671-b332-a1d2dcab59c0",
        TipoConta.CONTA_CORRENTE,
        Conta(
            "Itaú",
            "12345",
            "Victor Marcatonio",
            "111.111.111-11",
            "1", "123"
        )
    )

    val chave2 = Chave(
        UUID.fromString("5b5d5460-ec9e-4c30-add5-1c2fefa6c3bf"),
        TipoChave.ALEATORIA,
        "1dadf49d-f977-4df1-ae03-9498a6afe5e1",
        TipoConta.CONTA_CORRENTE,
        Conta(
            "Itaú",
            "12345",
            "Victor Marcatonio",
            "111.111.111-11",
            "1", "123"
        )
    )

    val chave3 = Chave(
        UUID.fromString("5b5d5460-ec9e-4c30-add5-1c2fefa6c3bf"),
        TipoChave.CELULAR,
        "b911702f-3166-45f3-a637-5db4194a5f97",
        TipoConta.CONTA_CORRENTE,
        Conta(
            "Itaú",
            "12345",
            "Victor Marcatonio",
            "111.111.111-11",
            "1", "123"
        )
    )

    @BeforeEach
    fun setup(){
        chaveRepository.deleteAll()
        chaveRepository.save(chave)
        chaveRepository.save(chave2)
        chaveRepository.save(chave3)
    }


    @Test
    internal fun `deve listar as chaves do usuario`() {
        val response = grpcClient.listar(ListaPixRequest
            .newBuilder().setClienteId("5b5d5460-ec9e-4c30-add5-1c2fefa6c3bf")
            .build())


        assertThat(response.chavesList,  hasSize(3))
        assertEquals(response.getChaves(0).chave, "3dd1491a-ae63-4671-b332-a1d2dcab59c0")
        assertEquals(response.getChaves(1).chave, "1dadf49d-f977-4df1-ae03-9498a6afe5e1")
        assertEquals(response.getChaves(2).chave, "b911702f-3166-45f3-a637-5db4194a5f97")
    }

    @Test
    internal fun `nao deve listar chaves caso o cliente não possua chaves`() {
        val response = grpcClient.listar(ListaPixRequest
            .newBuilder().setClienteId(UUID.randomUUID().toString())
            .build())

        assertThat(response.chavesList,  hasSize(0))
        assertEquals(0, response.chavesCount)

    }

    @Test
    internal fun `deve lancar erro caso nao seja enviado o clienteId`() {
        val error = assertThrows<StatusRuntimeException> {
            grpcClient.listar(ListaPixRequest
                .newBuilder()
                .setClienteId("")
                .build())
        }

        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Cliente id não informado", status.description)
        }
    }

    @Factory
    class Clients{
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): PixKeyManagerListaServiceGrpc.PixKeyManagerListaServiceBlockingStub?{
            return PixKeyManagerListaServiceGrpc.newBlockingStub(channel)
        }
    }
}


