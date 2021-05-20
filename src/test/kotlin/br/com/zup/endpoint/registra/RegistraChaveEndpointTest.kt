package br.com.zup.endpoint.registra

import br.com.zup.PixKeymanagerRegistraGrpcServiceGrpc
import br.com.zup.PixRequest
import br.com.zup.TipoChave
import br.com.zup.TipoConta
import br.com.zup.chave.Chave
import br.com.zup.chave.ChaveRepository
import br.com.zup.chave.Conta
import br.com.zup.chave.TipoPessoa
import br.com.zup.chave.registra.*
import br.com.zup.client.BcbClient
import br.com.zup.client.ContaClient
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
import java.lang.IllegalArgumentException
import java.time.LocalDateTime
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

    @field:Inject
    private lateinit var bcbClient: BcbClient


    @BeforeEach
     fun setup(){
        repository.deleteAll()
    }

    val conta = Conta(
        "Itaú",
        "12345",
        "Victor Marcatonio",
        "111.111.111-11",
        "1", "123"
    )

    val chave =  Chave(UUID.randomUUID(),
        br.com.zup.chave.TipoChave.CELULAR,
        "111.111.111-11",
        br.com.zup.chave.TipoConta.CONTA_CORRENTE,
        conta
    )

    @Test
    internal fun `deve cadastrar uma chave pix`() {


        `when`(contaClient.consulta(CLIENTE_ID,"CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(dadosContaResponse()))

        `when`(bcbClient.cadastraBcb(bcbRequest()))
            .thenReturn(HttpResponse.created(createPixKeyResponse()))


        val response = grpcClient.adicionar(PixRequest.newBuilder()
            .setId(CLIENTE_ID.toString())
            .setChave("11111111111")
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .setTipoChave(TipoChave.CPF)
            .setTipoPessoa(br.com.zup.TipoPessoa.NATURAL_PERSON)
            .build())

        assertNotNull(response.id)
        assertTrue(repository.existsById((UUID.fromString(response.id))))
    }

    @Test
    internal fun `deve cadastrar chave aleatória`() {

        val request = ChaveRequestBcb(
            keyType = PixKeyType.RANDOM,
            key = "",
            bankAccount = BankAccountRequest(
                participant = "60701190",
                branch = "1234",
                accountNumber = "123456",
                accountType = AccountType.CACC
            ),
            owner = OwnerRequest(
                type = TipoPessoa.NATURAL_PERSON,
                name = "Victor",
                taxIdNumber = "11111111111"
            )
        )

        val responseBcb = CreatePixKeyResponse(
            keyType = PixKeyType.RANDOM,
            key ="4f2ad454-b8a5-4ccf-b0cf-0d4efb7cf473",
            bankAccount = BankAccountResponse(
                participant = "60701190",
                branch = "1234",
                accountNumber = "123",
                accountType = AccountType.CACC
            ),
            owner = OwnerResponse(
                type = TipoPessoa.NATURAL_PERSON,
                name = "Victor",
                taxIdNumber = "11111111111"
            ),
            createdAt = LocalDateTime.now()
        )

        `when`(contaClient.consulta(CLIENTE_ID,"CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(dadosContaResponse()))

        `when`(bcbClient.cadastraBcb(request))
            .thenReturn(HttpResponse.created(responseBcb))


        val response = grpcClient.adicionar(PixRequest.newBuilder()
            .setId(CLIENTE_ID.toString())
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .setTipoChave(TipoChave.ALEATORIA)
            .setTipoPessoa(br.com.zup.TipoPessoa.NATURAL_PERSON)
            .build())

        assertNotNull(response.id)
        println(response.id)
        assertTrue(repository.existsById((UUID.fromString(response.id))))
    }

    @Test
    internal fun `Não deve cadastrar chave caso já exista`() {
          repository.save(chave
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
                .setTipoPessoa(br.com.zup.TipoPessoa.NATURAL_PERSON)
                .build())
        }

        with(error) {

            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    @Test
    internal fun `nao deve cadastrar chave caso dados estejam em branco`() {

         assertThrows<IllegalArgumentException> {
            grpcClient.adicionar(PixRequest.newBuilder()
                .setId("")
                .setChave("")
                .setTipoConta(TipoConta.valueOf(""))
                .setTipoChave(TipoChave.valueOf(""))
                .build())
        }


    }


    @Test
    internal fun `nao deve cadastrar caso a chave informada não corresponda ao seu próprio tipo informado`() {


        `when`(contaClient.consulta(CLIENTE_ID,"CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(dadosContaResponse()))

        `when`(bcbClient.cadastraBcb(bcbRequest()))
            .thenReturn(HttpResponse.created(createPixKeyResponse()))
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

    @Test
    internal fun `nao deve cadastrar caso ocorra erro no bcb`() {


        `when`(contaClient.consulta(CLIENTE_ID,"CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(dadosContaResponse()))


        `when`(bcbClient.cadastraBcb(bcbRequest()))
            .thenReturn(HttpResponse.badRequest())

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.adicionar(PixRequest.newBuilder()
                .setId("c56dfef4-7901-44fb-84e2-a2cefb157890")
                .setChave("11111111111")
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .setTipoChave(TipoChave.CPF)
                .setTipoPessoa(br.com.zup.TipoPessoa.NATURAL_PERSON)
                .build())
        }

        with(error) {

            assertEquals(Status.FAILED_PRECONDITION.code,status.code )
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

        val CLIENTE_ID = "c56dfef4-7901-44fb-84e2-a2cefb157890"
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
     fun dadosContaResponse(): ContaResponse{
        return ContaResponse(
            tipo="CONTA_CORRENTE",
            instituicao = InstituicaoResponse("ITAU", "60701190"),
            agencia = "1234",
            numero = "123456",
            titular = TitularResponse(UUID.randomUUID().toString(),"Victor", "11111111111")
        )
    }

    private fun bcbRequest(): ChaveRequestBcb{
        return ChaveRequestBcb(
            keyType = PixKeyType.CPF,
            key = "11111111111",
            bankAccount = bankAccountRequest(),
            owner = owner()
        )
    }

    fun bankAccountRequest(): BankAccountRequest{
        return BankAccountRequest(
            participant = "60701190",
            branch = "1234",
            accountNumber = "123456",
            accountType = AccountType.CACC
        )
    }

    fun owner(): OwnerRequest{
        return OwnerRequest(
            type = TipoPessoa.NATURAL_PERSON,
            name = "Victor",
            taxIdNumber = "11111111111"
        )
    }


     fun createPixKeyResponse(): CreatePixKeyResponse {
        return CreatePixKeyResponse(
            keyType = PixKeyType.CPF,
            key = "11111111111",
            bankAccount = BankAccountResponse(
                participant = "60701190",
                branch = "1234",
                accountNumber = "123",
                accountType = AccountType.CACC
            ),
            owner = OwnerResponse(
                type = TipoPessoa.NATURAL_PERSON,
                name = "Victor",
                taxIdNumber = "11111111111"
            ),
            createdAt = LocalDateTime.now()
        )
    }


    @MockBean(ContaClient::class)
    fun contaClient(): ContaClient?{
        return Mockito.mock(ContaClient::class.java)
    }

    @MockBean(BcbClient::class)
    fun bcbClient(): BcbClient?{
        return Mockito.mock(BcbClient::class.java)
    }

    @Factory
    class Clients{
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): PixKeymanagerRegistraGrpcServiceGrpc.PixKeymanagerRegistraGrpcServiceBlockingStub?{
            return PixKeymanagerRegistraGrpcServiceGrpc.newBlockingStub(channel)
        }
    }

}
