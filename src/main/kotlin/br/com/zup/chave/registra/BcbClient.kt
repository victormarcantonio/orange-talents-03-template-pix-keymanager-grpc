package br.com.zup.chave.registra

import br.com.zup.chave.remove.DeletaChaveRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client

@Client("\${itau.bcb.url}")
interface BcbClient {

    @Post("/api/v1/pix/keys")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    fun cadastraBcb(@Body chaveRequestBcb: ChaveRequestBcb): HttpResponse<ChaveBcbResponse?>

    @Delete("/api/v1/pix/keys/{key}")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    fun deletaBcb(@Body deletaChaveRequest: DeletaChaveRequest, @PathVariable key: String): HttpResponse<Any?>
}