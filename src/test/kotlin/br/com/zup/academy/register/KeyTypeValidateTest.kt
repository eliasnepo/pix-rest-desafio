package br.com.zup.academy.register

import br.com.zupacademy.register.KeyTypeDTO
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class KeyTypeValidateTest {

    @Nested
    inner class CPF {

        @Test
        fun `should not validate key when its empty`() {
            assertFalse(KeyTypeDTO.CPF.validate(""))
        }

        @Test
        fun `should not validate key when its not well formed`() {
            val key = "1234567891" // 10 caracteres
            val key2 = "123456789111" // 12 caracteres
            val key3 = "123456789a1" // 11 caracteres mas com letra
            assertFalse(KeyTypeDTO.CPF.validate(key))
            assertFalse(KeyTypeDTO.CPF.validate(key2))
            assertFalse(KeyTypeDTO.CPF.validate(key3))
        }

        @Test
        fun `should validate when key is well formed`() {
            val key = "12345678912"

            assertTrue(KeyTypeDTO.CPF.validate(key))
        }
    }

    @Nested
    inner class PHONE {
        @Test
        fun `should return true when valid phone`() {
            val validPhone = "+55999999999"

            assertTrue(KeyTypeDTO.PHONE.validate(validPhone))
        }

        @Test
        fun `should return false when phone is empty`() {
            val invalidPhone = ""

            assertFalse(KeyTypeDTO.PHONE.validate(invalidPhone))
        }

        @Test
        fun `should return false when invalid phone`() {
            val invalidPhone = "999999999"
            val invalidPhone2 = "+55999999999999999"

            assertFalse(KeyTypeDTO.PHONE.validate(invalidPhone))
            assertFalse(KeyTypeDTO.PHONE.validate(invalidPhone2))
        }
    }

    @Nested
    inner class EMAIL {

        @Test
        fun `should return true when valid email`() {
            val validEmail = "elias@zup.com"
            val validEmail2 = "elias@zup.com.br"

            assertTrue(KeyTypeDTO.EMAIL.validate(validEmail))
            assertTrue(KeyTypeDTO.EMAIL.validate(validEmail2))
        }

        @Test
        fun `should return false when empty email`() {
            val invalidEmail = ""
            assertFalse(KeyTypeDTO.EMAIL.validate(invalidEmail))
        }

        @Test
        fun `should return false when invalid email`() {
            val invalidEmail = "elias@zup"
            val invalidEmail2 = "elias.zup.br"

            assertFalse(KeyTypeDTO.EMAIL.validate(invalidEmail))
            assertFalse(KeyTypeDTO.EMAIL.validate(invalidEmail2))
        }
    }

    @Nested
    inner class RANDOM {
        @Test
        fun `should return false when its not empty`() {
            assertFalse(KeyTypeDTO.RANDOM.validate("a"))
        }

        @Test
        fun `should return true when is empty`() {
            assertTrue(KeyTypeDTO.RANDOM.validate(""))
        }
    }
}