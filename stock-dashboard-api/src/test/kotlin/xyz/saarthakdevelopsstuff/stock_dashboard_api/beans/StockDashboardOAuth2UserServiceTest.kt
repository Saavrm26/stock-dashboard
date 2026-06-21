package xyz.saarthakdevelopsstuff.stock_dashboard_api.beans

import org.junit.jupiter.api.*
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.context.annotation.Import
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import xyz.saarthakdevelopsstuff.stock_dashboard_api.TestContainersConfiguration
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.factories.UserFactory
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.factories.WatchListFactory
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.UserRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.WatchListRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.WatchList
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest
@Import(TestContainersConfiguration::class)
@DisplayName("StockDashboardOAuth2UserService Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class StockDashboardOAuth2UserServiceTest {

    @SpyBean
    private lateinit var oidcUserService: OidcUserService

    @Autowired
    private lateinit var stockDashboardOAuth2UserService: StockDashboardOAuth2UserService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var watchListRepository: WatchListRepository

    @Autowired
    private lateinit var userFactory: UserFactory

    @Autowired
    private lateinit var watchListFactory: WatchListFactory

    @BeforeEach
    fun cleanup() {
        // Clean up in proper order due to foreign key constraints
        watchListRepository.findAll().forEach { watchList: WatchList ->
            watchListRepository.delete(watchList)
        }
        userRepository.findAll().forEach { user ->
            userRepository.delete(user)
        }
    }

    /**
     * Helper method to create a mock OidcUserRequest with the given user attributes.
     *
     * @param userName the username to include in the request
     * @param email the email to include in the request
     * @param fullName the full name to include in the request
     * @return a mock OidcUserRequest with the specified attributes set as claims
     */
    private fun createMockOidcUserRequest(userName: String, email: String, fullName: String): OidcUserRequest {
        val clientRegistration = createClientRegistration()
        val mockAccessToken = OAuth2AccessToken(
            OAuth2AccessToken.TokenType.BEARER,
            "mock-token-value",
            Instant.now(),
            Instant.now().plusSeconds(3600),
            setOf("openid", "profile", "phone", "email")
        )
        val mockIdToken = OidcIdToken("mock-id-token-value", Instant.now(), Instant.now().plusSeconds(3600), mapOf(
            "sub" to userName,
            "email" to email,
            "name" to fullName
            "username" to userName
        ))

        return OidcUserRequest(
            clientRegistration,
            mockAccessToken,
            mockIdToken
        )
    }

    /**
     * Helper method to create a mock OidcUser with the given attributes.
     * Creates a DefaultOidcUser with proper claims structure including email, username, and name.
     *
     * @param userName the username to include in the user claims
     * @param email the email to include in the user claims
     * @param fullName the full name to include in the user claims
     * @return a DefaultOidcUser with the specified attributes set as claims
     */
    private fun createMockOidcUser(userName: String, email: String, fullName: String): DefaultOidcUser {
        val claims: Map<String, Any> = mapOf(
            "email" to email,
            "username" to userName,
            "name" to fullName
        )

        val idToken = OidcIdToken.withTokenValue("id-token-value")
            .claims { claims }
            .build()

        val accessToken = OAuth2AccessToken(
            OAuth2AccessToken.TokenType.BEARER,
            "access-token-value",
            Instant.now(),
            Instant.now().plusSeconds(3600)
        )

        return DefaultOidcUser(
            authorities = emptyList(),
            idToken = idToken,
            userInfo = claims,
            nameAttributeKey = IdTokenClaimNames.SUB
        )
    }

    /**
     * Helper method to create a basic ClientRegistration for test purposes.
     *
     * @return a ClientRegistration configured for testing
     */
    private fun createClientRegistration(): ClientRegistration {
        return ClientRegistration.withRegistrationId("test-registration")
            .clientId("test-client-id")
            .clientSecret("test-client-secret")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("http://localhost:8080/login/oauth2/code/test")
            .scope("openid", "profile", "email")
            .authorizationUri("http://auth-server/oauth/authorize")
            .tokenUri("http://auth-server/oauth/token")
            .userInfoUri("http://auth-server/userinfo")
            .userNameAttributeName(IdTokenClaimNames.SUB)
            .clientName("Test Client")
            .build()
    }

    @Test
    @Order(1)
    @DisplayName("loadUser - new user gets saved in DB with watchlist attached")
    fun testLoadUser_NewUser_CreatesUserWithWatchlist() {
        // Arrange
        val userName = "testuser1"
        val email = "test@example.com"
        val fullName = "Test User"

        val mockRequest = createMockOidcUserRequest(userName, email, fullName)
        val mockOidcUser = createMockOidcUser(userName, email, fullName)

        whenever(oidcUserService.loadUser(any())).thenReturn(mockOidcUser)

        // Pre-conditions: User should not exist, no watchlists
        assertNull(userRepository.findById(userName), "User should not exist initially")
        assertTrue(watchListRepository.findAll().isEmpty(), "No watchlists should exist initially")

        // Act - Call the public loadUser method
        val result = stockDashboardOAuth2UserService.loadUser(mockRequest)

        // Assert - Verify delegate was called
        verify(oidcUserService).loadUser(mockRequest)

        // Assert - Verify result
        assertNotNull(result,  "Result should not be null" )
        assertEquals(userName, result.name,  "Result name should match username" )

        // Assert - User should be created in database
        val createdUser = userRepository.findById(userName)
        assertNotNull(createdUser,  "User should be created" )
        assertEquals(userName, createdUser.id,  "User ID should match" )
        assertEquals(email, createdUser.email,  "User email should match" )
        assertEquals(fullName, createdUser.fullName,  "User full name should match" )

        // Assert - Watchlist should be created and associated with user
        val watchlists = watchListRepository.findAll()
        assertEquals(1, watchlists.size,  "Exactly one watchlist should be created" )

        val watchlist = watchlists[0]
        assertNotNull(watchlist.id) { "Watchlist ID should be generated" }
        assertEquals("My watch list", watchlist.name,  "Watchlist name should match" )
        assertEquals("My followed stocks", watchlist.description,  "Watchlist description should match" )
        assertNotNull(watchlist.createdBy,  "Watchlist should have a creator" )
        assertEquals(userName, watchlist.createdBy!!.id,  "Watchlist should be associated with correct user" )
    }

    @Test
    @Order(2)
    @DisplayName("Transactional - duplicate user creation prevented, no watchlist created")
    fun testTransactional_DuplicateUser_PreventsWatchlistCreation() {
        // Arrange
        val userName = "testuser2"
        val email = "test2@example.com"
        val fullName = "Test User 2"

        // First, manually create a user with the same ID to cause constraint violation
        val existingUser = userFactory.createUser(fullName, userName, email)
        userRepository.save(existingUser)

        val newFullName = "Test New User"
        val newEmail = "newemail@example.com"

        val mockRequest = createMockOidcUserRequest(userName, newEmail, newFullName)
        val mockOidcUser = createMockOidcUser(userName, newEmail, newFullName)

        whenever(oidcUserService.loadUser(any())).thenReturn(mockOidcUser)

        // Pre-conditions: Check initial state
        assertEquals(1, userRepository.count(), "One user should exist initially")
        assertTrue(watchListRepository.findAll().isEmpty(), "No watchlists should exist initially")

        // Act - Try to create duplicate user
        val result = stockDashboardOAuth2UserService.loadUser(mockRequest)

        // Assert - Verify delegate was called
        verify(oidcUserService).loadUser(mockRequest)

        // Assert - Result should be returned (existing user not created again)
        assertNotNull(result) { "Result should not be null" }

        // Assert - No new watchlist should be created
        val watchlists = watchListRepository.findAll()
        assertTrue(watchlists.isEmpty(),  "No watchlist should be created for existing user" )

        // Assert - Only one user should still exist (the original)
        assertEquals(1, userRepository.count()," \"User count should remain unchanged\" " )

        // Assert - Original user should still exist with unchanged data
        val originalUser = userRepository.findById(userName)
        assertNotNull(originalUser) { "Original user should still exist" }
        assertEquals(fullName, originalUser.fullName, "Original user data should be unchanged")
        assertEquals(email, originalUser.email, "Original user email should be unchanged")
    }

    @Test
    @Order(3)
    @DisplayName("loadUser - existing user returns without creating new watchlist")
    fun testLoadUser_ExistingUser_ReturnsWithoutNewWatchlist() {
        // Arrange - Create user and watchlist manually
        val userName = "testuser3"
        val email = "test3@example.com"
        val fullName = "Test User 3"

        val existingUser = userFactory.createUser(fullName, userName, email)
        userRepository.save(existingUser)

        var existingWatchlist = watchListFactory.createEmptyUserWatchList(
            watchListName = "My watch list",
            watchListDescription = "My followed stocks",
            user = existingUser
        )
        existingWatchlist = watchListRepository.save(existingWatchlist)

        val mockRequest = createMockOidcUserRequest(userName, email, fullName)
        val mockOidcUser = createMockOidcUser(userName, email, fullName)

        whenever(oidcUserService.loadUser(any())).thenReturn(mockOidcUser)

        // Pre-conditions
        assertEquals(1, userRepository.count(), "One user should exist")
        assertEquals(1, watchListRepository.count(), "One watchlist should exist")

        // Act - Call loadUser for existing user
        val result = stockDashboardOAuth2UserService.loadUser(mockRequest)

        // Assert - Verify delegate was called
        verify(oidcUserService).loadUser(mockRequest)

        // Assert - Result should be returned
        assertNotNull(result) { "Result should not be null" }
        assertEquals(userName, result.name,  "Result name should match username" )

        // Assert - No duplicate user created
        assertEquals(1, userRepository.count(), " \"User count should remain unchanged\" ")

        // Assert - No duplicate watchlist created
        assertEquals(1, watchListRepository.count(), " \"Watchlist count should remain unchanged\" ")

        // Assert - Original watchlist should still exist
        val watchlist = watchListRepository.findById(existingWatchlist.id!!)
        assertTrue(watchlist.isPresent) { "Original watchlist should still exist" }
        assertEquals("My watch list", watchlist.get().name, "Original watchlist data should be unchanged" )
    }

    @Test
    @Order(4)
    @DisplayName("getUser - happy case: returns existing user")
    fun testGetUser_HappyCase_ReturnsExistingUser() {
        // Arrange
        val fullName = "Test User 4"
        val userName = "testuser4"
        val email = "test4@example.com"

        val user = userFactory.createUser(fullName, userName, email)
        userRepository.save(user)

        // Act
        val foundUser = stockDashboardOAuth2UserService.getUser(userName)

        // Assert
        assertNotNull(foundUser) { "User should be found" }
        assertEquals(userName, foundUser.id,  "User ID should match" )
        assertEquals(email, foundUser.email,  "User email should match" )
        assertEquals(fullName, foundUser.fullName,  "User full name should match" )
    }

    @Test
    @Order(5)
    @DisplayName("getUser - edge case: returns null for non-existent user")
    fun testGetUser_EdgeCase_ReturnsNullForNonExistentUser() {
        // Arrange
        val nonExistentUserName = "nonexistentuser"

        // Act
        val foundUser = stockDashboardOAuth2UserService.getUser(nonExistentUserName)

        // Assert
        assertNull(foundUser,  "Should return null for non-existent user" )
    }

    @Test
    @Order(6)
    @DisplayName("getUser - edge case: returns null for empty input")
    fun testGetUser_EdgeCase_ReturnsNullForEmptyInput() {
        // Arrange - empty string input

        // Act
        val foundUser = stockDashboardOAuth2UserService.getUser("")

        // Assert
        assertNull(foundUser,  "Should return null for empty username" )
    }

    @Test
    @Order(7)
    @DisplayName("getUser - edge case: can find user after creating via loadUser")
    fun testGetUser_EdgeCase_FindUserAfterLoadUser() {
        // Arrange
        val fullName = "Test User 5"
        val userName = "testuser5"
        val email = "test5@example.com"

        val mockRequest = createMockOidcUserRequest(userName, email, fullName)
        val mockOidcUser = createMockOidcUser(userName, email, fullName)

        whenever(oidcUserService.loadUser(any())).thenReturn(mockOidcUser)

        // Create user through service's public method
        stockDashboardOAuth2UserService.loadUser(mockRequest)

        // Act
        val foundUser = stockDashboardOAuth2UserService.getUser(userName)

        // Assert
        assertNotNull(foundUser) { "User created through loadUser should be found via getUser" }
        assertEquals(userName, foundUser.id,  "User ID should match" )
        assertEquals(email, foundUser.email,  "User email should match" )
        assertEquals(fullName, foundUser.fullName,  "User full name should match" )
    }

    @Test
    @Order(8)
    @DisplayName("loadUser - multiple users get created with各自 watchlists")
    fun testLoadUser_MultipleUsers_CreatedWith各自Watchlists() {
        // Arrange - Create multiple mock users
        val users = listOf(
            Triple("testuser6", "user6@example.com", "User Six"),
            Triple("testuser7", "user7@example.com", "User Seven"),
            Triple("testuser8", "user8@example.com", "User Eight")
        )

        // Act - Create users through loadUser
        users.forEach { (userName, email, fullName) ->
            val mockRequest = createMockOidcUserRequest(userName, email, fullName)
            val mockOidcUser = createMockOidcUser(userName, email, fullName)

            whenever(oidcUserService.loadUser(any())).thenReturn(mockOidcUser)

            stockDashboardOAuth2UserService.loadUser(mockRequest)
        }

        // Assert - All users should be created
        assertEquals(users.size.toLong(), userRepository.count(),  "All users should be created" )

        // Assert - All watchlists should be created
        assertEquals(users.size.toLong(), watchListRepository.count(),  "All watchlists should be created" )

        // Assert - Each user should have their watchlist
        users.forEach { (userName, _, _) ->
            val user = userRepository.findById(userName)
            assertNotNull(user,  "User $userName should be found" )

            val userWatchlists = watchListRepository.findAll().filter { it.createdBy!!.id == userName }
            assertEquals(1, userWatchlists.size,  "User $userName should have exactly one watchlist" )
            assertEquals("My watch list", userWatchlists[0].name,  "Watchlist name should match expected" )
        }
    }
}
