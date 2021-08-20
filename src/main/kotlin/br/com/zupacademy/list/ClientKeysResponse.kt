package br.com.zupacademy.list

import br.com.zupacademy.AccountType
import br.com.zupacademy.FindAllKeysResponse
import br.com.zupacademy.KeyType
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

data class ClientKeysResponse (
    val keyType: KeyType,
    val accountType: AccountType,
    val pixId: String,
    val key: String,
    val clientId: String,
    val createdAt: LocalDateTime
        ){

    companion object {
        fun fromGrpc(grpcResponse : FindAllKeysResponse.Key) : ClientKeysResponse{
            return ClientKeysResponse(
                grpcResponse.keyType,
                grpcResponse.accountType,
                grpcResponse.pixId,
                grpcResponse.key,
                grpcResponse.clientId,
                Instant
                    .ofEpochSecond(grpcResponse.createdAt.seconds, grpcResponse.createdAt.nanos.toLong())
                    .atZone(ZoneOffset.UTC)
                    .toLocalDateTime()
            )
        }
    }
}
