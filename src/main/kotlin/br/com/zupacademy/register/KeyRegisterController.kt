package br.com.zupacademy.register

import br.com.zupacademy.PixKeyServiceGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.http.uri.UriBuilder
import io.micronaut.validation.Validated
import javax.validation.Valid

@Controller("/pix/keymanager/create")
@Validated
class KeyRegisterController(private val grpcClient: PixKeyServiceGrpc.PixKeyServiceBlockingStub) {

    @Post
    fun keyRegister(@Body @Valid request: CreatePixRequest): HttpResponse<Any> {
        val response = grpcClient.generateKey(request.toModelGrpc())

        val uri = UriBuilder.of("/pix/keymanager/{id}")
            .expand(mutableMapOf(Pair("id", response.pixId)))

        return HttpResponse.created(uri)
    }
}