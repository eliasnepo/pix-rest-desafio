package br.com.zupacademy.list

import br.com.zupacademy.AllKeysServiceGrpc
import br.com.zupacademy.FindAllKeysRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable

@Controller("/pix/keymanager/")
class ListKeysController (private val grpcClent: AllKeysServiceGrpc.AllKeysServiceBlockingStub) {

    @Get("/{clientId}")
    fun listAllKeys(@PathVariable clientId: String): HttpResponse<List<ClientKeysResponse>> {
        val response = grpcClent.findAll(
            FindAllKeysRequest.newBuilder()
            .setClientId(clientId)
            .build())
        val listOfKeys = response.keysList.map { key -> ClientKeysResponse.fromGrpc(key) }
        return HttpResponse.ok(listOfKeys)
    }
}