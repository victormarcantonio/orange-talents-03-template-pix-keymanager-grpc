package br.com.zup.client


import br.com.zup.chave.registra.ContaResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client

@Client("\${itau.contas.url}")
interface ContaClient {


    @Get("/api/v1/clientes/{idCliente}/contas")
    fun consulta(@PathVariable idCliente: String, @QueryValue tipo: String): HttpResponse<ContaResponse>?


}
