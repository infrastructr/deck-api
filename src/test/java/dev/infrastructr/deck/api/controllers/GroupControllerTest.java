package dev.infrastructr.deck.api.controllers;

import dev.infrastructr.deck.ContextCleaner;
import dev.infrastructr.deck.WebTestBase;
import dev.infrastructr.deck.api.actions.GroupActions;
import dev.infrastructr.deck.api.actions.InventoryActions;
import dev.infrastructr.deck.api.entities.Group;
import dev.infrastructr.deck.api.entities.Inventory;
import dev.infrastructr.deck.api.models.TestContext;
import dev.infrastructr.deck.api.requests.CreateGroupRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static dev.infrastructr.deck.api.builders.CreateGroupRequestBuilder.createGroupRequest;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.HttpStatus.OK;

public class GroupControllerTest extends WebTestBase {

    @Autowired
    private InventoryActions inventoryActions;

    @Autowired
    private GroupActions groupActions;

    @Autowired
    private ContextCleaner contextCleaner;

    @Test
    public void shouldCreate(){
        TestContext context = new TestContext();
        Inventory inventory = inventoryActions.create(context);
        CreateGroupRequest request = createGroupRequest()
            .withInventoryId(inventory.getId())
            .build();

        given(documentationSpec)
            .filter(getDocument("group-create"))
            .cookie(context.getCookie())
            .body(request)
            .contentType("application/json")
        .when()
            .post("/inventories/{inventoryId}/groups", inventory.getId())
        .then()
            .assertThat()
            .statusCode(is(OK.value()))
            .and()
            .body("id", is(notNullValue()))
            .body("name", is(request.getName()))
            .body("description", is(request.getDescription()));

        contextCleaner.clean(context);
    }

    @Test
    public void shouldGetByInventoryId(){
        TestContext context = new TestContext();
        Group group = groupActions.create(context);
        Inventory inventory = context.getInventories().get(0);

        given(documentationSpec)
            .filter(getDocument("group-get-by-inventory-id"))
            .cookie(context.getCookie())
            .contentType("application/json")
        .when()
            .get("/inventories/{inventoryId}/groups", inventory.getId())
        .then()
            .assertThat()
            .statusCode(is(OK.value()))
            .and()
            .body("content[0].id", is(group.getId().toString()))
            .body("content[0].name", is(group.getName()))
            .body("content[0].description", is(group.getDescription()));

        contextCleaner.clean(context);
    }

    @Test
    public void shouldGetFilteredByInventoryId(){
        TestContext context = new TestContext();
        Group group = groupActions.create(context);
        Inventory inventory = context.getInventories().get(0);

        given(documentationSpec)
            .filter(getDocument("group-get-filtered"))
            .cookie(context.getCookie())
            .contentType("application/json")
        .when()
            .get("/inventories/{inventoryId}/groups?filter={filter}", inventory.getId(), group.getName())
        .then()
            .assertThat()
            .statusCode(is(OK.value()))
            .and()
            .body("content[0].id", is(group.getId().toString()))
            .body("content[0].name", is(group.getName()))
            .body("content[0].description", is(group.getDescription()));

        contextCleaner.clean(context);
    }

    @Test
    public void shouldGetOnePerPageSortedByInventoryId(){
        TestContext context = new TestContext();
        Inventory inventory = inventoryActions.create(context);
        Group group = groupActions.create(context, inventory.getId());
        Group anotherGroup = groupActions.create(context, inventory.getId());
        String sort = "name,desc";
        Group expectedGroup = group.getName().compareTo(anotherGroup.getName()) > 0
            ? group
            : anotherGroup;

        given(documentationSpec)
            .filter(getDocument("group-get-by-inventory-id"))
            .cookie(context.getCookie())
            .contentType("application/json")
        .when()
            .get("/inventories/{inventoryId}/groups?page=0&size=1&sort={sort}", inventory.getId(), sort)
        .then()
            .assertThat()
            .statusCode(is(OK.value()))
            .and()
            .body("content[0].id", is(expectedGroup.getId().toString()))
            .body("content[0].name", is(expectedGroup.getName()))
            .body("content[0].description", is(expectedGroup.getDescription()));

        contextCleaner.clean(context);
    }

    @Test
    public void shouldGetById(){
        TestContext context = new TestContext();
        Group group = groupActions.create(context);

        given(documentationSpec)
            .filter(getDocument("group-get-by-id"))
            .cookie(context.getCookie())
            .contentType("application/json")
        .when()
            .get("/groups/{groupId}", group.getId())
        .then()
            .assertThat()
            .statusCode(is(OK.value()))
            .and()
            .body("id", is(group.getId().toString()))
            .body("name", is(group.getName()))
            .body("description", is(group.getDescription()));

        contextCleaner.clean(context);
    }
}
