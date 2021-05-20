package br.com.zup.chave.exception

import java.lang.RuntimeException
import javax.inject.Singleton

@Singleton
class ChaveNaoEncontradaException(message: String?): RuntimeException(message){
}