package br.com.zup.chave.registra

import br.com.zup.PixKeymanagerRegistraGrpcServiceGrpc
import br.com.zup.PixRequest
import br.com.zup.PixResponse
import br.com.zup.chave.Chave
import br.com.zup.chave.ChaveRepository
import br.com.zup.chave.exception.HttpResponseException
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.validation.Validated
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.ConstraintViolationException
import javax.validation.Valid

@Singleton
@Validated
class RegistraChaveEndpoint(val chaveRepository: ChaveRepository, val contaClient: ContaClient, val bcbClient: BcbClient): PixKeymanagerRegistraGrpcServiceGrpc.PixKeymanagerRegistraGrpcServiceImplBase() {


    override fun adicionar(request: PixRequest, responseObserver: StreamObserver<PixResponse>) {

        if(chaveRepository.existsById(request.chave)){
            responseObserver.onError(Status.ALREADY_EXISTS
                .withDescription("Chave já cadastrada")
                .asRuntimeException())
            return
        }

        val chaveRequest = request.toModel()
        try{
            val chave =  registra(chaveRequest)
            responseObserver.onNext(PixResponse.newBuilder().setId(chave.chaveId).build())
            responseObserver.onCompleted()
        }catch (e: ConstraintViolationException){
            responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription("Dados inválidos")
                .asRuntimeException())
            return
        }catch (e: HttpClientResponseException){
            responseObserver.onError(Status.ALREADY_EXISTS
                .withDescription("Chave já encontra-se cadastrada")
                .asRuntimeException())
        }
    }


    @Transactional
    fun registra(@Valid chaveRequest: ChaveRequest): Chave {

        val contaResponse = contaClient.consulta(chaveRequest.clienteId, chaveRequest.tipoConta.toString())
        val requestToBcb = chaveRequest.toChaveRequestBcb(contaResponse = contaResponse?.body())
        val conta = contaResponse?.body()?.toConta() ?: throw IllegalStateException("Cliente não encontrado")
        val chave = chaveRequest.toChave(conta)
        chaveRepository.save(chave)

        val bcbResponse =  bcbClient.cadastraBcb(requestToBcb)
        if(bcbResponse.status != HttpStatus.CREATED){
            throw HttpResponseException ("Erro")
        }
        println(bcbResponse.body()!!.key)
        println(chave.chaveId)
          chave.atualiza(bcbResponse.body()!!.key)

        chaveRepository.update(chave)


        return chave
    }

}