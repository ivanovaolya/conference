package com.epam.cm.steps.jbehave;


import com.epam.cm.dto.CredentialsDTO;
import com.epam.cm.steps.serenity.LoginPageSteps;
import net.thucydides.core.annotations.Steps;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.model.ExamplesTable;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class LoginPageDefinitionSteps {

    @Steps
    private LoginPageSteps loginPageSteps;

    @Given("the user logged as: $credentials")
    public void unsignedUserLoginAs(ExamplesTable table){
        CredentialsDTO user = table.getRowsAs(CredentialsDTO.class).get(0);

        loginPageSteps.unsignedUserInHomePage();
        loginPageSteps.clickOnAccountMenu();
        loginPageSteps.typeLoginAndPassword(user);
        loginPageSteps.clickSignInButton();


    }

    @When("user logs out")
    @Given("user logs out")
    public void whenUserLogsOut() {
        loginPageSteps.logout();
    }

    @Then("user is redirected to Home page")
    public void thenUserIsRedirectedToHomePage() {
        assertTrue(loginPageSteps.isHomePageOpened());
    }

    @Then("there is \"Your Account\" button in page header")
    public void thenThereIsYourAccountButtonInPageHeader() {
        assertThat(loginPageSteps.getAccountMenuTitle(),is("Your Account"));
    }

    @Given("the unsigned user accesses home page")
    public void unsignedUserIsOnHomePage() {
        loginPageSteps.unsignedUserInHomePage();
    }

    @Given("user clicks 'Your Account' menu option")
    public void userClicksYourAccountButtonInPageHeader() {
        loginPageSteps.clickOnAccountMenu();
    }

    @Given("user filled in login form: $activityTable")
    public void givenUserFilledInLoginFormEmailPassword(ExamplesTable data) {


        CredentialsDTO user = new CredentialsDTO() {
            {
                setEmail(data.getRowAsParameters(0, true).valueAs("email", String.class));
                setPassword(data.getRowAsParameters(0, true).valueAs("password", String.class));

            }
        };

        loginPageSteps.typeLoginAndPassword(user);
    }

    @When("user clicks SignIn button on login form")
    public void whenUserClicksSignInButtonOnLoginForm() {
        loginPageSteps.clickSignInButton();
    }

    @Then("\"Your Account\" replaced by \"$text\"")
    public void thenYourAccountReplacedBySpeakerssAccount(String namedAccount) {

        assertThat(loginPageSteps.getAccountMenuTitle(), is(namedAccount));
    }

    @Then("there is 'Sign Out' button in account menu")
    public void thenThereIsSignOutButtonInAccountMenu() {
        assertTrue(loginPageSteps.isLoggedIn());
    }

    @Then("user still in login form")
    public void thenUserStillInLoginForm() {
        assertTrue(loginPageSteps.isSignInFormOpened());
    }

    @Then("password field is highlighted")
    public void passwordFieldIsHighlighted() {
        assertTrue("password field  not hightlighted",loginPageSteps.isPasswordFieldIsHighlighted());
    }

    @Then("password error message is displayed: \"$msg\"")
    public void isWrongPassword(String msg) {

        assertThat(loginPageSteps.getPasswordValidationMsg(), is(msg));
    }

    @Then("login field is highlighted")
    public void loginFieldIsHighlighted() {

        assertTrue("login field  not hightlighted",loginPageSteps.isLoginFieldIsHighlighted());
    }

    @Then("login error message is displayed: \"$msg\"")
    public void isWrongLogin(String msg) {

        assertThat(loginPageSteps.getLoginValidationMsg(), is(msg));
    }


}
