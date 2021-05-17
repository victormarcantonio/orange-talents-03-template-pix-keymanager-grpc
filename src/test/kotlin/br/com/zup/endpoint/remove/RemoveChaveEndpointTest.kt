package br.com.zup.endpoint.remove

import br.com.zup.PixKeyManagerRemoveGrpcServiceGrpc
import br.com.zup.RemovePixRequest
import br.com.zup.chave.*
import br.com.zup.chave.registra.BcbClient
import br.com.zup.chave.registra.ContaClient
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class RemoveChaveEndpointTest(
     val repository: ChaveRepository,
     val grpcClient: PixKeyManagerRemoveGrpcServiceGrpc.PixKeyManagerRemoveGrpcServiceBlockingStub,
) {


    @field:Inject
    private lateinit var bcbClient: BcbClient


    val chave = Chave(
        UUID.fromString("5b5d5460-ec9e-4c30-add5-1c2fefa6c3bf"),
        TipoChave.CELULAR,
        "111.111.111-11",
        TipoConta.CONTA_CORRENTE,
        Conta(
            "Itaú",
            "12345",
            "Victor Marcatonio",
            "111.111.111-11",
            "1", "123"
        ))

    @BeforeEach
    fun setup(){
        repository.deleteAll()
        repository.save(chave)

    }

    @Test
    internal fun `deve remover chave`() {
        grpcClient.deletar(RemovePixRequest.newBuilder()
            .setClienteId("5b5d5460-ec9e-4c30-add5-1c2fefa6c3bf")
            .setPixId("111.111.111-11")
            .build())
        assertEquals(0,repository.count())
    }


    @Test
    internal fun `nao deve remover caso a chave não pertença ao cliente`() {
        val error = assertThrows<StatusRuntimeException> {
            grpcClient.deletar(RemovePixRequest.newBuilder()
                .setClienteId("5b5d5460-ec9e-4c30-add5-1c2fefa6c3be")
                .setPixId("111.111.111-11")
                .build())
        }

        with(error){
            assertEquals(Status.PERMISSION_DENIED.code, status.code)
        }
    }

    @Test
    internal fun `deve retornar not found caso a chave não exista`() {
        val error = assertThrows<StatusRuntimeException> {
            grpcClient.deletar(
                RemovePixRequest.newBuilder()
                    .setClienteId("5b5d5460-ec9e-4c30-add5-1c2fefa6c3be")
                    .setPixId("111.111.111-12")
                    .build())
        }
        with(error){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave não existe", status.description)
        }
    }

    @MockBean(ContaClient::class)
    fun bcbClient(): BcbClient?{
        return Mockito.mock(BcbClient::class.java)
    }

    @Factory
    class Clients{
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel):PixKeyManagerRemoveGrpcServiceGrpc.PixKeyManagerRemoveGrpcServiceBlockingStub?{
            return PixKeyManagerRemoveGrpcServiceGrpc.newBlockingStub(channel)
        }
    }
}
