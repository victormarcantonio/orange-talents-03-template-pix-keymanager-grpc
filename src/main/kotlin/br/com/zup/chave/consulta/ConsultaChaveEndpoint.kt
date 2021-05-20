package br.com.zup.chave.consulta

import br.com.zup.ConsultaPixRequest
import br.com.zup.ConsultaPixResponse
import br.com.zup.PixKeyManagerConsultaServiceGrpc
import br.com.zup.chave.ChaveRepository
import br.com.zup.chave.exception.ErrorHandler
import br.com.zup.client.BcbClient
import io.grpc.stub.StreamObserver
import io.micronaut.validation.Validated
import javax.inject.Singleton
import javax.validation.Validator

@Singleton
@Validated
@ErrorHandler
class ConsultaChaveEndpoint(
    val chaveRepository: ChaveRepository,
    val bcbClient: BcbClient,
    val validator: Validator
): PixKeyManagerConsultaServiceGrpc.PixKeyManagerConsultaServiceImplBase() {

    override fun consultar(request: ConsultaPixRequest, responseObserver: StreamObserver<ConsultaPixResponse>) {

        val filtro = request.toModel(validator)
        val chaveInfo = filtro.filtra(chaveRepository, bcbClient)

        responseObserver.onNext(ConsultaChaveResponseConverter().converter(chaveInfo))
        responseObserver.onCompleted()

    }
}