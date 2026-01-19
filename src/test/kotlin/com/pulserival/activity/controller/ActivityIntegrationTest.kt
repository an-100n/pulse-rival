package com.pulserival.activity.controller

import tools.jackson.databind.ObjectMapper
import com.pulserival.activity.dto.LogActivityCommand
import com.pulserival.identity.dto.RegisterUserCommand
import com.pulserival.activity.entity.ActivityType
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
class ActivityIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private fun getAuthToken(username: String): String {
        val loginRequest = mapOf(
            "username" to username,
            "password" to "testPassword123"
        )

        val responseJson = mockMvc.post("/api/v1/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(loginRequest)
        }.andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString

        return objectMapper.readTree(responseJson).get("token").asText()
    }

    @Test
    fun `should log activity for existing user`() {
        // 1. Register a user
        val username = "testuser_${UUID.randomUUID()}"
        val registerCommand = RegisterUserCommand(
            username = username,
            email = "test_${UUID.randomUUID()}@example.com",
            password = "testPassword123",
            timezone = "Europe/Berlin"
        )

        val userResponseJson = mockMvc.post("/api/v1/users") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(registerCommand)
        }.andExpect {
            status { isCreated() }
        }.andReturn().response.contentAsString

        val userId = objectMapper.readTree(userResponseJson).get("id").asText()
        
        // 2. Login to get token
        val token = getAuthToken(username)

        // 3. Log activity with Token
        val logCommand = LogActivityCommand(
            userId = UUID.fromString(userId),
            type = ActivityType.STEPS,
            value = 1000
        )

        mockMvc.post("/api/v1/activities") {
            header("Authorization", "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(logCommand)
        }.andExpect {
            status { isCreated() }
            jsonPath("$.type") { value("STEPS") }
            jsonPath("$.value") { value(1000) }
        }
    }

    @Test
    fun `should return 401 when logging activity without token`() {
        val logCommand = LogActivityCommand(
            userId = UUID.randomUUID(),
            type = ActivityType.STEPS,
            value = 1000
        )

        mockMvc.post("/api/v1/activities") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(logCommand)
        }.andExpect {
            status { isForbidden() } // Spring Security returns 403 Forbidden by default if not authenticated and no entry point configured
        }
    }

    @Test
    fun `should return 404 when logging activity for non-existent user`() {
        // We still need a valid token to reach the controller logic
        val username = "testuser_${UUID.randomUUID()}"
        val registerCommand = RegisterUserCommand(
            username = username,
            email = "test_${UUID.randomUUID()}@example.com",
            password = "testPassword123",
            timezone = "Europe/Berlin"
        )
        mockMvc.post("/api/v1/users") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(registerCommand)
        }
        val token = getAuthToken(username)

        val logCommand = LogActivityCommand(
            userId = UUID.randomUUID(),
            type = ActivityType.STEPS,
            value = 1000
        )

        mockMvc.post("/api/v1/activities") {
            header("Authorization", "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(logCommand)
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `should return 400 when logging activity with negative value`() {
        // We still need a valid token to reach the controller logic
        val username = "testuser_${UUID.randomUUID()}"
        val registerCommand = RegisterUserCommand(
            username = username,
            email = "test_${UUID.randomUUID()}@example.com",
            password = "testPassword123",
            timezone = "Europe/Berlin"
        )
        mockMvc.post("/api/v1/users") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(registerCommand)
        }
        val token = getAuthToken(username)

        val logCommand = LogActivityCommand(
            userId = UUID.randomUUID(),
            type = ActivityType.STEPS,
            value = -1
        )

        mockMvc.post("/api/v1/activities") {
            header("Authorization", "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(logCommand)
        }.andExpect {
            status { isBadRequest() }
        }
    }
}
