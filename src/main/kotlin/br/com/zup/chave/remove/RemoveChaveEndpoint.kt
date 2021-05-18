package br.com.zup.chave.remove

import br.com.zup.*
import br.com.zup.chave.ChaveRepository
import br.com.zup.client.BcbClient
import br.com.zup.chave.registra.toModel
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.http.HttpStatus
import java.util.*
import javax.inject.Singleton

@Singleton
class RemoveChaveEndpoint(val chaveRepository: ChaveRepository, val bcbClient: BcbClient) :
    PixKeyManagerRemoveGrpcServiceGrpc.PixKeyManagerRemoveGrpcServiceImplBase() {
    override fun deletar(request: RemovePixRequest?, responseObserver: StreamObserver<RemovePixResponse>) {
         chaveRepository.findById(UUID.fromString(request!!.pixId))
            .map { chave ->
                if (chave.pertenceAoCliente(UUID.fromString(request.clienteId))) {
                    val requestDeletaBcb = DeletaChaveRequest(chave.chaveId)
                    val bcbResponse = bcbClient.deletaBcb(requestDeletaBcb, chave.chaveId)
                    println(requestDeletaBcb)
                    if (bcbResponse.status != HttpStatus.OK) {
                        responseObserver.onError(
                            Status.FAILED_PRECONDITION
                                .withDescription("Erro ao deletar chave no bcb")
                                .asRuntimeException()
                        )
                    } else {
                        chaveRepository.delete(chave)
                        responseObserver.onNext(RemovePixResponse.newBuilder().build())
                        responseObserver.onCompleted()
                    }

                } else {
                    responseObserver.onError(
                        Status.PERMISSION_DENIED
                            .withDescription("Chave não pertence ao cliente que está tentando removê-la")
                            .asRuntimeException()
                    )
                }
            }.orElseGet {
                responseObserver.onError(
                    Status.NOT_FOUND
                        .withDescription("Chave não existe")
                        .asRuntimeException()
                )
            }
    }
}

