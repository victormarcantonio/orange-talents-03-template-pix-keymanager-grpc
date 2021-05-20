package br.com.zup.chave.consulta

import br.com.zup.ConsultaPixResponse
import br.com.zup.TipoChave
import br.com.zup.TipoConta
import com.google.protobuf.Timestamp
import java.time.ZoneId


class ConsultaChaveResponseConverter {


    fun converter(chavePixInfo: ChavePixInfo): ConsultaPixResponse{
        return ConsultaPixResponse.newBuilder()
            .setClienteId(chavePixInfo.clienteId.toString()?: "")
            .setPixId(chavePixInfo.pixId.toString()?: "")
            .setChave(ConsultaPixResponse.ChavePix
                .newBuilder()
                .setTipoChave(TipoChave.valueOf(chavePixInfo.tipoChave.name))
                .setChave(chavePixInfo.chave)
                .setDadosConta(ConsultaPixResponse.ChavePix.DadosConta.newBuilder()
                    .setTipoConta(TipoConta.valueOf(chavePixInfo.tipoConta.name))
                    .setInstituicao(chavePixInfo.conta.instituicao)
                    .setNome(chavePixInfo.conta.titular)
                    .setCpf(chavePixInfo.conta.cpfTitular)
                    .setAgencia(chavePixInfo.conta.agencia)
                    .setNumero(chavePixInfo.conta.numero)
                    .build())
                .setCriadaEm(chavePixInfo.criadaEm.let {
                    val createdAt = it!!.atZone(ZoneId.of("UTC")).toInstant()
                    Timestamp.newBuilder()
                        .setSeconds(createdAt.epochSecond)
                        .setNanos(createdAt.nano)
                        .build()
                })
            ).build()
    }
}