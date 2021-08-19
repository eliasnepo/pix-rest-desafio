package br.com.zup.academy.register

import br.com.zupacademy.KeyResponse
import br.com.zupacademy.PixKeyServiceGrpc
import br.com.zupacademy.register.AccountTypeDTO
import br.com.zupacademy.register.CreatePixRequest
import br.com.zupacademy.register.KeyTypeDTO
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
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class KeyRegisterControllerTestIT {

    @field:Inject
    @field:Client("/")
    lateinit var httpClient: HttpClient

    @field:Inject
    lateinit var grpcClient: PixKeyServiceGrpc.PixKeyServiceBlockingStub

    @BeforeEach
    fun setUp() {
        Mockito.reset(grpcClient)
    }

    @Test
    fun `should register key when all is valid`() {
        val reponseGrpc = KeyResponse.newBuilder()
            .setPixId(UUID.randomUUID().toString())
            .build()

        Mockito.`when`(grpcClient.generateKey(Mockito.any())).thenReturn(reponseGrpc)

        val request = CreatePixRequest(
            keyType = KeyTypeDTO.CPF,
            accountType = AccountTypeDTO.CONTA_CORRENTE,
            clientId = UUID.randomUUID().toString(),
            key = "12345678911"
        )

        val response = assertDoesNotThrow {
            httpClient.toBlocking()
                .exchange(HttpRequest.POST("/pix/keymanager/create", request), CreatePixRequest::class.java)
        }

        with(response) {
            assertEquals(HttpStatus.CREATED, response.status)
            assertTrue(response.header("location").contains(reponseGrpc.pixId))

        }
    }

    @Test
    fun `should return status 422 when key exists`() {
        Mockito.`when`(grpcClient.generateKey(Mockito.any()))
            .thenThrow(StatusRuntimeException(Status.ALREADY_EXISTS))

        val request = CreatePixRequest(
            keyType = KeyTypeDTO.CPF,
            accountType = AccountTypeDTO.CONTA_CORRENTE,
            clientId = UUID.randomUUID().toString(),
            key = "12345678911"
        )


        val error = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking()
                .exchange(HttpRequest.POST("/pix/keymanager/create", request), CreatePixRequest::class.java)
        }

        with(error) {
            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, error.status)
        }
    }

    @Test
    fun `should return bad request when is setted and key type is random`() {
        Mockito.`when`(grpcClient.generateKey(Mockito.any())).thenThrow(StatusRuntimeException(Status.INVALID_ARGUMENT))

        val request = CreatePixRequest(
            keyType = KeyTypeDTO.RANDOM,
            accountType = AccountTypeDTO.CONTA_CORRENTE,
            clientId = UUID.randomUUID().toString(),
            key = "12345678911"
        )


        val error = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking()
                .exchange(HttpRequest.POST("/pix/keymanager/create", request), CreatePixRequest::class.java)
        }

        with(error) {
            assertEquals(HttpStatus.BAD_REQUEST, error.status)
        }
    }

    @Test
    fun `should return bad request when format key is invalid`() {
        Mockito.`when`(grpcClient.generateKey(Mockito.any())).thenThrow(StatusRuntimeException(Status.INVALID_ARGUMENT))

        val request = CreatePixRequest(
            keyType = KeyTypeDTO.CPF,
            accountType = AccountTypeDTO.CONTA_CORRENTE,
            clientId = UUID.randomUUID().toString(),
            key = "1234567"
        )


        val error = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking()
                .exchange(HttpRequest.POST("/pix/keymanager/create", request), CreatePixRequest::class.java)
        }

        with(error) {
            assertEquals(HttpStatus.BAD_REQUEST, error.status)
        }
    }

    @Test
    fun `should return bad request when key has more than 77 characteres`() {
        Mockito.`when`(grpcClient.generateKey(Mockito.any())).thenThrow(StatusRuntimeException(Status.INVALID_ARGUMENT))

        val request = CreatePixRequest(
            keyType = KeyTypeDTO.CPF,
            accountType = AccountTypeDTO.CONTA_CORRENTE,
            clientId = UUID.randomUUID().toString(),
            key = "1".repeat(78)
        )


        val error = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking()
                .exchange(HttpRequest.POST("/pix/keymanager/create", request), CreatePixRequest::class.java)
        }

        with(error) {
            assertEquals(HttpStatus.BAD_REQUEST, error.status)
        }
    }


    @Factory
    @Replaces(factory = GrpcClientFactory::class)
    internal class KeyRegisterFactory {
        @Singleton
        internal fun stubRegisterMock() = Mockito.mock(PixKeyServiceGrpc.PixKeyServiceBlockingStub::class.java)
    }
}