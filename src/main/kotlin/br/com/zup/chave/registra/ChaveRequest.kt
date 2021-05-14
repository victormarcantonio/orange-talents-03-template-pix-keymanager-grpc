
package br.com.zup.chave



import br.com.zup.validation.ValidaChave
//import br.com.zup.validation.ValidaChave
import io.micronaut.core.annotation.Introspected
import java.util.*

@Introspected
@ValidaChave
data class ChaveRequest(val clienteId: String,
                        val tipoChave: TipoChave?,
                        var chave:String,
                        val tipoConta: TipoConta?){


 fun toChave(conta: Conta): Chave{
       if(chave.isBlank()){
           chave = UUID.randomUUID().toString()
       }
       return Chave(clienteId = UUID.fromString(clienteId),tipoChave!!,chave,tipoConta!!, conta)
 }


}

