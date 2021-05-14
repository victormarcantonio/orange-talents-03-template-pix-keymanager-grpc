package br.com.zup.chave.registra

import br.com.zup.PixRequest


fun PixRequest.toModel() : ChaveRequest {
    return ChaveRequest(
        clienteId = id,
        tipoChave = when(tipoChave){
            br.com.zup.TipoChave.CHAVE_DEFAULT -> null
            else -> TipoChave.valueOf(tipoChave.name)
        },
        chave = chave,
        tipoConta = when(tipoConta){
            br.com.zup.TipoConta.CONTA_DEFAULT -> null
                else -> TipoConta.valueOf(tipoConta.name)
        }

    )
}
