package br.com.zup.chave.remove

import br.com.zup.*
import br.com.zup.chave.ChaveRepository
import br.com.zup.chave.exception.ChaveNaoEncontradaException
import br.com.zup.chave.exception.ChaveNaoPertenceException
import br.com.zup.chave.exception.ErrorHandler
import br.com.zup.client.BcbClient
import br.com.zup.chave.registra.toModel
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.http.HttpStatus
import java.util.*
import javax.inject.Singleton

@Singleton
@ErrorHandler
class RemoveChaveEndpoint(val chaveRepository: ChaveRepository, val bcbClient: BcbClient) :
    PixKeyManagerRemoveGrpcServiceGrpc.PixKeyManagerRemoveGrpcServiceImplBase() {
    override fun deletar(request: RemovePixRequest?, responseObserver: StreamObserver<RemovePixResponse>) {
         chaveRepository.findById(UUID.fromString(request!!.pixId))
            .map { chave ->
                if (chave.pertenceAoCliente(UUID.fromString(request.clienteId))) {
                    val requestDeletaBcb = DeletaChaveRequest(chave.chave)
                    val bcbResponse = bcbClient.deletaBcb(requestDeletaBcb, chave.chave)
                    println(requestDeletaBcb)
                    if (bcbResponse.status != HttpStatus.OK) {
                        throw IllegalStateException("Erro ao deletar chave no bcb")

                    } else {
                        chaveRepository.delete(chave)
                        responseObserver.onNext(RemovePixResponse.newBuilder().build())
                        responseObserver.onCompleted()
                    }

                } else {
                   throw ChaveNaoPertenceException("Chave não pertence ao cliente")
                }
            }.orElseGet {
               throw ChaveNaoEncontradaException("Chave não existe")
            }
    }
}

