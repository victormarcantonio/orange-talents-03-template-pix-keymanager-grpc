package br.com.zup.chave.exception

import java.lang.RuntimeException
import javax.inject.Singleton

@Singleton
class ChaveExisteException(message: String?): RuntimeException(message) {
}