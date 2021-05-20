package br.com.zup.chave.consulta

import io.micronaut.core.annotation.Introspected

@Introspected
data class ConsultaChaveRequest(
    val clienteId: String?,
    val pixId: String?,
    val chavePix: String?,
) {
}