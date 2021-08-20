package br.com.zup.academy.list

import br.com.zup.academy.details.createGrpcResponse
import br.com.zupacademy.*
import br.com.zupacademy.details.DetailsReponse
import br.com.zupacademy.list.ClientKeysResponse
import br.com.zupacademy.shared.GrpcClientFactory
import com.google.protobuf.Timestamp
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
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class ListKeysControllerTestIT {

    @field:Inject
    @field:Client("/")
    lateinit var httpClient: HttpClient

    @field:Inject
    lateinit var grpcClient: AllKeysServiceGrpc.AllKeysServiceBlockingStub

    @BeforeEach
    fun setUp() {
        Mockito.reset(grpcClient)
    }

    @Test
    fun `should return list of keys when client id is valid`() {
        val clientId = UUID.randomUUID().toString()

        Mockito.`when`(grpcClient.findAll(Mockito.any())).thenReturn(createGrpcResponse(clientId))

        val response = assertDoesNotThrow {
            httpClient.toBlocking().exchange(HttpRequest.GET<List<ClientKeysResponse>>("/pix/keymanager/${clientId}"), List::class.java)
        }

        with(response) {
            assertEquals(HttpStatus.OK, status)
            assertFalse(response.body.isEmpty)
            assertEquals(2, body().size)
        }
    }

    @Test
    fun `should return status not found when client id is invalid`() {
        val clientId = UUID.randomUUID().toString()

        Mockito.`when`(grpcClient.findAll(Mockito.any())).thenThrow(StatusRuntimeException(Status.NOT_FOUND))

        val response = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(HttpRequest.GET<List<ClientKeysResponse>>("/pix/keymanager/${clientId}"), List::class.java)
        }

        with(response) {
            assertEquals(HttpStatus.NOT_FOUND, status)
        }
    }

    @Test
    fun `should return empty list of keys when the client doesn't have one`() {
        val clientId = UUID.randomUUID().toString()

        Mockito.`when`(grpcClient.findAll(Mockito.any())).thenReturn(FindAllKeysResponse.newBuilder()
            .build())

        val response = assertDoesNotThrow {
            httpClient.toBlocking().exchange(HttpRequest.GET<List<ClientKeysResponse>>("/pix/keymanager/${clientId}"), List::class.java)
        }

        with(response) {
            assertEquals(HttpStatus.OK, status)
            assertTrue(body().size == 0)
        }
    }

    @Factory
    @Replaces(factory = GrpcClientFactory::class)
    internal class KeyRegisterFactory {
        @Singleton
        internal fun stubRegisterMock() = Mockito.mock(AllKeysServiceGrpc.AllKeysServiceBlockingStub::class.java)
    }
}

@Factory
fun createGrpcResponse(clientId: String): FindAllKeysResponse {
    val key = FindAllKeysResponse.Key.newBuilder()
        .setKey("12345678912")
        .setPixId(UUID.randomUUID().toString())
        .setClientId(clientId)
        .setKeyType(KeyType.CPF)
        .setAccountType(AccountType.CONTA_CORRENTE)
        .setCreatedAt(Timestamp.newBuilder()
            .setNanos(1242132)
            .setSeconds(1621320142))
        .build()

    return FindAllKeysResponse.newBuilder()
        .addKeys(key)
        .addKeys(key)
        .build()
}