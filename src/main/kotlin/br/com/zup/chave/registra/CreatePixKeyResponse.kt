package br.com.zup.chave.registra

import br.com.zup.chave.TipoPessoa
import io.micronaut.core.annotation.Introspected
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Introspected
data class CreatePixKeyResponse(
    val key: String,
    val keyType: PixKeyType,
    val bankAccount: BankAccountResponse,
    val owner: OwnerResponse,
    val createdAt: LocalDateTime) {
}


data class BankAccountResponse(
   val participant: String,
   val branch: String,
   val accountNumber: String,
   val accountType: AccountType
)

data class OwnerResponse(
    val type: TipoPessoa?,
    val name: String,
    val taxIdNumber: String
)

