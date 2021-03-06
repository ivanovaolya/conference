package com.epam.cm.tests;

import com.epam.cm.base.EndpointUrl;
import com.epam.cm.base.SimpleBaseTest;
import com.epam.cm.base.TextConstants;
import com.epam.cm.jira.Jira;
import com.epam.cm.utils.JsonLoader;
import io.restassured.http.ContentType;
import org.junit.Test;

import static com.epam.cm.tests.RegistrationNewUser.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.nullValue;

public class RegistrationNewUserByAdmin extends SimpleBaseTest {

    private String validContent  = JsonLoader.asString("RegistrationNewUserByAdminInvalidDataAbsentParam.json");
    private String invalidContentAbsentParam  = JsonLoader.asString("RegistrationNewUserByAdminInvalidDataAbsentParam.json");
    private String invalidContentExistingUser  = JsonLoader.asString("RegistrationNewUserByAdminInvalidDataExistingUser.json");

    @Test
    @Jira("6689")
    public void positiveRegistrationUserByAdminTest() {

        given()
                .contentType(ContentType.JSON)
                .baseUri(config.baseHost)
                .auth().preemptive().basic(config.adminUser, config.adminPassword)
                .cookie(TOKEN, response.cookie(TOKEN))
                .header(X_TOKEN, response.cookie(TOKEN))
                .body("{\"fname\": \"fnametest1\", \"lname\": \"lnametest1\", " +
                        "\"mail\": \"" + "autoUser" + getCurrentTimeStamp() + "@mailtest1.com\"," +
                        " \"password\": \"1testtest1\",\"confirm\": \"1testtest1\", \"roleName\" : \"ROLE_SPEAKER\" }")
        .when()
                .post(EndpointUrl.USER_CREATE)
                .
        then()
                .log().all()
                .statusCode(202)
                .assertThat()
                .body(TextConstants.ERROR, nullValue(), TextConstants.RESULT, hasToString(TextConstants.SUCCESS));

    }

    @Test
    @Jira("6694")
    public void absentParamRegistrationUserByAdminTest() {

        given()
                .contentType(ContentType.JSON)
                .baseUri(config.baseHost)
                .auth().preemptive().basic(config.adminUser, config.adminPassword)
                .cookie(TOKEN, response.cookie(TOKEN))
                .header(X_TOKEN, response.cookie(TOKEN))
                .body(invalidContentAbsentParam)
                .
        when()
                .post(EndpointUrl.USER_CREATE)
                .
        then()
                .log().all()
                .statusCode(400)
                .assertThat()
                .body(TextConstants.ERROR, hasToString(TextConstants.EMPTY_FIELDS));

    }

    @Test
    @Jira("7040")
    public void unauthorizedRegistrationUserByAdminTest() {

        given()
                .contentType(ContentType.JSON)
                .baseUri(config.baseHost)
                .cookie(TOKEN, response.cookie(TOKEN))
                .header(X_TOKEN, response.cookie(TOKEN))
                .body("{\"fname\": \"fnametest1\", \"lname\": \"lnametest1\", " +
                "\"mail\": \"" + "autoUser" + getCurrentTimeStamp() + "@mailtest1.com\"," +
                " \"password\": \"1testtest1\",\"confirm\": \"1testtest1\", \"roleName\" : \"ROLE_SPEAKER\" }")

                .
        when()
                .post(EndpointUrl.USER_CREATE)
                .
        then()
                .log().all()
                .statusCode(401)
                .assertThat()
                .body(TextConstants.ERROR, hasToString(TextConstants.UNAUTHORIZED));

    }
    @Test
    @Jira("6691")
    public void negativeRegistrationExistingUserByAdminTest() {

        given()
                .contentType(ContentType.JSON)
                .baseUri(config.baseHost)
                .auth().preemptive().basic(config.adminUser, config.adminPassword)
                .cookie(TOKEN, response.cookie(TOKEN))
                .header(X_TOKEN, response.cookie(TOKEN))
                .body(invalidContentExistingUser)
                .
        when()
                .post(EndpointUrl.USER_CREATE)
                .
        then()
                .log().all()
                .statusCode(409)
                .assertThat()
                .body(TextConstants.ERROR, hasToString(TextConstants.EXISTING_EMAIL));
    }
}

