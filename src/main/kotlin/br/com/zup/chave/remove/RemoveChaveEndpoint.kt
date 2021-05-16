package br.com.zup.chave.remove

import br.com.zup.*
import br.com.zup.chave.ChaveRepository
import io.grpc.Status
import io.grpc.stub.StreamObserver
import java.util.*
import javax.inject.Singleton

@Singleton
class RemoveChaveEndpoint (val chaveRepository: ChaveRepository): PixKeyManagerRemoveGrpcServiceGrpc.PixKeyManagerRemoveGrpcServiceImplBase(){
    override fun deletar(request: RemovePixRequest?, responseObserver: StreamObserver<RemovePixResponse>?) {
         chaveRepository.findById(request!!.pixId)
            .map {chave->
                if(chave.pertenceAoCliente(UUID.fromString(request.clienteId))){
                    chaveRepository.deleteById(request.pixId)
                    responseObserver?.onNext(RemovePixResponse.newBuilder().build())
                    responseObserver!!.onCompleted()
                }
                    responseObserver?.onError(Status.PERMISSION_DENIED
                        .withDescription("Chave não pertence ao cliente que está tentando removê-la")
                        .asRuntimeException())
            }
                responseObserver?.onError(Status.NOT_FOUND
                    .withDescription("Chave não existe")
                    .asRuntimeException())

    }
}