package br.com.zup.chave.registra

import br.com.zup.chave.Conta
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
    fun toConta(): Conta {
       return Conta(
           instituicao = instituicao.nome,
           ispb = instituicao.ispb,
           titular = titular.nome,
           cpfTitular = titular.cpf,
           agencia = agencia,
           numero = numero
       )
    }


}
