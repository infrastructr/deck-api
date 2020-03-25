package dev.infrastructr.deck.api;

import dev.infrastructr.deck.WebTestBase;
import dev.infrastructr.deck.data.entities.Organization;
import dev.infrastructr.deck.data.entities.Role;
import dev.infrastructr.deck.data.entities.User;
import org.junit.Test;

import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.HttpStatus.OK;

public class UserControllerTest extends WebTestBase {

    @Test
    public void shouldReturnUser(){
        User user = user(PASSWORD);
        Organization organization = user.getOrganization();

        given(documentationSpec)
            .filter(getDocument("user-get-me"))
            .auth().none()
            .cookie(authenticate(user))
        .when()
            .get("/users/me")
        .then()
            .assertThat()
            .statusCode(is(OK.value()))
            .and()
            .body("id", is(user.getId().toString()))
            .body("name", is(user.getName()))
            .body("organization.name", is(organization.getName()))
            .body("organization.id", is(organization.getId().toString()))
            .body("roles", is(
                user.getRoles()
                    .stream()
                    .map(Role::name)
                    .collect(Collectors.toList())));
    }
}
