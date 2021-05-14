package br.com.zup.chave

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected

@Introspected
data class ContaResponse(
    val tipo: String,
    val instituicao: InstituicaoResponse,
    val agencia:String,
    val numero: String,
    val titular: TitularResponse
    )

{
    fun toConta(): Conta{
       return Conta(
           instituicao = this.instituicao.nome,
           ispb = this.instituicao.ispb,
           titular = this.titular.nome,
           cpfTitular = this.titular.cpf,
           agencia = this.agencia,
           numero = this.numero
       )
    }
}
