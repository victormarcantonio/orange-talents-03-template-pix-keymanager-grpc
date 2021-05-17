package br.com.zup.chave.registra

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Introspected
data class BankAccount(@field: NotNull val participant: String,
                       @field: NotBlank val branch: String,
                       @field: NotBlank val accountNumber: String,
                       @field: NotNull val accountType: AccountType)
