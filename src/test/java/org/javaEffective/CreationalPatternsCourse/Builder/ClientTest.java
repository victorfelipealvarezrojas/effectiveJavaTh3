package org.javaEffective.CreationalPatternsCourse.Builder;

import CreationalPatternsCourse.Builder.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;


@DisplayName("Test Builder pattern")
class ClientTest {
    private User user;

    @BeforeEach
    void setUp() {
        // setter values
        user = new User();
        user.setBirthday(LocalDate.of(1960, 5, 6));
        user.setFirstName("Ron");
        user.setLastName("Swanson");

        Address address = new Address();
        address.setHouseNumber("100");
        address.setStreet("State Street");
        address.setCity("Pawnee");
        address.setState("Indiana");
        address.setZipcode("47998");
        user.setAddress(address);
    }

    @Test
    @DisplayName("Should correctly initialize User properties")
    void UserTest() {
        assertEquals("100", user.getAddress().getHouseNumber());
        assertEquals("State Street", user.getAddress().getStreet());
        assertEquals("Pawnee", user.getAddress().getCity());
    }

    @Test
    @DisplayName("Should build UserDTO using the director method")
    void UserBuilderDirectorTest() {
        UserDTO dto = directorBuild(new UserWebDTOBuilder(), user);
        assertTrue(dto.getAddress().contains("State Street"));
    }

    // director
    private UserDTO directorBuild(UserDTOBuilder builder, User user) {
        return builder.withFirstName(user.getFirstName())
                .withLastName(user.getLastName())
                .withBirthday(user.getBirthday())
                .withAddress(user.getAddress())
                .build();
    }

    @Test
    @DisplayName("Should build User without using the director method")
    void UserBuilderTest() {

    }
}