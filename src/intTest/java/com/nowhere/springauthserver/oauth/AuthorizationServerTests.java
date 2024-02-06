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
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.web.util.UriComponentsBuilder;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthorizationServerTests extends BaseIntegrationTest {
    private final String AUTHORIZATION_URI = "http://localhost:9000/oauth2/token";
    private final String REDIRECT_URI = "https://oidcdebugger.com/debug";
    private final String SHA_256 = "SHA-256";
    private final String AUTH_CLIENT = "nowhere-client:nowhere-secret";
    private final String BASIC_AUTH = "Basic " + Base64.getEncoder().encodeToString(AUTH_CLIENT.getBytes());
    private final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    private final String DEFAULT_USERNAME = "user@user.com";
    private final String DEFAULT_PASSWORD = "user";
    private final String ROOT_PATH = "/";
    private final String LOGIN_PATH = "/login";
    private final String LOGOUT_PATH = "/logout";
    @Autowired
    private WebClient webClient;
    @Autowired
    private EmbeddedDatabase db;

    @BeforeEach
    void setUp() {
        JdbcTemplate template = new JdbcTemplate(db);
        // Delete the content from the tables related to authorization
        // to avoid the use of DirtyContext to reset the context for each test.
        template.execute("DELETE FROM oauth2_authorization");
        template.execute("DELETE FROM oauth2_authorization_consent");
        this.webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
        this.webClient.getOptions().setRedirectEnabled(true);
        this.webClient.getCookieManager().clearCookies();
    }

    @Test
    void whenLoginSuccessfulThenDisplayBadRequestError() throws IOException {
        HtmlPage page = this.webClient.getPage(ROOT_PATH);
        assertLoginPage(page);
        this.webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        WebResponse signInResponse = signIn(page, DEFAULT_USERNAME, DEFAULT_PASSWORD).getWebResponse();
        assertThat(signInResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());    // there is no "default" index page
    }

    @Test
    void whenLoginFailsThenDisplayBadCredentials() throws IOException {
        HtmlPage page = this.webClient.getPage(ROOT_PATH);

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
    void whenLoggingInAndRequestingTokenThenRedirectsToClientApplication() throws Exception {
        this.webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        this.webClient.getOptions().setRedirectEnabled(false);
        signIn(this.webClient.getPage(LOGIN_PATH), DEFAULT_USERNAME, DEFAULT_PASSWORD);
        String verifier = generateCodeVerifier();
        String challenge = generateCodeChallenge(verifier);
        HtmlPage page = webClient.getPage(generateAuthorizationRequest(challenge));
        assertConsentPage(page);
        WebResponse consentPage = consent(page).getWebResponse();
        assertConsentResponsePage(consentPage);
        logout();
    }

    @Test
    void whenLoggingInAndRequestingTokenWithValidAccessCode() throws IOException {
        // Log in
        this.webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        this.webClient.getOptions().setRedirectEnabled(false);
        signIn(this.webClient.getPage(LOGIN_PATH), DEFAULT_USERNAME, DEFAULT_PASSWORD);
        String verifier = generateCodeVerifier();
        String challenge = generateCodeChallenge(verifier);
        HtmlPage page = webClient.getPage(generateAuthorizationRequest(challenge));
        assertConsentPage(page);
        WebResponse consentPage = consent(page).getWebResponse();

        // get the access code from the consent response
        String location = consentPage.getResponseHeaderValue("location");
        String accessCode = location.substring(location.indexOf("code=") + 5);
        // get the token
        WebRequest request = buildTokenRequest(accessCode, verifier);
        WebResponse responseToken = this.webClient.loadWebResponse(request);
        assertThat(responseToken.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        var content = responseToken.getContentAsString();
        assertThat(content).contains("access_token");
        logout();
    }

    private WebRequest buildTokenRequest(String access_code, String verifier) throws MalformedURLException {
        WebRequest request = new WebRequest(
                new URL(AUTHORIZATION_URI),
                HttpMethod.POST
        );
        request.setRequestBody("grant_type=authorization_code&code=" + access_code + "&redirect_uri=" + REDIRECT_URI + "&client_id=nowhere-client&code_verifier=" + verifier);
        request.setAdditionalHeader("Content-Type", CONTENT_TYPE);
        request.setAdditionalHeader("Authorization", BASIC_AUTH);
        return request;
    }


    private <P extends Page> P signIn(HtmlPage page, String username, String password) throws IOException {
        HtmlInput usernameInput = page.querySelector("input[name=\"username\"]");
        HtmlInput passwordInput = page.querySelector("input[name=\"password\"]");
        HtmlButton signInButton = page.querySelector("button");

        usernameInput.type(username);
        passwordInput.type(password);
        return signInButton.click();
    }

    private void logout() throws IOException {
        Page page = this.webClient.getPage(LOGOUT_PATH);
        if (page instanceof HtmlPage logoutPage) {
            HtmlButton logoutButton = logoutPage.querySelector("button");
            if (Objects.equals(logoutButton.getId(), "Log Out")) logoutButton.click();
        }
    }

    private <P extends Page> P consent(HtmlPage consentPage) throws IOException {
        consentPage.querySelectorAll("input[name='scope']").forEach(scope ->
                ((HtmlCheckBoxInput) scope).setChecked(true));
        HtmlButton submitConsentButton = consentPage.querySelector("button[id='submit-consent']");
        return submitConsentButton.click();
    }

    private void assertConsentPage(HtmlPage consentPage) {
        assertThat(consentPage.getTitleText()).isEqualTo("Consent required");
        List<String> scopeIds = new ArrayList<>();
        consentPage.querySelectorAll("input[name='scope']").forEach(scope ->
                scopeIds.add(((HtmlCheckBoxInput) scope).getId())
        );
        assertThat(scopeIds).containsExactlyInAnyOrder("message.read", "message.write");
        HtmlButton submitConsentButton = consentPage.querySelector("button[id='submit-consent']");
        assertThat(submitConsentButton).isNotNull();

    }

    private void assertLoginPage(HtmlPage page) {
        assertThat(page.getUrl().toString()).endsWith(LOGIN_PATH);

        HtmlInput usernameInput = page.querySelector("input[name=\"username\"]");
        HtmlInput passwordInput = page.querySelector("input[name=\"password\"]");
        HtmlButton signInButton = page.querySelector("button");

        assertThat(usernameInput).isNotNull();
        assertThat(passwordInput).isNotNull();
        assertThat(signInButton.getTextContent()).isEqualTo("Sign in");
    }

    private void assertConsentResponsePage(WebResponse consentResponse) {
        assertThat(consentResponse.getStatusCode()).isEqualTo(HttpStatus.MOVED_PERMANENTLY.value());
        String location = consentResponse.getResponseHeaderValue("location");
        assertThat(location).startsWith(REDIRECT_URI);
        assertThat(location).contains("code=");
    }

    private String generateCodeVerifier() {
        byte[] bytes = new byte[32]; // 256 bits
        ThreadLocalRandom.current().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String generateCodeChallenge(String codeVerifier) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance(SHA_256);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] hash = digest.digest(codeVerifier.getBytes());
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);

    }

    private String generateAuthorizationRequest(String challenge) {

        return UriComponentsBuilder
                .fromPath("/oauth2/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", "nowhere-client")
                .queryParam("scope", "message.read message.write")
                .queryParam("code_challenge", challenge)
                .queryParam("code_challenge_method", "S256")
                .queryParam("redirect_uri", REDIRECT_URI)
                .toUriString();
    }
}
