
package br.com.zup.chave.registra



import br.com.zup.chave.*
import br.com.zup.validation.ValidaChave
//import br.com.zup.validation.ValidaChave
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Introspected
@ValidaChave
data class ChaveRequest(@field: NotNull val clienteId: String,
                        @field: NotNull val tipoChave: TipoChave?,
                        @field: Size(max = 77) var chave:String,
                        @field: NotNull var tipoConta: TipoConta?,
                        @field: NotNull val tipoPessoa: TipoPessoa?){


 fun toChave(conta: Conta): Chave {
       if(chave.isBlank()){
           chave = UUID.randomUUID().toString()
       }
       return Chave(clienteId = UUID.fromString(clienteId),
           tipoChave= tipoChave!!,
            chave= chave,
           tipoConta = tipoConta!!,
           conta= conta)
 }

    fun toChaveRequestBcb(chavePix: Chave): ChaveRequestBcb{
        if(chavePix.isAleatoria()){
            chavePix.chave = ""
        }
        return ChaveRequestBcb(
            keyType = PixKeyType.by(chavePix.tipoChave),
            key = chavePix.chave,
            bankAccount = BankAccountRequest(
             chavePix.conta.ispb,
             chavePix.conta.agencia,
             chavePix.conta.numero,
             byTipoContaBcb(chavePix.tipoConta)
         ),
            owner = OwnerRequest(
                tipoPessoa,
                chavePix.conta.titular,
                chavePix.conta.cpfTitular
            )
        )
    }

    fun byTipoContaBcb(tipoConta: TipoConta?): AccountType{
        when(tipoConta){
            TipoConta.CONTA_CORRENTE-> return AccountType.CACC
            TipoConta.CONTA_POUPANCA-> return AccountType.SVGS
        }
        throw IllegalArgumentException("Tipo conta não encontrado")
    }

}

