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
import javax.transaction.Transactional
import kotlin.IllegalArgumentException

@Singleton
@ErrorHandler
class RemoveChaveEndpoint(val chaveRepository: ChaveRepository, val bcbClient: BcbClient) :
    PixKeyManagerRemoveGrpcServiceGrpc.PixKeyManagerRemoveGrpcServiceImplBase() {
    @Transactional
    override fun deletar(request: RemovePixRequest?, responseObserver: StreamObserver<RemovePixResponse>) {


        if(request!!.pixId.isBlank() || request.clienteId.isBlank()){
            throw IllegalArgumentException("Chave ou Cliente ID em branco. Devem ser preenchidos")
        }
         val chave = chaveRepository.findByIdAndClienteId(UUID.fromString(request!!.pixId), UUID.fromString(request.clienteId))
             .orElseThrow {
                 throw ChaveNaoEncontradaException("Chave não encontrada ou não pertence ao cliente")
             }
            chaveRepository.deleteById(UUID.fromString(request.pixId))

        val requestDeletaBcb = DeletaChaveRequest(chave.chave)
        val bcbResponse = bcbClient.deletaBcb(requestDeletaBcb, chave.chave)
        if (bcbResponse.status != HttpStatus.OK) {
            throw IllegalStateException("Erro ao deletar chave no bcb")

        }
        responseObserver.onNext(RemovePixResponse.newBuilder().build())
        responseObserver.onCompleted()

    }
}

