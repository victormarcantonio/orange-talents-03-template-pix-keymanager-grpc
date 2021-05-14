package br.com.zup.chave.registra

import javax.persistence.Embeddable
import javax.persistence.Embedded
import javax.validation.constraints.NotBlank

@Embeddable
class Conta(

    @field: NotBlank val instituicao: String,
    @field: NotBlank val ispb: String,
    @field: NotBlank val titular: String,
    @field: NotBlank val cpfTitular: String,
    @field: NotBlank val agencia:String,
    @field: NotBlank val numero:String) {
}
