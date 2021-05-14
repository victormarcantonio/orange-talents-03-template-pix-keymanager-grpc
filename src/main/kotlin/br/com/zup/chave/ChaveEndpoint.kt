package br.com.zup.chave

import br.com.zup.PixKeymanagerGrpcServiceGrpc
import br.com.zup.PixRequest
import br.com.zup.PixResponse
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.ConstraintViolationException
import javax.validation.Valid

@Singleton
@Validated
class ChaveEndpoint(val chaveService: ChaveService): PixKeymanagerGrpcServiceGrpc.PixKeymanagerGrpcServiceImplBase() {

    override fun adicionar(request: PixRequest, responseObserver: StreamObserver<PixResponse>) {

        val chaveRequest = request.toModel()

        val chave = chaveService.cadastra(chaveRequest)
        println(chave)


        responseObserver.onNext(PixResponse.newBuilder().setId(chave.chaveId).build())
        responseObserver.onCompleted()


        //val chave = Chave(request.id, request.chave, request.tipoConta)
    }
}
