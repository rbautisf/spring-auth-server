package com.nowhere.springauthserver.oauth;

import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;

import static com.nowhere.springauthserver.security.SecurityConstants.LOGIN_PATH;
import static org.assertj.core.api.Assertions.assertThat;

public class FlowTest extends BaseOauthFlowTest {
    @Test
    void whenLoggingInAndRequestingTokenWithValidAccessCode() throws IOException {
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
        // get the token
        WebRequest request = buildTokenRequest(accessCode, verifier);
        WebResponse responseToken = this.webClient.loadWebResponse(request);
        assertThat(responseToken.getStatusCode()).isEqualTo(HttpStatus.OK.value());
        var content = responseToken.getContentAsString();
        assertThat(content).contains(OAuth2TokenType.ACCESS_TOKEN.getValue());
    }
}
