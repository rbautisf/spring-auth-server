package com.nowhere.springauthserver.persistence;

import com.nowhere.springauthserver.persistence.entity.AuthUser;
import com.nowhere.springauthserver.persistence.entity.Role;
import com.nowhere.springauthserver.persistence.repository.AuthUserRepository;
import java.util.Set;
import java.util.UUID;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//import com.nowhere.springauthserver.persistence.AuthUserFixture;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

@DataJpaTest
public class AuthUserRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private AuthUserRepository authUserRepository;

    @Test
    public void testFindById() {
        // Setup: Create a new AuthUser and persist it to the database
        AuthUser expectedAuthUser = AuthUserFixture .defaultAuthUserWithRolesFixture(Set.of(RoleFixture.roleFixture(UUID.randomUUID(), Role.RoleType.USER)));
//        var expectedAuthUser =  new AuthUser();
        testEntityManager.persist(new AuthUser());
        testEntityManager.flush();

        // Exercise: Find the AuthUser by ID
        AuthUser actualAuthUser = authUserRepository.findById(expectedAuthUser.getId()).orElse(null);

        // Verify: The actual AuthUser returned from the repository matches the expected AuthUser
//        assertEquals(expectedAuthUser, actualAuthUser);
    }

    // TODO: Add more tests here...
}
