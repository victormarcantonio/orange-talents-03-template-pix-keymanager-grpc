package br.com.zup.chave.registra

import br.com.zup.chave.TipoChave
import br.com.zup.chave.TipoConta

enum class AccountType() {
    CACC,
    SVGS;


    companion object {
        fun by(tipoConta: TipoConta): AccountType {
            return when (tipoConta) {
                TipoConta.CONTA_CORRENTE -> CACC
                TipoConta.CONTA_POUPANCA -> SVGS
            }
        }
    }
}