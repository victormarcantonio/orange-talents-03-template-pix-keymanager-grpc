package br.com.zup.chave.registra

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected


@Introspected
data class InstituicaoResponse(
    val nome: String,
    val ispb: String) {
}
