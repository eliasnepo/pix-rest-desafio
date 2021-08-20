package br.com.zup.academy.details

import br.com.zupacademy.*
import br.com.zupacademy.delete.DeleteKeyRequest
import br.com.zupacademy.details.AccountResponse
import br.com.zupacademy.details.DetailsReponse
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
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class DetailsKeyTestIT {

    @field:Inject
    @field:Client("/")
    lateinit var httpClient: HttpClient

    @field:Inject
    lateinit var grpcClient: DetailsKeyServiceGrpc.DetailsKeyServiceBlockingStub

    @BeforeEach
    fun setUp() {
        Mockito.reset(grpcClient)
    }

    @Test
    fun `should get details of key when all is valid`() {
        val pixId = UUID.randomUUID().toString()
        val clientId = UUID.randomUUID().toString()

        Mockito.`when`(grpcClient.keyDetail(Mockito.any())).thenReturn(createGrpcResponse(pixId, clientId))

        val response = assertDoesNotThrow {
            httpClient.toBlocking().exchange(HttpRequest.GET<DetailsReponse>("/pix/keymanager/${pixId}/client/${clientId}"), DetailsReponse::class.java)
        }

        with(response) {
            assertEquals(HttpStatus.OK, response.status)
            assertEquals(clientId, response.body().clientId)
            assertEquals(pixId, response.body().pixId)
        }
    }

    @Test
    fun `should return not found status when pixId is not found`() {
        val pixId = UUID.randomUUID().toString()
        val clientId = UUID.randomUUID().toString()
        Mockito.`when`(grpcClient.keyDetail(Mockito.any())).thenThrow(StatusRuntimeException(Status.NOT_FOUND))

        val response = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(HttpRequest.GET<DetailsReponse>("/pix/keymanager/${pixId}/client/${clientId}"), DetailsReponse::class.java)
        }

        with(response) {
            assertEquals(HttpStatus.NOT_FOUND, status)
        }
    }

    @Test
    fun `should return bad request when clientId is not the owner of pixId`() {
        val pixId = UUID.randomUUID().toString()
        val clientId = UUID.randomUUID().toString()
        Mockito.`when`(grpcClient.keyDetail(Mockito.any())).thenThrow(StatusRuntimeException(Status.PERMISSION_DENIED))

        val response = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(HttpRequest.GET<DetailsReponse>("/pix/keymanager/${pixId}/client/${clientId}"), DetailsReponse::class.java)
        }

        with(response) {
            assertEquals(HttpStatus.BAD_REQUEST, status)
        }
    }

    @Factory
    @Replaces(factory = GrpcClientFactory::class)
    internal class KeyRegisterFactory {
        @Singleton
        internal fun stubRegisterMock() = Mockito.mock(DetailsKeyServiceGrpc.DetailsKeyServiceBlockingStub::class.java)
    }
}

@Factory
fun createGrpcResponse(pixId: String, clientId: String): KeyDetailResponse {
    return KeyDetailResponse.newBuilder()
        .setAccount(KeyDetailResponse.Account.newBuilder()
            .setAccountType(AccountType.CONTA_CORRENTE)
            .setBranch("0001")
            .setOwnerCpf("12345678911")
            .setOwnerName("Elias")
            .setNumber("1234-5")
            .setParticipant("6070190"))
        .setPixId(pixId)
        .setClientId(clientId)
        .setKeyType(KeyType.CPF)
        .setKey("12345678911")
        .build()
}