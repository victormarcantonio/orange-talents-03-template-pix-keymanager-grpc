package br.com.zup.chave.registra


import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client("\${itau.contas.url}")
interface ContaClient {


    @Get("/api/v1/clientes/{idCliente}/contas")
    fun consulta(@PathVariable idCliente: String, @QueryValue tipo: String): HttpResponse<ContaResponse>?
}
