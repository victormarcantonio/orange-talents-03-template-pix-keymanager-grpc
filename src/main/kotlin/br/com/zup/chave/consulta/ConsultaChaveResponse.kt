package br.com.zup.chave.consulta

import br.com.zup.Instituicoes
import br.com.zup.TipoChave
import br.com.zup.chave.Conta
import br.com.zup.chave.TipoConta
import br.com.zup.chave.registra.*
import java.time.LocalDateTime

data class ConsultaChaveResponse(
    val keyType: PixKeyType,
    val key: String,
    val bankAccount: BankAccountResponse,
    val owner: OwnerResponse,
    val createdAt: LocalDateTime
) {

    fun toChave(): ChavePixInfo {
        return ChavePixInfo(
            tipoChave = keyType.tipoChave,
            chave = this.key,
            tipoConta = when (this.bankAccount.accountType) {
                AccountType.CACC -> TipoConta.CONTA_CORRENTE
                AccountType.SVGS -> TipoConta.CONTA_POUPANCA
            },
            conta = Conta(
                instituicao = Instituicoes.nome(bankAccount.participant),
                titular = owner.name,
                cpfTitular = owner.taxIdNumber,
                agencia = bankAccount.branch,
                numero = bankAccount.accountNumber,
                ispb = bankAccount.participant
            ),
            criadaEm = this.createdAt
        )
    }
}








