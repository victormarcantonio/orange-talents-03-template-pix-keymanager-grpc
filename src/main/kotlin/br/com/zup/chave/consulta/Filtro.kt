package br.com.zup.chave.consulta

import br.com.zup.chave.ChaveRepository
import br.com.zup.chave.exception.ChaveNaoEncontradaException
import br.com.zup.client.BcbClient
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpStatus
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size
import kotlin.IllegalArgumentException
import kotlin.math.max

@Introspected
sealed class Filtro{


    abstract fun filtra (chaveRepository: ChaveRepository, bcbClient: BcbClient): ChavePixInfo


    @Introspected
    data class PorPixId(
        @field:NotBlank val clienteId: String,
        @field: NotBlank val pixId: String
    ): Filtro(){

        override fun filtra(chaveRepository: ChaveRepository, bcbClient: BcbClient): ChavePixInfo {
           return chaveRepository.findById(UUID.fromString(pixId))
               .filter{it.pertenceAoCliente(UUID.fromString(clienteId))}
               .map(ChavePixInfo::toChavePixInfo)
               .orElseThrow{ChaveNaoEncontradaException("Chave não encontrada")}
        }
    }

    @Introspected
    data class PorChave(
        @field: NotBlank @field: Size(max = 77) val chave: String
    ): Filtro(){
        override fun filtra(chaveRepository: ChaveRepository, bcbClient: BcbClient): ChavePixInfo {
            return chaveRepository.findByChave(chave)
                .map (ChavePixInfo::toChavePixInfo)
                .orElseGet {
                    val response = bcbClient.consultaBcb(chave)
                    when(response.status) {
                        HttpStatus.OK -> response.body()?.toChave()
                        else -> throw ChaveNaoEncontradaException("Chave não encontrada")
                    }

                }
        }
    }
    @Introspected
     class Invalido(): Filtro(){
        override fun filtra(chaveRepository: ChaveRepository, bcbClient: BcbClient): ChavePixInfo {
            throw IllegalArgumentException("Chave inválida ou não informada")
        }
    }

}
