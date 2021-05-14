package br.com.zup.chave.registra

import javax.persistence.Embeddable

@Embeddable
class Instituicao(val nome:String, val ispb: String) {
}
