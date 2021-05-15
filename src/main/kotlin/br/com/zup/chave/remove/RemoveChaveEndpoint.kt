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
        val possivelChave = chaveRepository.findById(request!!.pixId)
        if(possivelChave.isEmpty){
          responseObserver?.onError(Status.NOT_FOUND
              .withDescription("Chave não existe")
              .asRuntimeException())
            return
        }
        val chave = possivelChave.get()
        if(chave.pertenceAoCliente(request.clienteId)){
            chaveRepository.deleteById(request.pixId)
        }

        responseObserver?.onError(Status.PERMISSION_DENIED
            .withDescription("Chave não pertence ao cliente que está tentando removê-la")
            .asRuntimeException())
        return

    }
}