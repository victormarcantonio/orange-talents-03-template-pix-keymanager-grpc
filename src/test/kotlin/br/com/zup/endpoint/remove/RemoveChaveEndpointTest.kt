package br.com.zup.endpoint.remove

import br.com.zup.PixKeyManagerRemoveGrpcServiceGrpc
import br.com.zup.RemovePixRequest
import br.com.zup.chave.*
import br.com.zup.chave.remove.DeletaChaveRequest
import br.com.zup.client.BcbClient
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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

    lateinit var CHAVE_EXISTENTE: Chave



    val chave = Chave(
        UUID.fromString("5b5d5460-ec9e-4c30-add5-1c2fefa6c3bf"),
        TipoChave.CELULAR,
        "11111111111",
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
        CHAVE_EXISTENTE = repository.save(chave)

    }

    @Test
    internal fun `deve remover chave`() {

        Mockito.`when`(bcbClient.deletaBcb(bcbRequest(), "11111111111"))
            .thenReturn(HttpResponse.ok())
        grpcClient.deletar(RemovePixRequest.newBuilder()
            .setClienteId("5b5d5460-ec9e-4c30-add5-1c2fefa6c3bf")
            .setPixId(CHAVE_EXISTENTE.id.toString())
            .build())
        assertEquals(0,repository.count())
    }


    @Test
    internal fun `nao deve remover caso a chave não pertença ao cliente`() {
        val error = assertThrows<StatusRuntimeException> {
            grpcClient.deletar(RemovePixRequest.newBuilder()
                .setClienteId("5b5d5460-ec9e-4c30-add5-1c2fefa6c3be")
                .setPixId(CHAVE_EXISTENTE.id.toString())
                .build())
        }

        with(error){
            assertEquals(Status.NOT_FOUND.code, status.code)
        }
    }

    @Test
    internal fun `deve retornar not found caso a chave não exista`() {
        val error = assertThrows<StatusRuntimeException> {
            grpcClient.deletar(
                RemovePixRequest.newBuilder()
                    .setClienteId("5b5d5460-ec9e-4c30-add5-1c2fefa6c3be")
                    .setPixId("ea10b0f0-2ae5-4e40-9fbe-6593922f0462")
                    .build())
        }
        with(error){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave não encontrada ou não pertence ao cliente", status.description)
        }
    }

    @Test
    internal fun `não deve deletar caso ocorra erro no bcb`() {

        Mockito.`when`(bcbClient.deletaBcb(bcbRequest(),"11111111111"))
            .thenReturn(HttpResponse.badRequest())
        val error = assertThrows<StatusRuntimeException> {
            grpcClient.deletar(
                RemovePixRequest.newBuilder()
                    .setClienteId("5b5d5460-ec9e-4c30-add5-1c2fefa6c3bf")
                    .setPixId(CHAVE_EXISTENTE.id.toString())
                    .build())
        }
        with(error){
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
            assertEquals("Erro ao deletar chave no bcb", status.description)
        }
    }

    @Test
    internal fun `nao deve remover caso pix id esteja em branco`() {
        val error = assertThrows<StatusRuntimeException> {
            grpcClient.deletar(
                RemovePixRequest.newBuilder()
                    .setClienteId("5b5d5460-ec9e-4c30-add5-1c2fefa6c3be")
                    .setPixId("")
                    .build())
        }
        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Chave ou Cliente ID em branco. Devem ser preenchidos", status.description)
        }
    }

    @Test
    internal fun `nao deve remover caso cliente id esteja em branco`() {
        val error = assertThrows<StatusRuntimeException> {
            grpcClient.deletar(
                RemovePixRequest.newBuilder()
                    .setClienteId("")
                    .setPixId(CHAVE_EXISTENTE.id.toString())
                    .build())
        }
        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Chave ou Cliente ID em branco. Devem ser preenchidos", status.description)
        }
    }

    private fun bcbRequest(): DeletaChaveRequest {
        return DeletaChaveRequest(
            key = "11111111111"
        )
    }

    @MockBean(BcbClient::class)
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
