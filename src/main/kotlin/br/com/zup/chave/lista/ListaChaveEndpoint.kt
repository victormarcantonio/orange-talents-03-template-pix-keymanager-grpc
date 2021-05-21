package br.com.zup.chave.lista

import br.com.zup.*
import br.com.zup.chave.ChaveRepository
import br.com.zup.chave.consulta.ChavePixInfo
import br.com.zup.chave.exception.ErrorHandler
import com.google.protobuf.Timestamp
import io.grpc.stub.StreamObserver
import java.lang.IllegalArgumentException
import java.time.ZoneId
import java.util.*
import javax.inject.Singleton


@Singleton
@ErrorHandler
class ListaChaveEndpoint(
    val chaveRepository: ChaveRepository
): PixKeyManagerListaServiceGrpc.PixKeyManagerListaServiceImplBase(){

    override fun listar(request: ListaPixRequest, responseObserver: StreamObserver<ListaPixResponse>) {


        if(request.clienteId.isBlank()){
            throw IllegalArgumentException("Cliente id não informado")
        }

       val listaChaves = chaveRepository.findByClienteId(UUID.fromString(request.clienteId))
           .map {
               ListaPixResponse.Chave.newBuilder()
                   .setPixId(it.id.toString())
                   .setTipoChave(TipoChave.valueOf(it.tipoChave.name))
                   .setChave(it.chave)
                   .setTipoConta(TipoConta.valueOf(it.tipoConta.name))
                   .setCriadaEm(it.criadaEm.let {
                       val createdAt = it!!.atZone(ZoneId.of("UTC")).toInstant()
                       Timestamp.newBuilder()
                           .setSeconds(createdAt.epochSecond)
                           .setNanos(createdAt.nano)
                           .build()
                   })
                   .build()
           }

        responseObserver.onNext(ListaPixResponse.newBuilder()
            .setClienteId(request.clienteId)
            .addAllChaves(listaChaves)
            .build())
        responseObserver.onCompleted()
    }

}