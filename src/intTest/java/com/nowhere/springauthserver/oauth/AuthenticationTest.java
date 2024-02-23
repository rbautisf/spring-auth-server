package com.nowhere.springauthserver.oauth;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.nowhere.springauthserver.config.BaseIntegrationTest;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.web.util.UriComponentsBuilder;

import static com.nowhere.springauthserver.security.SecurityConstants.LOGIN_PATH;
import static org.assertj.core.api.Assertions.assertThat;

public class AuthenticationTest extends BaseOauthFlowTest {

    @Test
    void whenLoginSuccessfulThenDisplayBadRequestError() throws IOException {
        HtmlPage page = this.webClient.getPage(LOGIN_PATH);
        assertLoginPage(page);
        this.webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        WebResponse signInResponse = signIn(page, DEFAULT_USERNAME, DEFAULT_PASSWORD).getWebResponse();
        assertThat(signInResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());    // Error page
    }

    @Test
    void whenLoginFailsThenDisplayBadCredentials() throws IOException {
        HtmlPage page = this.webClient.getPage(LOGIN_PATH);

        HtmlPage loginErrorPage = signIn(page, DEFAULT_USERNAME, "wrong-password");

        HtmlElement alert = loginErrorPage.querySelector("div[role=\"alert\"]");
        assertThat(alert).isNotNull();
        assertThat(alert.asNormalizedText()).isEqualTo("Bad credentials");
    }

    @Test
    void whenNotLoggedInAndRequestingTokenThenRedirectsToLogin() throws IOException {
        String verifier = generateCodeVerifier();
        String challenge = generateCodeChallenge(verifier);
        HtmlPage page = this.webClient.getPage(generateAuthorizationRequest(challenge));
        assertLoginPage(page);
    }

    @Test
    public void whenLoggingInAndRequestingTokenThenRedirectsToClientApplication() throws IOException {
        // Log in
        this.webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        this.webClient.getOptions().setRedirectEnabled(false);
        var login = signIn(this.webClient.getPage(LOGIN_PATH), DEFAULT_USERNAME, DEFAULT_PASSWORD);
        String verifier = generateCodeVerifier();
        String challenge = generateCodeChallenge(verifier);
        this.webClient.getOptions().setRedirectEnabled(true);
        HtmlPage page = webClient.getPage(generateAuthorizationRequest(challenge));
        assertConsentPage(page);
        this.webClient.getOptions().setRedirectEnabled(false);
        WebResponse consentPage = consent(page).getWebResponse();

        // get the access code from the consent response
        String location = consentPage.getResponseHeaderValue(HttpHeaders.LOCATION);
        String accessCode = location.substring(location.indexOf("code=") + 5);

        assertThat(accessCode).isNotNull();
    }


}
