package com.spring.lab.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.lab.security.jwt.config.SecurityConstants;
import com.spring.lab.security.jwt.dto.LoanApplicationRequest;
import com.spring.lab.security.jwt.entity.BankTransaction;
import com.spring.lab.security.jwt.entity.SupportInquiry;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JwtMethodSecurityOAuth2ApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @Value("${application.security.jwt.secret:" + SecurityConstants.JWT_SECRET_DEFAULT_VALUE + "}")
    private String jwtSecret;

    private String generateTestJwt(String username, String authorities, long expiryDurationMs) {
        SecretKey secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .issuer(SecurityConstants.ISSUER)
                .subject(SecurityConstants.TOKEN_SUBJECT)
                .claim(SecurityConstants.CLAIM_USERNAME, username)
                .claim(SecurityConstants.CLAIM_AUTHORITIES, authorities)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expiryDurationMs))
                .signWith(secretKey)
                .compact();
    }

    @Test
    @DisplayName("1. Public endpoint notices should be accessible without credentials")
    void testPublicNoticesEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/notices"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("2. Unauthenticated request to protected accounts endpoint should return 401")
    void testUnauthenticatedAccessRejected() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/my-account")
                        .param("email", "user@apexbank.com"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("3. Request validation filter should reject requests with blocked parameter")
    void testRequestValidationFilterBlocksQueryParam() throws Exception {
        mockMvc.perform(get("/api/v1/notices")
                        .param("blocked_tag", "true"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("4. Request validation filter should reject basic auth with prohibited username")
    void testRequestValidationFilterBlocksProhibitedBasicUser() throws Exception {
        String invalidAuth = "Basic " + Base64.getEncoder().encodeToString("malicious-test:password".getBytes(StandardCharsets.UTF_8));
        mockMvc.perform(get("/api/v1/user/profile")
                        .header(HttpHeaders.AUTHORIZATION, invalidAuth))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("5. Basic login should return signed JWT token in Authorization response header")
    void testBasicLoginIssuesSignedJwt() throws Exception {
        String validAuth = "Basic " + Base64.getEncoder().encodeToString("user@apexbank.com:ApexPass123!".getBytes(StandardCharsets.UTF_8));

        MvcResult result = mockMvc.perform(get("/api/v1/auth/login")
                        .header(HttpHeaders.AUTHORIZATION, validAuth))
                .andExpect(status().isOk())
                .andExpect(header().exists(SecurityConstants.JWT_HEADER))
                .andReturn();

        String jwtHeader = result.getResponse().getHeader(SecurityConstants.JWT_HEADER);
        assertThat(jwtHeader).isNotNull();
        assertThat(jwtHeader).startsWith(SecurityConstants.TOKEN_PREFIX);
    }

    @Test
    @DisplayName("6. Protected endpoint access using valid Bearer JWT should succeed")
    void testAccessWithValidBearerJwt() throws Exception {
        String token = generateTestJwt("user@apexbank.com", "ROLE_USER", 3600000L);

        mockMvc.perform(get("/api/v1/accounts/my-account")
                        .header(HttpHeaders.AUTHORIZATION, SecurityConstants.TOKEN_PREFIX + token)
                        .param("email", "user@apexbank.com"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("7. Access with expired JWT token should return 401")
    void testExpiredJwtRejected() throws Exception {
        String expiredToken = generateTestJwt("user@apexbank.com", "ROLE_USER", -3600000L);

        mockMvc.perform(get("/api/v1/accounts/my-account")
                        .header(HttpHeaders.AUTHORIZATION, SecurityConstants.TOKEN_PREFIX + expiredToken)
                        .param("email", "user@apexbank.com"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("8. Access with tampered JWT token should return 401")
    void testTamperedJwtRejected() throws Exception {
        String token = generateTestJwt("user@apexbank.com", "ROLE_USER", 3600000L);
        String tamperedToken = token.substring(0, token.length() - 5) + "abcde";

        mockMvc.perform(get("/api/v1/accounts/my-account")
                        .header(HttpHeaders.AUTHORIZATION, SecurityConstants.TOKEN_PREFIX + tamperedToken)
                        .param("email", "user@apexbank.com"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("9. Method Security @PreAuthorize should permit loan application below limit")
    @WithMockUser(username = "user@apexbank.com", roles = {"USER"})
    void testPreAuthorizePermitsValidLoan() throws Exception {
        LoanApplicationRequest request = new LoanApplicationRequest("user@apexbank.com", "PERSONAL", 25000.0);

        mockMvc.perform(post("/api/v1/loans/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("10. Method Security @PreAuthorize should reject loan application exceeding limit")
    @WithMockUser(username = "user@apexbank.com", roles = {"USER"})
    void testPreAuthorizeRejectsExcessiveLoan() throws Exception {
        LoanApplicationRequest request = new LoanApplicationRequest("user@apexbank.com", "PERSONAL", 5000000.0);

        mockMvc.perform(post("/api/v1/loans/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("11. Method Security @PostAuthorize should reject access if user does not own account")
    @WithMockUser(username = "other_user@apexbank.com", roles = {"USER"})
    void testPostAuthorizeRejectsUnownedAccount() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/my-account")
                        .param("email", "user@apexbank.com"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("12. Method Security @PreFilter should filter out invalid transfer items")
    @WithMockUser(username = "user@apexbank.com", roles = {"USER"})
    void testPreFilterTransactions() throws Exception {
        List<BankTransaction> list = new ArrayList<>();
        list.add(new BankTransaction("TXN-VALID-1", 1865764534L, "user@apexbank.com", LocalDate.now(), "Valid item", "DEBIT", 100.0, 244900.50));
        list.add(new BankTransaction("TXN-INVALID-2", 1865764534L, "user@apexbank.com", LocalDate.now(), "Negative item", "DEBIT", -50.0, 244900.50));

        MvcResult result = mockMvc.perform(post("/api/v1/transactions/batch-transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(list)))
                .andExpect(status().isCreated())
                .andReturn();

        List<?> returned = objectMapper.readValue(result.getResponse().getContentAsString(), List.class);
        assertThat(returned).hasSize(1);
    }

    @Test
    @DisplayName("13. Method Security @PostFilter should filter out items not belonging to principal")
    @WithMockUser(username = "user@apexbank.com", roles = {"USER"})
    void testPostFilterHistory() throws Exception {
        mockMvc.perform(get("/api/v1/transactions/history")
                        .param("email", "user@apexbank.com"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("14. Method Security @PreFilter should remove blocked inquiry sender names")
    @WithMockUser(username = "user@apexbank.com", roles = {"USER"})
    void testPreFilterSupportInquiries() throws Exception {
        List<SupportInquiry> inquiries = new ArrayList<>();
        inquiries.add(new SupportInquiry("INQ-1", "ValidSender", "user@apexbank.com", "Help", "Inquiry detail", LocalDate.now()));
        inquiries.add(new SupportInquiry("INQ-2", "BlockedSender", "user@apexbank.com", "Spam", "Spam detail", LocalDate.now()));

        MvcResult result = mockMvc.perform(post("/api/v1/inquiries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inquiries)))
                .andExpect(status().isCreated())
                .andReturn();

        List<?> savedList = objectMapper.readValue(result.getResponse().getContentAsString(), List.class);
        assertThat(savedList).hasSize(1);
    }

    @Test
    @DisplayName("15. Federated OAuth2 Resource Server endpoint should authorize via Mock JWT")
    void testFederatedEndpointWithMockJwt() throws Exception {
        mockMvc.perform(get("/api/v1/federated/profile")
                        .with(jwt().jwt(builder -> builder
                                .subject("federated-user-uuid")
                                .claim("realm_access", Map.of("roles", List.of("USER"))))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("16. Federated Admin endpoint should reject non-admin JWT with 403")
    void testFederatedAdminEndpointRejectsStandardUser() throws Exception {
        mockMvc.perform(get("/api/v1/federated/admin/audit")
                        .with(jwt().jwt(builder -> builder
                                .subject("federated-user-uuid")
                                .claim("realm_access", Map.of("roles", List.of("USER"))))))
                .andExpect(status().isForbidden());
    }
}
