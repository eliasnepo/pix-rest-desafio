package br.com.zupacademy.details

import br.com.zupacademy.DetailsKeyServiceGrpc
import br.com.zupacademy.KeyDetailRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable

@Controller("/pix/keymanager/")
class DetailsKeyController(private val grpcClient: DetailsKeyServiceGrpc.DetailsKeyServiceBlockingStub) {

    @Get("/{pixId}/client/{clientId}")
    fun keyDetails(@PathVariable pixId: String, @PathVariable clientId: String) : HttpResponse<Any> {
        val requestGrpc = KeyDetailRequest.newBuilder()
            .setPixId(KeyDetailRequest.PixIdAndClientId.newBuilder()
                .setPixId(pixId)
                .setClientId(clientId))
            .build()
        val responseGrpc = grpcClient.keyDetail(requestGrpc)
        return HttpResponse.ok(DetailsReponse.fromGrpc(responseGrpc))
    }
}