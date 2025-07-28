package com.nowhere.springauthserver.oauth;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.util.UriComponentsBuilder;
import javax.sql.DataSource;

import static com.nowhere.springauthserver.security.SecurityConstants.LOGIN_PATH;
import static org.assertj.core.api.Assertions.assertThat;

public class BaseOauthFlowTest extends BaseIntegrationTest {
    protected final String TOKEN_URI = "http://localhost:9000/oauth2/token";
    protected final String AUTHORIZE_PATH = "/oauth2/authorize";
    protected final String REDIRECT_URI = "https://oidcdebugger.com/debug";
    protected final String CLIENT_ID = "nowhere-client";
    protected final String SHA_256 = "SHA-256";
    protected final String BASIC_AUTH = "Basic " + Base64.getEncoder().encodeToString("nowhere-client:nowhere-secret".getBytes());
    protected final String DEFAULT_USERNAME = "user@user.com";
    protected final String DEFAULT_PASSWORD = "user";

    @Autowired
    protected WebClient webClient;
    
    @Autowired
    protected DataSource dataSource;

    @BeforeEach
    protected void setUp() {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        // Delete the content from the tables related to authorization
        // to avoid the use of DirtyContext to reset the context for each test.
        template.execute("DELETE FROM oauth2_authorization");
        template.execute("DELETE FROM oauth2_authorization_consent");
        this.webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
        this.webClient.getOptions().setRedirectEnabled(true);
        this.webClient.getCookieManager().clearCookies();
    }

    protected WebRequest buildTokenRequest(String access_code, String verifier) throws MalformedURLException {
        WebRequest request = new WebRequest(
                new URL(TOKEN_URI),
                HttpMethod.POST
        );
        // using string format generate the request body
        var requestBody = "grant_type=%s&code=%s&redirect_uri=%s&client_id=%s&code_verifier=%s";
        request.setRequestBody(String.format(
                        requestBody,
                        AuthorizationGrantType.AUTHORIZATION_CODE.getValue(),
                        access_code,
                        REDIRECT_URI,
                        CLIENT_ID,
                        verifier
                )
        );
        request.setAdditionalHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        request.setAdditionalHeader(HttpHeaders.AUTHORIZATION, BASIC_AUTH);
        return request;
    }


    protected  <P extends Page> P signIn(HtmlPage page, String username, String password) throws IOException {
        HtmlInput usernameInput = page.querySelector("input[name=\"username\"]");
        HtmlInput passwordInput = page.querySelector("input[name=\"password\"]");
        HtmlButton signInButton = page.querySelector("button");

        usernameInput.type(username);
        passwordInput.type(password);
        return signInButton.click();
    }

    protected  <P extends Page> P consent(HtmlPage consentPage) throws IOException {
        consentPage.querySelectorAll("input[name='scope']").forEach(scope ->
                ((HtmlCheckBoxInput) scope).setChecked(true));
        HtmlButton submitConsentButton = consentPage.querySelector("button[id='submit-consent']");
        return submitConsentButton.click();
    }

    protected  void assertConsentPage(HtmlPage consentPage) {
        assertThat(consentPage.getTitleText()).isEqualTo("Consent required");
        List<String> scopeIds = new ArrayList<>();
        consentPage.querySelectorAll("input[name='scope']").forEach(scope ->
                scopeIds.add(((HtmlCheckBoxInput) scope).getId())
        );
        assertThat(scopeIds).containsExactlyInAnyOrder("message.read", "message.write");
        HtmlButton submitConsentButton = consentPage.querySelector("button[id='submit-consent']");
        assertThat(submitConsentButton).isNotNull();

    }

    protected  void assertLoginPage(HtmlPage page) {
        assertThat(page.getUrl().toString()).endsWith(LOGIN_PATH);

        HtmlInput usernameInput = page.querySelector("input[name=\"username\"]");
        HtmlInput passwordInput = page.querySelector("input[name=\"password\"]");
        HtmlButton signInButton = page.querySelector("button");

        assertThat(usernameInput).isNotNull();
        assertThat(passwordInput).isNotNull();
        assertThat(signInButton.getTextContent()).isEqualTo("Sign in");
    }

    protected  void assertConsentResponsePage(WebResponse consentResponse) {
        assertThat(consentResponse.getStatusCode()).isEqualTo(HttpStatus.MOVED_PERMANENTLY.value());
        String location = consentResponse.getResponseHeaderValue("location");
        assertThat(location).startsWith(REDIRECT_URI);
        assertThat(location).contains("code=");
    }

    protected  String generateCodeVerifier() {
        byte[] bytes = new byte[32]; // 256 bits
        ThreadLocalRandom.current().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    protected  String generateCodeChallenge(String codeVerifier) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance(SHA_256);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] hash = digest.digest(codeVerifier.getBytes());
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);

    }

    protected  String generateAuthorizationRequest(String challenge) {
        return UriComponentsBuilder
                .fromPath(AUTHORIZE_PATH)
                .queryParam("response_type", "code")
                .queryParam("client_id", CLIENT_ID)
                .queryParam("scope", "message.read message.write")
                .queryParam("code_challenge", challenge)
                .queryParam("code_challenge_method", "S256")
                .queryParam("redirect_uri", REDIRECT_URI)
                .toUriString();
    }

}
