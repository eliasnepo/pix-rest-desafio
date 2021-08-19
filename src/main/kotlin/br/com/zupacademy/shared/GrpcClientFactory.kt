package br.com.zupacademy.shared

import br.com.zupacademy.AllKeysServiceGrpc
import br.com.zupacademy.DeleteKeyServiceGrpc
import br.com.zupacademy.DetailsKeyServiceGrpc
import br.com.zupacademy.PixKeyServiceGrpc
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import javax.inject.Singleton

@Factory
class GrpcClientFactory(@GrpcChannel("keymanager") val channel: ManagedChannel) {

    @Singleton
    fun keyRegister() = PixKeyServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun keyDelete() = DeleteKeyServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun keyDetails() = DetailsKeyServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun allKeys() = AllKeysServiceGrpc.newBlockingStub(channel)
}