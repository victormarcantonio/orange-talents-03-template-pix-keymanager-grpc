package br.com.zup.chave.remove

import br.com.zup.PixKeyManagerRemoveGrpcServiceGrpc
import br.com.zup.RemovePixRequest
import br.com.zup.RemovePixResponse
import br.com.zup.chave.ChaveRepository
import io.grpc.Status
import io.grpc.stub.StreamObserver
import javax.inject.Singleton

@Singleton
class RemoveChaveEndpoint (val chaveRepository: ChaveRepository): PixKeyManagerRemoveGrpcServiceGrpc.PixKeyManagerRemoveGrpcServiceImplBase(){
    override fun deletar(request: RemovePixRequest?, responseObserver: StreamObserver<RemovePixResponse>?) {
        val chaveExiste = chaveRepository.existsById(request!!.pixId)
        if(!chaveExiste){
          responseObserver?.onError(Status.NOT_FOUND
              .withDescription("Chave não existe")
              .asRuntimeException())
            return
        }
        chaveRepository.deleteById(request.pixId)
    }
}