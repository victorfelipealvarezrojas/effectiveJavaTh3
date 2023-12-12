package org.javaEffective.CreationalPatternsCourse.BuilderAbstract;

import CreationalPatternsCourse.BuilderAbstract.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static CreationalPatternsCourse.BuilderAbstract.UserAbstract.Interest.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Test Builder pattern")
class UserTest {
    @Test
    @DisplayName("Should correctly initialize User properties")
    void UserBuilderTest() {
        User user = new User.Builder()
                .rut("12345678-9")
                .name("Ron")
                .age(38)
                .email("valvarez@vavarez.cl")
                .addInterest(MUSIC)
                .addInterest(PHOTOGRAPHY)
                .addInterest(PROGRAMING)
                .addInterest(PHILOSOPHY)
                .addInterest(POETRY)
                .addInterest(SPORTS)
                .addInterest(NUTRITION).build();

        assertEquals("12345678-9", user.getRut());
        assertEquals("Ron", user.getName());
        assertEquals(38, user.getAge());
        assertEquals("valvarez@vavarez.cl", user.getEmail());
        assertEquals(7, user.getInterests().size());
        assertTrue(user.getInterests().contains(MUSIC));
        assertTrue(user.getInterests().contains(PHOTOGRAPHY));

    }
}