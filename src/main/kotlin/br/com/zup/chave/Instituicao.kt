package br.com.zup.chave

import javax.persistence.Embeddable

@Embeddable
class Instituicao(val nome:String, val ispb: String) {
}
