package com.pulserival.api.infrastructure.web

import tools.jackson.databind.ObjectMapper
import com.pulserival.api.application.service.LogActivityCommand
import com.pulserival.api.application.service.RegisterUserCommand
import com.pulserival.api.domain.model.ActivityType
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

    @Test
    fun `should log activity for existing user`() {
        // 1. Register a user
        val registerCommand = RegisterUserCommand(
            username = "testuser_${UUID.randomUUID()}",
            email = "test_${UUID.randomUUID()}@example.com",
            timezone = "Europe/Berlin"
        )

        val userResponseJson = mockMvc.post("/api/v1/users") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(registerCommand)
        }.andExpect {
            status { isCreated() }
        }.andReturn().response.contentAsString

        val userId = objectMapper.readTree(userResponseJson).get("id").asText()

        // 2. Log activity
        val logCommand = LogActivityCommand(
            userId = UUID.fromString(userId),
            type = ActivityType.STEPS,
            value = 1000
        )

        mockMvc.post("/api/v1/activities") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(logCommand)
        }.andExpect {
            status { isCreated() }
            jsonPath("$.type") { value("STEPS") }
            jsonPath("$.value") { value(1000) }
        }
    }

    @Test
    fun `should return 404 when logging activity for non-existent user`() {
        val logCommand = LogActivityCommand(
            userId = UUID.randomUUID(),
            type = ActivityType.STEPS,
            value = 1000
        )

        mockMvc.post("/api/v1/activities") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(logCommand)
        }.andExpect {
            status { isNotFound() }
            jsonPath("$.title") { value("Not Found") }
            jsonPath("$.type") { value("https://pulserival.com/errors/resource-not-found") }
        }
    }

    @Test
    fun `should return 400 when logging activity with negative value`() {
        val logCommand = LogActivityCommand(
            userId = UUID.randomUUID(),
            type = ActivityType.STEPS,
            value = -1
        )

        mockMvc.post("/api/v1/activities") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(logCommand)
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.title") { value("Bad Request") }
            jsonPath("$.type") { value("https://pulserival.com/errors/invalid-request") }
        }
    }
}
