package br.com.zup.endpoint.consulta

import br.com.zup.ConsultaPixRequest
import br.com.zup.PixKeyManagerConsultaServiceGrpc
import br.com.zup.PixKeyManagerRemoveGrpcServiceGrpc
import br.com.zup.chave.*
import br.com.zup.chave.consulta.ConsultaChaveRequest
import br.com.zup.chave.consulta.ConsultaChaveResponse
import br.com.zup.chave.registra.*
import br.com.zup.chave.remove.DeletaChaveRequest
import br.com.zup.client.BcbClient
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.lang.RuntimeException
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
class ConsultaChaveEndpointTest(
    val repository: ChaveRepository,
    val grpcClient: PixKeyManagerConsultaServiceGrpc.PixKeyManagerConsultaServiceBlockingStub
) {


    @field:Inject
    private lateinit var bcbClient: BcbClient

    lateinit var CHAVE_EXISTENTE: Chave


    val chave = Chave(
        UUID.fromString("5b5d5460-ec9e-4c30-add5-1c2fefa6c3bf"),
        TipoChave.CELULAR,
        "46431186877",
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
    fun setup() {
        repository.deleteAll()
        CHAVE_EXISTENTE = repository.save(chave)

    }

    @Test
    internal fun deveRetornarChaveNaConsulta() {
       val response =  grpcClient.consultar(
            ConsultaPixRequest
                .newBuilder()
                .setPixId(
                    ConsultaPixRequest.FiltroPixPorId.newBuilder()
                        .setClienteId("5b5d5460-ec9e-4c30-add5-1c2fefa6c3bf")
                        .setPixId(CHAVE_EXISTENTE.id.toString())
                )
                .build()
        )

        assertEquals(response.chave.chave, CHAVE_EXISTENTE.chave)
    }

    @Test
    internal fun `deve retornar Not Found caso não encontre a chave`() {
        val error =  assertThrows<StatusRuntimeException> {
            grpcClient.consultar(
                ConsultaPixRequest
                    .newBuilder()
                    .setPixId(
                        ConsultaPixRequest.FiltroPixPorId.newBuilder()
                            .setClienteId("5b5d5460-ec9e-4c30-add5-1c2fefa6c3bf")
                            .setPixId(UUID.randomUUID().toString())
                    )
                    .build()
            )
        }
        with(error){
            assertEquals(Status.NOT_FOUND.code, status.code)
        }

    }

    @Test
    internal fun `deve consultar apenas pela chave`() {
       val response = grpcClient.consultar( ConsultaPixRequest
            .newBuilder()
            .setChave("46431186877")
            .build())

      assertEquals(response.chave.chave, CHAVE_EXISTENTE.chave)
      assertEquals(response.chave.dadosConta.nome, CHAVE_EXISTENTE.conta.titular)
    }

    @Test
    internal fun `deve consultar no bcb caso chave nao exista no sistema`() {
        Mockito.`when`(bcbClient.consultaBcb( "26249951024"))
            .thenReturn(HttpResponse.ok(bcbResponse()))

       val response = grpcClient.consultar( ConsultaPixRequest
            .newBuilder()
            .setChave("26249951024")
            .build())

        assertEquals(response.chave.chave, "26249951024")
    }

    @Test
    internal fun `deve lancar NotFound caso a chave nao seja encontrada no bcb`() {
        Mockito.`when`(bcbClient.consultaBcb( "26249951024"))
            .thenReturn(HttpResponse.notFound())

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.consultar(ConsultaPixRequest
                .newBuilder()
                .setChave("26249951024")
                .build())
        }

        with(error){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave não encontrada", status.description)
        }
    }

    @Test
    internal fun `deve lancar IllegalArgumentException caso o filtro seja invalido`() {
        val error = assertThrows<StatusRuntimeException> {
            grpcClient.consultar(ConsultaPixRequest.newBuilder().build())
        }


        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Chave inválida ou não informada", status.description)
        }
    }

    private fun consultaRequest(): ConsultaChaveRequest {
        return ConsultaChaveRequest(
            clienteId = "5b5d5460-ec9e-4c30-add5-1c2fefa6c3bf",
            pixId = CHAVE_EXISTENTE.id.toString(),
            chavePix = null
        )
    }

    fun bcbResponse(): ConsultaChaveResponse {
        return ConsultaChaveResponse(
            keyType = PixKeyType.CPF,
            key = "26249951024",
            bankAccount = BankAccountResponse(
                participant = "60701190",
                branch = "1234",
                accountNumber = "123",
                accountType = AccountType.CACC
            ),
            owner = OwnerResponse(
                type = TipoPessoa.NATURAL_PERSON,
                name = "Victor",
                taxIdNumber = "26249951024"
            ),
            createdAt = LocalDateTime.now()
        )
    }

    @MockBean(BcbClient::class)
    fun bcbClient(): BcbClient? {
        return Mockito.mock(BcbClient::class.java)
    }


    @Factory
    class Clients{
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel):PixKeyManagerConsultaServiceGrpc.PixKeyManagerConsultaServiceBlockingStub?{
            return PixKeyManagerConsultaServiceGrpc.newBlockingStub(channel)
        }
    }
}