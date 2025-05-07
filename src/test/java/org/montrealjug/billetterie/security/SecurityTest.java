// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.security;

import static io.restassured.RestAssured.given;
import static io.restassured.matcher.RestAssuredMatchers.detailedCookie;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.montrealjug.billetterie.security.SecurityConfiguration.AdminProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class SecurityTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    @Autowired
    AdminProperties adminProperties;

    @ParameterizedTest
    @ValueSource(strings = { "events", "bookers" })
    void anonymous_user_should_be_redirected_to_login_to_access_admin_pages(String adminPage) {
        // `/admin/$page` -> `/admin/login` if not authenticated
        given()
            .redirects()
            .follow(false)
            .get("/admin/%s".formatted(adminPage))
            .then()
            .statusCode(302)
            .header("Location", "http://localhost:" + port + "/admin/login");
    }

    @Test
    void admin_credentials_should_be_set_by_admin_properties_with_expected_redirects() {
        // login success -> `/admin/events`
        given()
            .contentType(ContentType.URLENC)
            .formParam("username", adminProperties.adminUsername())
            .formParam("password", adminProperties.adminPassword())
            .post("/admin/login")
            .then()
            .statusCode(302)
            .header("Location", "http://localhost:" + port + "/admin/events");
        // login failed -> to `/`
        given()
            .contentType(ContentType.URLENC)
            .formParam("username", adminProperties.adminUsername())
            .formParam("password", "not-expected-password")
            .post("/admin/login")
            .then()
            .statusCode(302)
            .header("Location", "http://localhost:" + port + "/");
    }

    @Test
    void admin_session_cookie_should_be_secured_and_with_given_name() {
        given()
            .contentType(ContentType.URLENC)
            .formParam("username", adminProperties.adminUsername())
            .formParam("password", adminProperties.adminPassword())
            .post("/admin/login")
            .then()
            .statusCode(302)
            .cookie(
                adminProperties.sessionCookieName(),
                detailedCookie().secured(true).httpOnly(true).path("/admin").sameSite("Strict")
            );
    }

    @ParameterizedTest
    @ValueSource(strings = { "bookers", "events" })
    void admin_should_access_admin_page(String adminPage) {
        var sessionCookie = given()
            .contentType(ContentType.URLENC)
            .formParam("username", adminProperties.adminUsername())
            .formParam("password", adminProperties.adminPassword())
            .post("/admin/login")
            .then()
            .extract()
            .cookie(adminProperties.sessionCookieName());

        given()
            .cookie(adminProperties.sessionCookieName(), sessionCookie)
            .get("http://localhost:" + port + "/admin/%s".formatted(adminPage))
            .then()
            .statusCode(200);
    }

    @Test
    void logout_should_logout_and_redirect_to_index() {
        // login
        var sessionCookie = given()
            .contentType(ContentType.URLENC)
            .formParam("username", adminProperties.adminUsername())
            .formParam("password", adminProperties.adminPassword())
            .post("/admin/login")
            .then()
            .extract()
            .cookie(adminProperties.sessionCookieName());
        // logout
        given()
            .cookie(adminProperties.sessionCookieName(), sessionCookie)
            .redirects()
            .follow(false)
            .get("/admin/logout")
            .then()
            .statusCode(302)
            .header("Location", "http://localhost:" + port + "/")
            .cookie(adminProperties.sessionCookieName(), detailedCookie().path("/").maxAge(0).value(""));
        // `/admin/events` -> `/admin/login`
        given()
            .redirects()
            .follow(false)
            .get("/admin/events")
            .then()
            .statusCode(302)
            .header("Location", "http://localhost:" + port + "/admin/login");
    }

    @ParameterizedTest
    @ValueSource(strings = { "books", "events" })
    void admin_should_not_accept_basic_auth(String adminPage) {
        // 401 for basic auth and admin credentials
        given()
            .redirects()
            .follow(false)
            .auth()
            .basic(adminProperties.adminUsername(), adminProperties.adminPassword())
            .get("http://localhost:" + port + "/admin/%s".formatted(adminPage))
            .then()
            .statusCode(302)
            .header("Location", "http://localhost:" + port + "/admin/login");
        // 401 for basic auth and actuator credentials
        given()
            .redirects()
            .follow(false)
            .auth()
            .basic(adminProperties.actuatorUsername(), adminProperties.actuatorPassword())
            .get("http://localhost:" + port + "/admin/%s".formatted(adminPage))
            .then()
            .statusCode(302)
            .header("Location", "http://localhost:" + port + "/admin/login");
    }

    @Test
    void actuator_should_be_secured_with_basic_auth_and_actuator_credentials() {
        // 401 on unauthenticated direct access
        given()
            .get("http://localhost:" + port + "/actuator")
            .then()
            .statusCode(401)
            .header("WWW-Authenticate", "Basic realm=\"actuator realm\"");
        // 200 with basic auth
        given()
            .auth()
            .basic(adminProperties.actuatorUsername(), adminProperties.actuatorPassword())
            .get("http://localhost:" + port + "/actuator")
            .then()
            .statusCode(200);
        // 403 with admin credentials
        var sessionCookie = given()
            .contentType(ContentType.URLENC)
            .formParam("username", adminProperties.adminUsername())
            .formParam("password", adminProperties.adminPassword())
            .post("/admin/login")
            .then()
            .extract()
            .cookie(adminProperties.sessionCookieName());
        given()
            .cookie(adminProperties.sessionCookieName(), sessionCookie)
            .get("http://localhost:" + port + "/actuator")
            .then()
            .statusCode(403);
    }
}
