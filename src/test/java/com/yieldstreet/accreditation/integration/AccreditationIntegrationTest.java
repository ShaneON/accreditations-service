package com.yieldstreet.accreditation.integration;

import com.yieldstreet.accreditation.model.Accreditation;
import com.yieldstreet.accreditation.model.AccreditationStatus;
import com.yieldstreet.accreditation.model.AccreditationType;
import com.yieldstreet.accreditation.repository.AccreditationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@EnableScheduling
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccreditationIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private AccreditationRepository accreditationRepository;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        accreditationRepository.findAllAccreditations().clear(); // Clear the repository before each test
    }

    @Test
    void testCreateAccreditation_Success() throws Exception {

        String requestBody = """
                {
                  "user_id": "abc123",
                  "accreditation_type": "BY_NET_WORTH",
                  "document": {
                     "name": "2005.pdf",
                     "mime_type": "application/pdf",
                     "content": "ICAiQC8qIjogWyJzcmMvKiJdCiAgICB9CiAgfQp9Cg=="
                    }
                }
                """;

        mockMvc.perform(post("/user/accreditation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accreditation_id").isNotEmpty()); // Verify accreditation_id in response
    }

    @Test
    void testCreateAccreditation_Failure_InvalidFields() throws Exception {

        String invalidRequestBody = """
                {
                  "user_id": "invalid invalid",
                  "accreditation_type": "BY_NET_WORTH",
                  "document": {
                     "name": "2005",
                     "mime_type": "application.pdf",
                     "content": "NOT_BASE_64"
                    }
                }
                """;

        mockMvc.perform(post("/user/accreditation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.userId").value("userId must be a combination of letters and numbers"))
                .andExpect(jsonPath("$.['document.mimeType']").value("mimeType must be in the format '{some text}/{document type}'"))
                .andExpect(jsonPath("$.['document.content']").value("content must be base64 encoded"))
                .andExpect(jsonPath("$.['document.name']").value("name must be in the format '{year}.{document type}'"));
    }

    @Test
    void testCreateAccreditation_Failure_InvalidAccreditationType() throws Exception {

        String invalidRequestBody = """
                {
                  "user_id": "abc123",
                  "accreditation_type": "INVALID",
                  "document": {
                     "name": "2005.pdf",
                     "mime_type": "application/pdf",
                     "content": "ICAiQC8qIjogWyJzcmMvKiJdCiAgICB9CiAgfQp9Cg=="
                    }
                }
                """;

        mockMvc.perform(post("/user/accreditation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Accepted values are: BY_INCOME, BY_NET_WORTH"));
    }

    @Test
    void testFinalizeAccreditation_Success() throws Exception {
        String requestBody = """
                {
                  "user_id": "abc123",
                  "accreditation_type": "BY_NET_WORTH",
                  "document": {
                     "name": "2005.pdf",
                     "mime_type": "application/pdf",
                     "content": "ICAiQC8qIjogWyJzcmMvKiJdCiAgICB9CiAgfQp9Cg=="
                    }
                }
                """;

        String accreditationId = mockMvc.perform(post("/user/accreditation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .replace("{\"accreditation_id\":\"", "")
                .replace("\"}", ""); // Extract accreditation_id from response

        String finalizationRequest = """
                {
                    "outcome": "CONFIRMED"
                }
                """;

        mockMvc.perform(put("/user/accreditation/" + accreditationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(finalizationRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accreditation_id").value(accreditationId));
    }

    @Test
    void testFinalizeAccreditation_Failure_AccreditationDoesNotExist() throws Exception {

        String finalizationRequest = """
                {
                    "outcome": "CONFIRMED"
                }
                """;

        String nonExistentId = "abc123";

        mockMvc.perform(put("/user/accreditation/" + nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(finalizationRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("No Accreditations found with id: " + nonExistentId));
    }

    @Test
    void testGetAllAccreditationsForUser() throws Exception {

        accreditationRepository.saveAccreditation(new Accreditation("id1", "user123", AccreditationType.BY_INCOME, AccreditationStatus.PENDING, LocalDateTime.now()));
        accreditationRepository.saveAccreditation(new Accreditation("id2", "user123", AccreditationType.BY_NET_WORTH, AccreditationStatus.CONFIRMED, LocalDateTime.now()));

        mockMvc.perform(get("/user/user123/accreditation")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value("user123"))
                .andExpect(jsonPath("$.accreditation_statuses.id1.accreditation_type").value("BY_INCOME"))
                .andExpect(jsonPath("$.accreditation_statuses.id1.status").value("PENDING"))
                .andExpect(jsonPath("$.accreditation_statuses.id2.accreditation_type").value("BY_NET_WORTH"))
                .andExpect(jsonPath("$.accreditation_statuses.id2.status").value("CONFIRMED"));
    }

    @Test
    void testAccreditationScheduler() throws Exception {
        accreditationRepository.saveAccreditation(new Accreditation(
                "acc1", "user1", AccreditationType.BY_NET_WORTH, AccreditationStatus.CONFIRMED,
                LocalDateTime.now().minusDays(31)  // Older than 30 days
        ));

        accreditationRepository.saveAccreditation(new Accreditation(
                "acc2", "user2", AccreditationType.BY_INCOME, AccreditationStatus.CONFIRMED,
                LocalDateTime.now().minusDays(29)  // Less than 30 days
        ));

        accreditationRepository.saveAccreditation(new Accreditation(
                "acc3", "user3", AccreditationType.BY_NET_WORTH, AccreditationStatus.PENDING,
                LocalDateTime.now().minusDays(40)  // Older than 30 days but not CONFIRMED
        ));

        Thread.sleep(15000);  // Simulate waiting for the scheduler to execute

        List<Accreditation> accreditations = accreditationRepository.findAllAccreditations();

        // CONFIRMED accreditation older than 30 days is expired
        assertThat(accreditations)
                .anyMatch(a -> a.getAccreditationId().equals("acc1") && a.getStatus() == AccreditationStatus.EXPIRED);

        // CONFIRMED accreditation less than 30 days old is not expired
        assertThat(accreditations)
                .anyMatch(a -> a.getAccreditationId().equals("acc2") && a.getStatus() == AccreditationStatus.CONFIRMED);

        // PENDING accreditation is unaffected
        assertThat(accreditations)
                .anyMatch(a -> a.getAccreditationId().equals("acc3") && a.getStatus() == AccreditationStatus.PENDING);

    }
}
