package br.com.zup.chave.remove

import br.com.zup.chave.Chave

data class DeletaChaveRequest(val key: String, val participant: String = "60701190" ) {
}