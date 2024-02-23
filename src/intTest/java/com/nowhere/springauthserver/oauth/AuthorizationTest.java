package com.nowhere.springauthserver.oauth;

import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.Test;

import static com.nowhere.springauthserver.security.SecurityConstants.LOGIN_PATH;

public class AuthorizationTest extends BaseOauthFlowTest {

    @Test
    void whenLoggedAndRequestingTokenThenRedirectsToClientApplication() throws Exception {
        this.webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        this.webClient.getOptions().setRedirectEnabled(false);
        var login = signIn(this.webClient.getPage(LOGIN_PATH), DEFAULT_USERNAME, DEFAULT_PASSWORD);
        String verifier = generateCodeVerifier();
        String challenge = generateCodeChallenge(verifier);
        // allow redirects to go to the custom consent endpoint and then to the custom consent page
        this.webClient.getOptions().setRedirectEnabled(true);
        HtmlPage page = webClient.getPage(generateAuthorizationRequest(challenge));
        this.webClient.getOptions().setRedirectEnabled(false);
        assertConsentPage(page);
        WebResponse consentPage = consent(page).getWebResponse();
        assertConsentResponsePage(consentPage);
    }
}
