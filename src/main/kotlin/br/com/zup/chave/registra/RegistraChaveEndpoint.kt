package br.com.zup.chave.registra

import br.com.zup.PixKeymanagerRegistraGrpcServiceGrpc
import br.com.zup.PixRequest
import br.com.zup.PixResponse
import br.com.zup.chave.Chave
import br.com.zup.chave.ChaveRepository
import br.com.zup.chave.exception.ChaveExisteException
import br.com.zup.chave.exception.ErrorHandler
import br.com.zup.client.BcbClient
import br.com.zup.client.ContaClient
import io.grpc.stub.StreamObserver
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid
import javax.validation.Validator


@Singleton
@ErrorHandler
@Validated
class RegistraChaveEndpoint(
    val chaveRepository: ChaveRepository,
    val contaClient: ContaClient,
    val bcbClient: BcbClient,
    val validator: Validator
) : PixKeymanagerRegistraGrpcServiceGrpc.PixKeymanagerRegistraGrpcServiceImplBase() {

    private val LOGGER = LoggerFactory.getLogger(this.javaClass)


    @Transactional
    override fun adicionar(request: PixRequest, responseObserver: StreamObserver<PixResponse>) {

        if (chaveRepository.existsByChave(request.chave)) {
            throw ChaveExisteException("Chave já cadastrada")
        }
        val chaveRequest = request.toModel(validator)

        val chave = registra(chaveRequest)
        responseObserver.onNext(PixResponse.newBuilder().setId(chave.id.toString()).build())
        responseObserver.onCompleted()

    }

    @Transactional
    private fun registra(@Valid chaveRequest: ChaveRequest): Chave {

        val contaResponse = contaClient.consulta(chaveRequest.clienteId, chaveRequest.tipoConta.toString())
        val conta = contaResponse?.body()?.toConta() ?: throw IllegalStateException("Cliente não encontrado")
        val chave = chaveRequest.toChave(conta)
        chaveRepository.save(chave)
        val requestToBcb = chaveRequest.toChaveRequestBcb(chave)



        println(requestToBcb)
        val bcbResponse = bcbClient.cadastraBcb(requestToBcb)

        println(bcbResponse.body())
        if (bcbResponse.status != HttpStatus.CREATED) {
            throw IllegalStateException("Erro ao cadastrar chave no bcb")
        }
        chave.atualiza(bcbResponse.body()!!.key)
        return chave
    }

}