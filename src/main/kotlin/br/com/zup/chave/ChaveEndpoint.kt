package br.com.zup.chave

import br.com.zup.PixKeymanagerGrpcServiceGrpc
import br.com.zup.PixRequest
import br.com.zup.PixResponse
import br.com.zup.chave.registra.ChaveRequest
import br.com.zup.chave.registra.ContaClient
import br.com.zup.chave.registra.toModel
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.validation.Validated
import javax.inject.Singleton
import javax.validation.ConstraintViolationException
import javax.validation.Valid

@Singleton
@Validated
class ChaveEndpoint(val chaveRepository: ChaveRepository, val contaClient: ContaClient): PixKeymanagerGrpcServiceGrpc.PixKeymanagerGrpcServiceImplBase() {

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
        }
    }


    fun registra(@Valid chaveRequest: ChaveRequest): Chave{

        val contaResponse = contaClient.consulta(chaveRequest.clienteId, chaveRequest.tipoConta.toString())

        val conta = contaResponse?.body()?.toConta() ?: throw IllegalStateException("Cliente não encontrado")

        val chave = chaveRequest.toChave(conta)
        chaveRepository.save(chave)

        return chave
    }

}
