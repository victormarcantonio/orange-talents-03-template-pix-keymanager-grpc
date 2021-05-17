
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
            chaveId= chave,
           tipoConta = tipoConta!!,
           conta= conta)
 }

    fun toChaveRequestBcb(contaResponse: ContaResponse?): ChaveRequestBcb{
        return ChaveRequestBcb(
            keyType = byKeyType(tipoChave),
            key = this.chave,
            bankAccount = BankAccount(
             contaResponse!!.instituicao.ispb,
             contaResponse.agencia,
             contaResponse.numero,
             byTipoContaBcb(TipoConta.valueOf(contaResponse.tipo))
         ),
            owner = Owner(
                tipoPessoa,
                contaResponse.titular.nome,
                contaResponse.titular.cpf
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

    fun byKeyType(tipoChave: TipoChave?): PixKeyType{
        when(tipoChave){
            TipoChave.CPF-> return PixKeyType.CPF
            TipoChave.CELULAR -> return PixKeyType.PHONE
            TipoChave.EMAIL -> return PixKeyType.EMAIL
            TipoChave.ALEATORIA-> return PixKeyType.RANDOM
        }
        throw IllegalArgumentException("Tipo chave inválido")
    }


}

