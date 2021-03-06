package com.epam.cm.tests;

import com.epam.cm.base.*;
import com.epam.cm.jira.Jira;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasToString;

public class UpdateConferenceTests extends SimpleBaseTest {


    @Test
    @Jira("6866")
    public void updateConferenceAdminTest(){

        given()
                .contentType(ContentType.JSON)
                .baseUri(config.baseHost)
                .auth().preemptive().basic(config.adminUser, config.adminPassword)
                .cookie(SimpleBaseTest.TOKEN, response.cookie(SimpleBaseTest.TOKEN))
                .header(SimpleBaseTest.X_TOKEN, response.cookie(SimpleBaseTest.TOKEN))
                .body(ConferenceConstants.CONFERENCE_BODY_JSON)
                .
        when()
                .patch(EndpointUrl.UPDATE_CONFERENCE)
                .
        then().log().all()
                .statusCode(200)
                .assertThat()
                .body(TextConstants.ERROR, Matchers.nullValue());
    }

    // Test is failed on old server
    // Bug ID: EPMFARMKPP-6895
    // Link to Bug: https://jirapct.epam.com/jira/browse/EPMFARMKPP-6895
    @Test
    @Jira("6864")
    @Ignore
    public void updateConferenceOrganiserTest(){

        given()
                .contentType(ContentType.JSON)
                .baseUri(config.baseHost)
                .auth().preemptive().basic(config.organiserUser, config.organiserPassword)
                .cookie(SimpleBaseTest.TOKEN, response.cookie(SimpleBaseTest.TOKEN))
                .header(SimpleBaseTest.X_TOKEN, response.cookie(SimpleBaseTest.TOKEN))
                .body(ConferenceConstants.CONFERENCE_BODY_JSON)
                .
        when()
                .patch(EndpointUrl.UPDATE_CONFERENCE)
                .
        then().log().all()
                .statusCode(200).assertThat()
                .body(TextConstants.ERROR, Matchers.nullValue());
    }

    @Test
    @Jira("6863")
    public void updateConferenceUserWithInvalidCredentialsTest(){

        given()
                .contentType(ContentType.JSON)
                .baseUri(config.baseHost)
                .auth().preemptive().basic(TextConstants.WRONG_USER, TextConstants.WRONG_PASSWORD)
                .cookie(SimpleBaseTest.TOKEN, response.cookie(SimpleBaseTest.TOKEN))
                .header(SimpleBaseTest.X_TOKEN, response.cookie(SimpleBaseTest.TOKEN))
                .body(ConferenceConstants.CONFERENCE_BODY_JSON)
                .
        when()
                .patch(EndpointUrl.UPDATE_CONFERENCE)
                .
        then().log().all()
                .statusCode(401)
                .assertThat()
                .body(TextConstants.ERROR, hasToString(TextConstants.LOGIN_ERROR));
    }

    @Test
    @Jira("6864")
    public void updateNonExistingConferenceAdminTest(){

        given()
                .contentType(ContentType.JSON)
                .baseUri(config.baseHost)
                .auth().preemptive().basic(config.adminUser, config.adminPassword)
                .cookie(SimpleBaseTest.TOKEN, response.cookie(SimpleBaseTest.TOKEN))
                .header(SimpleBaseTest.X_TOKEN, response.cookie(SimpleBaseTest.TOKEN))
                .body(ConferenceConstants.NON_EXISTING_CONFERENCE_BODY_JSON)
                .
        when()
                .patch(EndpointUrl.UPDATE_NON_EXISTING_CONFERENCE)
                .
        then().log().all()
                .statusCode(404);
    }
}
