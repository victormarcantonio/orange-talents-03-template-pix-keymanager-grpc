package br.com.zup.chave.registra

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected


@Introspected
data class TitularResponse(
     val id: String,
     val nome: String,
     val cpf: String)
