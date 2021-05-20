package br.com.zup.chave.registra

import br.com.zup.chave.TipoChave

enum class PixKeyType(val tipoChave: TipoChave) {
    CPF(TipoChave.CPF),
    PHONE(TipoChave.CELULAR),
    EMAIL(TipoChave.EMAIL),
    RANDOM(TipoChave.ALEATORIA);


    companion object {

        private val mapping = PixKeyType.values().associateBy(PixKeyType::tipoChave)

        fun by(tipoChave: TipoChave?): PixKeyType {
            return  mapping[tipoChave] ?: throw IllegalArgumentException("PixKeyType invalid or not found for $tipoChave")
        }
    }
}