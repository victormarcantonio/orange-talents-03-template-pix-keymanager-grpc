package br.com.zup.chave

import io.grpc.Status
import io.micronaut.validation.Validated
import javax.inject.Singleton
import javax.validation.ConstraintViolationException
import javax.validation.Valid

@Validated
@Singleton
class ChaveService(val chaveRepository: ChaveRepository, val contaClient: ContaClient) {


    fun cadastra(@Valid chaveRequest: ChaveRequest): Chave{

        val contaResponse = contaClient.consulta(chaveRequest.clienteId, chaveRequest.tipoConta.toString())

        val conta = contaResponse.body()!!.toConta()


        val chave = chaveRequest.toChave(conta)
        println(chave)
       chaveRepository.save(chave)

        return chave
    }

}