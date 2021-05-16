
package br.com.zup.chave.registra



import br.com.zup.chave.Chave
import br.com.zup.chave.Conta
import br.com.zup.chave.TipoChave
import br.com.zup.chave.TipoConta
import br.com.zup.validation.ValidaChave
//import br.com.zup.validation.ValidaChave
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Introspected
@ValidaChave
data class ChaveRequest(@field: NotBlank val clienteId: String,
                        @field: NotNull val tipoChave: TipoChave?,
                        @field: Size(max = 77) var chave:String,
                        @field: NotNull val tipoConta: TipoConta?){


 fun toChave(conta: Conta): Chave {
       if(chave.isBlank()){
           chave = UUID.randomUUID().toString()
       }
       return Chave(clienteId = UUID.fromString(clienteId),tipoChave!!,chave,tipoConta!!, conta)
 }


}

