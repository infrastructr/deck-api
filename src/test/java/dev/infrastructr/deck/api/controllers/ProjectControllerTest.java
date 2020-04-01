package dev.infrastructr.deck.api.controllers;

import dev.infrastructr.deck.WebTestBase;
import dev.infrastructr.deck.api.actions.ProjectActions;
import dev.infrastructr.deck.api.actions.UserActions;
import dev.infrastructr.deck.api.entities.Project;
import dev.infrastructr.deck.api.requests.CreateProjectRequest;
import dev.infrastructr.deck.data.entities.Organization;
import dev.infrastructr.deck.data.entities.User;
import io.restassured.http.Cookie;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static dev.infrastructr.deck.api.builders.CreateProjectRequestBuilder.createProjectRequest;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.HttpStatus.OK;

public class ProjectControllerTest extends WebTestBase {

    @Autowired
    private UserActions userActions;

    @Autowired
    private ProjectActions projectActions;

    @Test
    public void shouldCreate(){
        User user = userActions.create();
        Organization organization = user.getOrganization();
        CreateProjectRequest request = createProjectRequest().build();

        given(documentationSpec)
            .filter(getDocument("project-create"))
            .cookie(userActions.authenticate(user))
            .body(request)
            .contentType("application/json")
        .when()
            .post("/projects")
        .then()
            .assertThat()
            .statusCode(is(OK.value()))
            .and()
            .body("id", is(notNullValue()))
            .body("name", is(request.getName()))
            .body("author.id", is(user.getId().toString()))
            .body("author.name", is(user.getName()))
            .body("owner.id", is(organization.getId().toString()))
            .body("owner.name", is(organization.getName()));
    }

    @Test
    public void shouldGetAll(){
        User user = userActions.create();
        Organization organization = user.getOrganization();
        Cookie cookie = userActions.authenticate(user);
        Project project = projectActions.create(cookie);

        given(documentationSpec)
            .filter(getDocument("project-get-all"))
            .cookie(userActions.authenticate(user))
            .contentType("application/json")
        .when()
            .get("/projects")
        .then()
            .assertThat()
            .statusCode(is(OK.value()))
            .and()
            .body("[0].id", is(notNullValue()))
            .body("[0].name", is(project.getName()))
            .body("[0].author.id", is(user.getId().toString()))
            .body("[0].author.name", is(user.getName()))
            .body("[0].owner.id", is(organization.getId().toString()))
            .body("[0].owner.name", is(organization.getName()));
    }

    @Test
    public void shouldGetById(){
        User user = userActions.create();
        Organization organization = user.getOrganization();
        Cookie cookie = userActions.authenticate(user);
        Project project = projectActions.create(cookie);

        given(documentationSpec)
            .filter(getDocument("project-get-by-id"))
            .cookie(userActions.authenticate(user))
            .contentType("application/json")
            .when()
            .get("/projects/{projectId}", project.getId())
            .then()
            .assertThat()
            .statusCode(is(OK.value()))
            .and()
            .body("id", is(notNullValue()))
            .body("name", is(project.getName()))
            .body("author.id", is(user.getId().toString()))
            .body("author.name", is(user.getName()))
            .body("owner.id", is(organization.getId().toString()))
            .body("owner.name", is(organization.getName()));
    }
}
