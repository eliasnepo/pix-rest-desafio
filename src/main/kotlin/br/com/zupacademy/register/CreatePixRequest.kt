package br.com.zupacademy.register

import br.com.zupacademy.AccountType
import br.com.zupacademy.KeyRequest
import br.com.zupacademy.KeyType
import br.com.zupacademy.shared.validations.ValidPixKey
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@ValidPixKey
@Introspected
data class CreatePixRequest (

    @field:NotNull
    val keyType: KeyTypeDTO?,

    @field:NotNull
    val accountType: AccountTypeDTO?,

    @field:NotBlank
    val clientId: String,

    @field:Size(max = 77)
    val key: String
        ) {

    fun toModelGrpc(): KeyRequest {
        return KeyRequest.newBuilder()
            .setClientId(clientId)
            .setKey(key)
            .setAccountType(accountType?.grpcType ?: AccountType.UNKNOWN_ACCOUNT)
            .setKeyType(keyType?.grpcType ?: KeyType.UNKNOWN_KEY)
            .build()
    }
}

enum class AccountTypeDTO(val grpcType: AccountType) {
    CONTA_CORRENTE(AccountType.CONTA_CORRENTE),
    CONTA_POUPANCA(AccountType.CONTA_POUPANCA)
}

enum class KeyTypeDTO(val grpcType: KeyType) {
    CPF(KeyType.CPF) {
        override fun validate(key: String): Boolean {
            if (key.isNullOrBlank()) {
                return false
            }
            return key.matches("^[0-9]{11}\$".toRegex())
        }
    },
    EMAIL(KeyType.EMAIL) {
        override fun validate(key: String): Boolean {
            if (key.isNullOrBlank()) {
                return false
            }
            return key.matches("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}\\@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+"
                .toRegex())
        }
    },
    PHONE(KeyType.PHONE) {
        override fun validate(key: String): Boolean {
            if (key.isNullOrBlank()) {
                return false
            }
            return key.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
        }
    },
    RANDOM(KeyType.RANDOM) {
        override fun validate(key: String): Boolean {
            return key.isNullOrBlank()
        }
    };

    abstract fun validate(key: String): Boolean
}