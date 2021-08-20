package br.com.zupacademy.details

import br.com.zupacademy.AccountType
import br.com.zupacademy.KeyDetailResponse
import br.com.zupacademy.KeyType
import io.micronaut.core.annotation.Introspected

@Introspected
data class DetailsReponse(
    val account: AccountResponse,
    val keyType: KeyType,
    val key: String,
    val pixId: String,
    val clientId: String
) {
    companion object {
        fun fromGrpc(responseGrpc: KeyDetailResponse): DetailsReponse {
            return DetailsReponse(
                AccountResponse(responseGrpc.account.accountType,
                    responseGrpc.account.ownerName,
                    responseGrpc.account.ownerCpf,
                    responseGrpc.account.branch,
                    responseGrpc.account.number,
                    responseGrpc.account.participant),
                responseGrpc.keyType,
                responseGrpc.key,
                responseGrpc.pixId,
                responseGrpc.clientId
            )
        }
    }
}

@Introspected
data class AccountResponse(
    val accountType: AccountType,
    val ownerName: String,
    val ownerCpf: String,
    val branch: String,
    val number: String,
    val participant: String
)