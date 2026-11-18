package com.spring.lab.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.lab.security.dto.BankUserRegistrationRequest;
import com.spring.lab.security.entity.*;
import com.spring.lab.security.event.SecurityAuthenticationEventListener;
import com.spring.lab.security.filter.SecurityAuditFilter;
import com.spring.lab.security.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BankingSecurityPerimeterApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Autowired
    private BankUserRepository bankUserRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private BankTransactionRepository bankTransactionRepository;

    @Autowired
    private BankCardRepository bankCardRepository;

    @Autowired
    private BankLoanRepository bankLoanRepository;

    @Autowired
    private PublicNoticeRepository publicNoticeRepository;

    @Autowired
    private SupportInquiryRepository supportInquiryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SecurityAuthenticationEventListener securityEventListener;

    private static final String USER_EMAIL = "alex.morgan@apexbank.com";
    private static final String USER_RAW_PASSWORD = "StrongSecurePassword!2026";

    private static final String ADMIN_EMAIL = "admin.clara@apexbank.com";
    private static final String ADMIN_RAW_PASSWORD = "SuperAdminPassword!2026";

    private static final String INACTIVE_USER_EMAIL = "locked.user@apexbank.com";
    private static final String INACTIVE_RAW_PASSWORD = "ValidPassword!2026";

    private static final String RESTRICTED_USER_EMAIL = "restricted.bob@apexbank.com";
    private static final String RESTRICTED_RAW_PASSWORD = "RestrictedPassword!2026";

    private Long sampleAccountNumber = 1002003001L;

    @BeforeEach
    void setUp() {
        // Reset DB & audit events
        supportInquiryRepository.deleteAll();
        publicNoticeRepository.deleteAll();
        bankTransactionRepository.deleteAll();
        bankCardRepository.deleteAll();
        bankLoanRepository.deleteAll();
        bankAccountRepository.deleteAll();
        bankUserRepository.deleteAll();
        securityEventListener.resetCounters();

        // 1. Standard Customer (Alex Morgan) with full user authorities
        BankUser user = new BankUser(
                "Alex Morgan",
                USER_EMAIL,
                "+1-555-019-2831",
                passwordEncoder.encode(USER_RAW_PASSWORD),
                "ROLE_USER"
        );
        user.addAuthority("ROLE_USER");
        user.addAuthority("VIEW_ACCOUNT");
        user.addAuthority("VIEW_BALANCE");
        user.addAuthority("VIEW_CARDS");
        user.addAuthority("VIEW_LOANS");
        BankUser savedUser = bankUserRepository.save(user);

        // Seed Account, Transactions, Card, Loan for Alex
        BankAccount account = bankAccountRepository.save(new BankAccount(
                sampleAccountNumber,
                "SAVINGS",
                "742 Evergreen Terrace, Sector 4",
                new BigDecimal("45250.75"),
                savedUser
        ));

        bankTransactionRepository.save(new BankTransaction(
                account.getAccountNumber(),
                LocalDate.now().minusDays(2),
                "Merchant Terminal Purchase - Tech Store",
                "WITHDRAWAL",
                new BigDecimal("129.99"),
                new BigDecimal("45120.76"),
                savedUser
        ));

        bankCardRepository.save(new BankCard(
                "4532-XXXX-XXXX-8910",
                "CREDIT",
                new BigDecimal("15000.00"),
                new BigDecimal("1250.00"),
                new BigDecimal("13750.00"),
                savedUser
        ));

        bankLoanRepository.save(new BankLoan(
                LocalDate.now().minusMonths(6),
                "HOME_MORTGAGE",
                new BigDecimal("250000.00"),
                new BigDecimal("35000.00"),
                new BigDecimal("215000.00"),
                savedUser
        ));

        // 2. Administrator (Clara Finch) with ROLE_ADMIN
        BankUser admin = new BankUser(
                "Clara Finch",
                ADMIN_EMAIL,
                "+1-555-010-9944",
                passwordEncoder.encode(ADMIN_RAW_PASSWORD),
                "ROLE_ADMIN"
        );
        admin.addAuthority("ROLE_ADMIN");
        admin.addAuthority("VIEW_ACCOUNT");
        admin.addAuthority("VIEW_BALANCE");
        admin.addAuthority("VIEW_CARDS");
        admin.addAuthority("VIEW_LOANS");
        bankUserRepository.save(admin);

        // 3. Deactivated / Inactive User
        BankUser inactiveUser = new BankUser(
                "Locked Account User",
                INACTIVE_USER_EMAIL,
                "+1-555-011-0011",
                passwordEncoder.encode(INACTIVE_RAW_PASSWORD),
                "ROLE_USER"
        );
        inactiveUser.setActive(false); // Inactive status
        inactiveUser.addAuthority("ROLE_USER");
        bankUserRepository.save(inactiveUser);

        // 4. Restricted User (Bob) who has VIEW_ACCOUNT but lacks VIEW_CARDS
        BankUser restrictedUser = new BankUser(
                "Bob Limited",
                RESTRICTED_USER_EMAIL,
                "+1-555-014-4422",
                passwordEncoder.encode(RESTRICTED_RAW_PASSWORD),
                "ROLE_USER"
        );
        restrictedUser.addAuthority("ROLE_USER");
        restrictedUser.addAuthority("VIEW_ACCOUNT");
        // Notably omitting VIEW_CARDS
        bankUserRepository.save(restrictedUser);

        // 5. Seed Public Notice
        publicNoticeRepository.save(new PublicNotice(
                "Scheduled Core Banking Maintenance",
                "ApexBank online infrastructure will undergo routine maintenance from 01:00 to 03:00 UTC.",
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(5),
                true
        ));
    }

    // =========================================================================
    // SECTION 1 & 2: DEFAULT CONFIGURATIONS, FILTER CHAINS & PUBLIC ACCESS
    // =========================================================================

    @Test
    @DisplayName("Section 1: Custom SecurityAuditFilter injects trace header X-ApexBank-Audit-Trace")
    void testAuditFilterInjectsTraceHeader() throws Exception {
        mockMvc.perform(get("/api/v1/auth/health"))
                .andExpect(status().isOk())
                .andExpect(header().exists(SecurityAuditFilter.TRACE_HEADER))
                .andExpect(header().string(SecurityAuditFilter.TRACE_HEADER, startsWith("TRC-")));
    }

    @Test
    @DisplayName("Section 2: Public endpoint /api/v1/notices is accessible without authentication with Cache-Control")
    void testPublicNoticesAccessibleWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/notices"))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", containsString("max-age=60")))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].noticeTitle", is("Scheduled Core Banking Maintenance")));
    }

    @Test
    @DisplayName("Section 2: Public endpoint POST /api/v1/support/inquiries is open without authentication")
    void testPublicSupportInquiriesSubmission() throws Exception {
        String inquiryJson = """
                {
                    "customerName": "Marcus Vance",
                    "contactEmail": "marcus@external.org",
                    "subject": "Branch Inquiry",
                    "message": "Need schedule for safe deposit access."
                }
                """;

        mockMvc.perform(post("/api/v1/support/inquiries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inquiryJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.inquiryId", startsWith("INQ-")))
                .andExpect(jsonPath("$.customerName", is("Marcus Vance")));
    }

    // =========================================================================
    // SECTION 3 & 4: PASSWORD MANAGEMENT & COMPROMISED PASSWORD CHECKER
    // =========================================================================

    @Test
    @DisplayName("Section 4: Registration with a compromised dictionary password fails with 400 Bad Request")
    void testRegistrationFailsWithCompromisedPassword() throws Exception {
        BankUserRegistrationRequest badRequest = new BankUserRegistrationRequest(
                "Hacker Test", "vulnerable@test.com", "+1-555-999-0000", "12345678", "USER"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", containsString("breached password dictionaries")));
    }

    @Test
    @DisplayName("Section 4: Registration with a robust password succeeds, persisting {bcrypt} encoded hash")
    void testRegistrationSucceedsWithStrongPassword() throws Exception {
        BankUserRegistrationRequest goodRequest = new BankUserRegistrationRequest(
                "Diana Prince", "diana.prince@apexbank.com", "+1-555-333-7777", "VeryStrongPassword!9876", "USER"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(goodRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("SUCCESS")))
                .andExpect(jsonPath("$.email", is("diana.prince@apexbank.com")));

        BankUser registered = bankUserRepository.findByEmail("diana.prince@apexbank.com").orElseThrow();
        assertThat(registered.getPassword()).startsWith("{bcrypt}");
        assertThat(passwordEncoder.matches("VeryStrongPassword!9876", registered.getPassword())).isTrue();
    }

    // =========================================================================
    // SECTION 5: CUSTOM AUTHENTICATION PROVIDER
    // =========================================================================

    @Test
    @DisplayName("Section 5: Custom AuthenticationProvider succeeds for active user with valid credentials")
    void testAuthenticationProviderSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/users/profile")
                        .with(httpBasic(USER_EMAIL, USER_RAW_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(USER_EMAIL)))
                .andExpect(jsonPath("$.name", is("Alex Morgan")))
                .andExpect(jsonPath("$.role", is("ROLE_USER")))
                .andExpect(jsonPath("$.active", is(true)))
                .andExpect(jsonPath("$.authorities", hasItem("VIEW_ACCOUNT")));

        assertThat(securityEventListener.getSuccessCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Section 5: Custom AuthenticationProvider rejects invalid password credentials with 401")
    void testAuthenticationProviderRejectsBadPassword() throws Exception {
        mockMvc.perform(get("/api/v1/users/profile")
                        .with(httpBasic(USER_EMAIL, "TotallyWrongPassword!123")))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("X-ApexBank-Error-Reason", "Authentication failed"))
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.error", is("Unauthorized")));

        assertThat(securityEventListener.getFailureCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Section 5: Custom AuthenticationProvider enforces business rule: deactivated user rejected")
    void testAuthenticationProviderRejectsDeactivatedUser() throws Exception {
        mockMvc.perform(get("/api/v1/users/profile")
                        .with(httpBasic(INACTIVE_USER_EMAIL, INACTIVE_RAW_PASSWORD)))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("X-ApexBank-Error-Reason", "Authentication failed"))
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.message", containsString("deactivated")));

        assertThat(securityEventListener.getFailureCount()).isEqualTo(1);
    }

    // =========================================================================
    // SECTION 6: CUSTOM EXCEPTION HANDLING & JSON ERROR PAYLOADS
    // =========================================================================

    @Test
    @DisplayName("Section 6: Unauthenticated request to protected API triggers CustomBasicAuthenticationEntryPoint")
    void testUnauthenticatedAccessTriggersCustomEntryPoint() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/my-account?accountNumber=" + sampleAccountNumber))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("X-ApexBank-Error-Reason", "Authentication failed"))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.error", is("Unauthorized")))
                .andExpect(jsonPath("$.path", is("/api/v1/accounts/my-account")));
    }

    @Test
    @DisplayName("Section 6: Insufficient permissions triggers CustomAccessDeniedHandler emitting 403 JSON")
    void testInsufficientPermissionsTriggersCustomAccessDeniedHandler() throws Exception {
        mockMvc.perform(get("/api/v1/admin/audit")
                        .with(httpBasic(USER_EMAIL, USER_RAW_PASSWORD)))
                .andExpect(status().isForbidden())
                .andExpect(header().string("X-ApexBank-Denied-Reason", "Access denied: insufficient permissions"))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.status", is(403)))
                .andExpect(jsonPath("$.error", is("Forbidden")))
                .andExpect(jsonPath("$.path", is("/api/v1/admin/audit")));
    }

    // =========================================================================
    // SECTION 7: CORS & CSRF DEFENSES
    // =========================================================================

    @Test
    @DisplayName("Section 7: Preflight OPTIONS request from whitelisted origin receives CORS headers")
    void testCorsPreflightReturnsAllowedHeaders() throws Exception {
        mockMvc.perform(options("/api/v1/accounts/my-account")
                        .header("Origin", "http://localhost:4200")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Access-Control-Request-Headers", "Authorization, Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"))
                .andExpect(header().string("Access-Control-Allow-Methods", containsString("GET")));
    }

    @Test
    @DisplayName("Section 7: CsrfCookieFilter renders XSRF-TOKEN cookie upon GET requests")
    void testCsrfCookieFilterLoadsToken() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1/notices"))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getCookie("XSRF-TOKEN")).isNotNull();
    }

    // =========================================================================
    // SECTION 8: FINE-GRAINED AUTHORIZATION (ROLES & AUTHORITIES)
    // =========================================================================

    @Test
    @DisplayName("Section 8: User with VIEW_ACCOUNT authority can access /api/v1/accounts/my-account")
    void testAuthorityViewAccountGranted() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/my-account?accountNumber=" + sampleAccountNumber)
                        .with(httpBasic(USER_EMAIL, USER_RAW_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber", is(sampleAccountNumber.intValue())))
                .andExpect(jsonPath("$.accountType", is("SAVINGS")))
                .andExpect(jsonPath("$.balance", is(45250.75)));
    }

    @Test
    @DisplayName("Section 8: User with VIEW_BALANCE authority can access /api/v1/transactions/my-balance")
    void testAuthorityViewBalanceGranted() throws Exception {
        mockMvc.perform(get("/api/v1/transactions/my-balance?accountNumber=" + sampleAccountNumber)
                        .with(httpBasic(USER_EMAIL, USER_RAW_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].transactionSummary", containsString("Merchant Terminal")));
    }

    @Test
    @DisplayName("Section 8: Restricted user Bob (lacking VIEW_CARDS) is denied access (403) to /api/v1/cards/my-cards")
    void testRestrictedUserLackingAuthorityIsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/cards/my-cards?email=" + RESTRICTED_USER_EMAIL)
                        .with(httpBasic(RESTRICTED_USER_EMAIL, RESTRICTED_RAW_PASSWORD)))
                .andExpect(status().isForbidden())
                .andExpect(header().string("X-ApexBank-Denied-Reason", "Access denied: insufficient permissions"))
                .andExpect(jsonPath("$.status", is(403)));
    }

    @Test
    @DisplayName("Section 8: Administrator Clara (ROLE_ADMIN) can access administrative audit endpoint")
    void testAdminCanAccessAdminAuditEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/admin/audit")
                        .with(httpBasic(ADMIN_EMAIL, ADMIN_RAW_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.securityStatus", is("SECURE_PERIMETER_ENFORCED")))
                .andExpect(jsonPath("$.totalRegisteredBankUsers", is(4)));
    }
}
