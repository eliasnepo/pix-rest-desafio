package br.com.zupacademy.delete

import br.com.zupacademy.DeleteKeyServiceGrpc
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.PathVariable
import io.micronaut.validation.Validated
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@Controller("/pix/keymanager/")
@Validated
class DeleteKeyController(private val grpcClient: DeleteKeyServiceGrpc.DeleteKeyServiceBlockingStub) {

    @Delete("/{pixId}")
    fun delete(@Valid request: DeleteKeyRequest, @PathVariable pixId: String) : HttpResponse<Any> {
        val requestGrpc = request.toModel(pixId)
        grpcClient.deleteKey(requestGrpc)
        return HttpResponse.noContent()
    }
}

@Introspected
data class DeleteKeyRequest(
        @field:NotBlank val clientId: String
) {
    fun toModel(pixId: String) : br.com.zupacademy.DeleteKeyRequest {
        return br.com.zupacademy.DeleteKeyRequest
                .newBuilder()
                .setClientId(clientId)
                .setPixId(pixId)
                .build()
    }
}