package br.com.zup.chave.consulta

import br.com.zup.chave.Chave
import br.com.zup.chave.Conta
import br.com.zup.chave.TipoChave
import br.com.zup.chave.TipoConta
import br.com.zup.chave.registra.ContaResponse
import java.time.LocalDateTime
import java.util.*

data class ChavePixInfo(
    val pixId: UUID? = null,
    val clienteId: UUID? = null,
    val tipoChave: TipoChave,
    val chave: String,
    val tipoConta: TipoConta,
    val conta: Conta,
    val criadaEm: LocalDateTime?
) {


    companion object {
        fun toChavePixInfo(chave: Chave): ChavePixInfo{
            return ChavePixInfo(
                pixId = chave.id,
                clienteId = chave.clienteId,
                tipoChave = chave.tipoChave,
                chave = chave.chave,
                tipoConta = chave.tipoConta,
                conta = chave.conta,
                criadaEm = chave.criadaEm,
            )
        }
    }

}
