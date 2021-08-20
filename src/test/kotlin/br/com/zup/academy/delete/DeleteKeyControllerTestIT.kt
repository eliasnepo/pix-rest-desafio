package br.com.zup.academy.delete

import br.com.zupacademy.DeleteKeyResponse
import br.com.zupacademy.DeleteKeyServiceGrpc
import br.com.zupacademy.delete.DeleteKeyRequest
import br.com.zupacademy.shared.GrpcClientFactory
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class DeleteKeyControllerTestIT {

    @field:Inject
    @field:Client("/")
    lateinit var httpClient: HttpClient

    @field:Inject
    lateinit var grpcClient: DeleteKeyServiceGrpc.DeleteKeyServiceBlockingStub

    @BeforeEach
    fun setUp() {
        Mockito.reset(grpcClient)
    }

    @Test
    fun `should delete key when all is valid`() {
        val pixId = UUID.randomUUID().toString()
        val grpcResponse = DeleteKeyResponse.newBuilder()
            .setPixId(pixId)
            .build()

        Mockito.`when`(grpcClient.deleteKey(Mockito.any())).thenReturn(grpcResponse)

        val request = DeleteKeyRequest(UUID.randomUUID().toString())

        val response = assertDoesNotThrow {
            httpClient.toBlocking().exchange(HttpRequest.DELETE("/pix/keymanager/${pixId}", request), DeleteKeyRequest::class.java)
        }

        with(response) {
            assertEquals(HttpStatus.NO_CONTENT, response.status)
        }
    }

    @Test
    fun `should return status 404 when pixId does not exists`() {
        val pixId = UUID.randomUUID().toString()
        Mockito.`when`(grpcClient.deleteKey(Mockito.any())).thenThrow(StatusRuntimeException(Status.NOT_FOUND))

        val request = DeleteKeyRequest(UUID.randomUUID().toString())

        val response = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(HttpRequest.DELETE("/pix/keymanager/${pixId}", request), DeleteKeyRequest::class.java)
        }

        with(response) {
            assertEquals(HttpStatus.NOT_FOUND, response.status)
        }
    }

    @Test
    fun `should return status 400 when clientId is empty`() {
        val pixId = UUID.randomUUID().toString()
        Mockito.`when`(grpcClient.deleteKey(Mockito.any())).thenThrow(StatusRuntimeException(Status.NOT_FOUND))

        val request = DeleteKeyRequest(clientId = "")

        val response = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(HttpRequest.DELETE("/pix/keymanager/${pixId}", request), DeleteKeyRequest::class.java)
        }

        with(response) {
            assertEquals(HttpStatus.BAD_REQUEST, response.status)
        }
    }

    @Factory
    @Replaces(factory = GrpcClientFactory::class)
    internal class KeyRegisterFactory {
        @Singleton
        internal fun stubRegisterMock() = Mockito.mock(DeleteKeyServiceGrpc.DeleteKeyServiceBlockingStub::class.java)
    }
}