package br.com.zup.chave.remove

import br.com.zup.*
import br.com.zup.chave.ChaveRepository
import br.com.zup.chave.exception.HttpResponseException
import br.com.zup.chave.registra.BcbClient
import br.com.zup.chave.registra.toModel
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.http.client.exceptions.HttpClientResponseException
import java.util.*
import javax.inject.Singleton

@Singleton
class RemoveChaveEndpoint (val chaveRepository: ChaveRepository, val bcbClient: BcbClient): PixKeyManagerRemoveGrpcServiceGrpc.PixKeyManagerRemoveGrpcServiceImplBase(){
    override fun deletar(request: RemovePixRequest?, responseObserver: StreamObserver<RemovePixResponse>) {
         chaveRepository.findById(request!!.pixId)
            .map {chave->

                val requestDeletaBcb = request.toModel()
                try{
                    bcbClient.deletaBcb(requestDeletaBcb, request.pixId)
                }catch (e: HttpClientResponseException){
                    throw HttpResponseException("erro")
                }
                if(chave.pertenceAoCliente(UUID.fromString(request.clienteId))){
                    chaveRepository.deleteById(request.pixId)
                    responseObserver.onNext(RemovePixResponse.newBuilder().build())
                    responseObserver.onCompleted()

                }else{
                    responseObserver.onError(Status.PERMISSION_DENIED
                        .withDescription("Chave não pertence ao cliente que está tentando removê-la")
                        .asRuntimeException())
                }
            }.orElseGet {
                 responseObserver.onError(Status.NOT_FOUND
                     .withDescription("Chave não existe")
                     .asRuntimeException())
             }
    }
}