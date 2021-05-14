package br.com.zup.chave.registra

import javax.persistence.Embeddable
import javax.persistence.Id

@Embeddable
class Titular(val nome: String, @field: Id val cpf: String) {
}
