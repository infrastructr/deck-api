package dev.infrastructr.deck.api.controllers;

import dev.infrastructr.deck.WebTestBase;
import dev.infrastructr.deck.api.actions.HostActions;
import dev.infrastructr.deck.api.actions.InventoryActions;
import dev.infrastructr.deck.api.actions.ProjectActions;
import dev.infrastructr.deck.api.actions.UserActions;
import dev.infrastructr.deck.api.entities.Host;
import dev.infrastructr.deck.api.entities.Inventory;
import dev.infrastructr.deck.api.entities.Project;
import dev.infrastructr.deck.api.requests.CreateHostRequest;
import dev.infrastructr.deck.data.entities.User;
import io.restassured.http.Cookie;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static dev.infrastructr.deck.api.builders.CreateHostRequestBuilder.createHostRequest;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.HttpStatus.OK;

public class HostControllerTest extends WebTestBase {

    @Autowired
    private UserActions userActions;

    @Autowired
    private ProjectActions projectActions;

    @Autowired
    private InventoryActions inventoryActions;

    @Autowired
    private HostActions hostActions;

    @Test
    public void shouldCreate(){
        User user = userActions.create();
        Cookie cookie = userActions.authenticate(user);
        Project project = projectActions.create(cookie);
        Inventory inventory = inventoryActions.create(cookie, project.getId());
        CreateHostRequest request = createHostRequest()
            .withInventoryId(inventory.getId())
            .build();

        given(documentationSpec)
            .filter(getDocument("host-create"))
            .cookie(cookie)
            .body(request)
            .contentType("application/json")
        .when()
            .post("/inventories/{inventoryId}/hosts", inventory.getId())
        .then()
            .assertThat()
            .statusCode(is(OK.value()))
            .and()
            .body("id", is(notNullValue()))
            .body("name", is(request.getName()))
            .body("description", is(request.getDescription()));
    }

    @Test
    public void shouldGetByInventoryId(){
        User user = userActions.create();
        Cookie cookie = userActions.authenticate(user);
        Project project = projectActions.create(cookie);
        Inventory inventory = inventoryActions.create(cookie, project.getId());
        Host host = hostActions.create(cookie, inventory.getId());

        given(documentationSpec)
            .filter(getDocument("host-get-by-inventory-id"))
            .cookie(userActions.authenticate(user))
            .contentType("application/json")
        .when()
            .get("/inventories/{inventoryId}/hosts", inventory.getId())
        .then()
            .assertThat()
            .statusCode(is(OK.value()))
            .and()
            .body("content[0].id", is(host.getId().toString()))
            .body("content[0].name", is(host.getName()))
            .body("content[0].description", is(host.getDescription()));
    }

    @Test
    public void shouldGetOnePerPageSortedByProjectId(){
        User user = userActions.create();
        Cookie cookie = userActions.authenticate(user);
        Project project = projectActions.create(cookie);
        Inventory inventory = inventoryActions.create(cookie, project.getId());
        Host host = hostActions.create(cookie, inventory.getId());
        Host anotherHost = hostActions.create(cookie, inventory.getId());
        String sort = "name,desc";
        Host expectedHost = host.getName().compareTo(anotherHost.getName()) > 0
            ? host
            : anotherHost;

        given(documentationSpec)
            .filter(getDocument("host-get-by-inventory-id"))
            .cookie(userActions.authenticate(user))
            .contentType("application/json")
        .when()
            .get("/inventories/{inventoryId}/hosts?page=0&size=1&sort={sort}", inventory.getId(), sort)
        .then()
            .assertThat()
            .statusCode(is(OK.value()))
            .and()
            .body("content[0].id", is(expectedHost.getId().toString()))
            .body("content[0].name", is(expectedHost.getName()))
            .body("content[0].description", is(expectedHost.getDescription()));
    }

    @Test
    public void shouldGetById(){
        User user = userActions.create();
        Cookie cookie = userActions.authenticate(user);
        Project project = projectActions.create(cookie);
        Inventory inventory = inventoryActions.create(cookie, project.getId());
        Host host = hostActions.create(cookie, inventory.getId());

        given(documentationSpec)
            .filter(getDocument("host-get-by-id"))
            .cookie(userActions.authenticate(user))
            .contentType("application/json")
        .when()
            .get("/hosts/{hostId}", host.getId())
        .then()
            .assertThat()
            .statusCode(is(OK.value()))
            .and()
            .body("id", is(host.getId().toString()))
            .body("name", is(host.getName()))
            .body("description", is(host.getDescription()));
    }
}
