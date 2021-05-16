package br.com.zup.endpoint.registra

import br.com.zup.PixKeymanagerRegistraGrpcServiceGrpc
import br.com.zup.PixRequest
import br.com.zup.TipoChave
import br.com.zup.TipoConta
import br.com.zup.chave.Chave
import br.com.zup.chave.ChaveRepository
import br.com.zup.chave.Conta
import br.com.zup.chave.registra.*
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.util.*
import java.util.stream.Stream
import javax.inject.Inject
import javax.inject.Singleton


@MicronautTest(transactional = false)
internal class RegistraChaveEndpointTest(
    private val repository: ChaveRepository,
    private val grpcClient: PixKeymanagerRegistraGrpcServiceGrpc.PixKeymanagerRegistraGrpcServiceBlockingStub
) {


    @field:Inject
    private lateinit var contaClient: ContaClient


    @BeforeEach
     fun setup(){
        repository.deleteAll()
    }


    @ParameterizedTest
    @MethodSource("retornaChaves")
    internal fun `deve cadastrar uma chave pix`(chave: String, tipoChave: TipoChave) {

        `when`(contaClient.consulta(CLIENTE_ID.toString(),"CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(dadosContaResponse()))

        val response = grpcClient.adicionar(PixRequest.newBuilder()
            .setId(CLIENTE_ID.toString())
            .setChave(chave)
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .setTipoChave(tipoChave)
            .build())

        assertNotNull(response.id)
        assertTrue(repository.existsById(response.id))
    }

    @Test
    internal fun `Não deve cadastrar chave caso já exista`() {
          repository.save(
            Chave(
                UUID.randomUUID(),
                br.com.zup.chave.TipoChave.CELULAR,
                "111.111.111-11",
                br.com.zup.chave.TipoConta.CONTA_CORRENTE,
                Conta(
                    "Itaú",
                    "12345",
                    "Victor Marcatonio",
                    "111.111.111-11",
                    "1", "123"
                )
            )
        )
        val error = assertThrows<StatusRuntimeException> {
            grpcClient.adicionar(PixRequest.newBuilder()
                .setId(CLIENTE_ID.toString())
                .setChave("111.111.111-11")
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .setTipoChave(TipoChave.CPF)
                .build())
        }
        with(error){
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals("Chave já cadastrada", status.description)
        }
    }


    @ParameterizedTest
    @MethodSource("retornaChavesInvalidas")
    internal fun `nao deve cadastrar chave caso exista algum dado inválido`(chave: String, tipoChave: TipoChave) {

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.adicionar(PixRequest.newBuilder()
                .setId("c56dfef4-7901-44fb-84e2-a2cefb157890")
                .setChave(chave)
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .setTipoChave(tipoChave)
                .build())
        }

        with(error) {

            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }


    @Test
    internal fun `nao deve cadastrar caso a chave informada não corresponda ao seu próprio tipo informado`() {
        val error = assertThrows<StatusRuntimeException> {
            grpcClient.adicionar(PixRequest.newBuilder()
                .setId("c56dfef4-7901-44fb-84e2-a2cefb157890")
                .setChave("11111111111")
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .setTipoChave(TipoChave.CELULAR)
                .build())
        }

        with(error) {

            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    fun retornaTiposDeChave(): Stream<Arguments?>? {
        return Stream.of(
            Arguments.of(TipoChave.CPF, true),
            Arguments.of(TipoChave.CELULAR, true),
            Arguments.of(TipoChave.EMAIL, true),
            Arguments.of(TipoChave.ALEATORIA, true)
        )
    }

    companion object{

        val CLIENTE_ID = UUID.randomUUID()
        @JvmStatic
        fun retornaChaves(): Stream<Arguments?>? {
            return Stream.of(
                Arguments.of("57902588083",TipoChave.CPF ),
                Arguments.of("+5585988714077", TipoChave.CELULAR),
                Arguments.of("victor@email.com", TipoChave.EMAIL),
                Arguments.of("", TipoChave.ALEATORIA)
            )
        }

        @JvmStatic
        fun retornaChavesInvalidas():  Stream<Arguments?>?{
            return Stream.of(
                Arguments.of("111.111.111-1w",TipoChave.CPF ),
                Arguments.of("123", TipoChave.CELULAR),
                Arguments.of("victoremail.com", TipoChave.EMAIL),
            )
        }
    }
    private fun dadosContaResponse(): ContaResponse{
        return ContaResponse(
            tipo="CONTA_CORRENTE",
            instituicao = InstituicaoResponse("ITAU", "1234"),
            agencia = "1234",
            numero = "123456",
            titular = TitularResponse(UUID.randomUUID().toString(),"Victor", "519.491.250-17")
        )
    }

    @MockBean(ContaClient::class)
    fun contaClient(): ContaClient?{
        return Mockito.mock(ContaClient::class.java)
    }

    @Factory
    class Clients{
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): PixKeymanagerRegistraGrpcServiceGrpc.PixKeymanagerRegistraGrpcServiceBlockingStub?{
            return PixKeymanagerRegistraGrpcServiceGrpc.newBlockingStub(channel)
        }
    }

}
