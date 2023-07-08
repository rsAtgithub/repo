package com.example.phonelocation_1;

import android.util.Log;

import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.CoreUtils;
import com.azure.messaging.webpubsub.models.GetClientAccessTokenOptions;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Class to generate the web-socket for our pub-sub service.
 *
 * In general, the URL consists of data in base64 format and later a keyed-hash.
 * e.g.
 * wss://loc-1-watch4.webpubsub.azure.com/client/hubs/Hub?access_token=eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJodHRwczovL2xvYy0xLXdhdGNoNC53ZWJwdWJzdWIuYXp1cmUuY29tL2NsaWVudC9odWJzL0h1YiIsInN1YiI6IlRyaWFsXzEiLCJyb2xlIjpbIndlYnB1YnN1Yi5qb2luTGVhdmVHcm91cCIsIndlYnB1YnN1Yi5zZW5kVG9Hcm91cCJdLCJleHAiOjE2ODg4MTg3NjR9.UNC-gHOIG-ZeOhDTB25v_9BddwOlfp4PqQjtxEWVD-k
 *
 * The Subparts of URL are:
 * [Fixed URL] wss://loc-1-watch4.webpubsub.azure.com/client/hubs/Hub?access_token=
 * [Header] eyJhbGciOiJIUzI1NiJ9
 * [Body] eyJhdWQiOiJodHRwczovL2xvYy0xLXdhdGNoNC53ZWJwdWJzdWIuYXp1cmUuY29tL2NsaWVudC9odWJzL0h1YiIsInN1YiI6IlRyaWFsXzEiLCJyb2xlIjpbIndlYnB1YnN1Yi5qb2luTGVhdmVHcm91cCIsIndlYnB1YnN1Yi5zZW5kVG9Hcm91cCJdLCJleHAiOjE2ODg4MTg3NjR9
 * [Keyed-hash] .UNC-gHOIG-ZeOhDTB25v_9BddwOlfp4PqQjtxEWVD-k
 *
 * And after decrypting base64 data from Header and Body we have
 * [Header] {"alg":"HS256"}
 * [Body] {"aud":"https://loc-1-watch4.webpubsub.azure.com/client/hubs/Hub","sub":"Trial_1","role":["webpubsub.joinLeaveGroup","webpubsub.sendToGroup"],"exp":1688818764}
 */
public class WebSocketUrlGeneration {
    /** The username for which we want to generate the URL. */
    private String userNameString;

    /** The key to be used for hashing.
     * @Note: Keep this secret and do not commit to version control.
     */
    private String authKeyString;

    // Reference: https://learn.microsoft.com/en-us/azure/azure-web-pubsub/concept-client-protocols
    private static String[] roles = {"webpubsub.joinLeaveGroup", "webpubsub.sendToGroup"};

    private static String embeddedConnectionString = "https://loc-1-watch4.webpubsub.azure.com/client/hubs/Hub";

    private static String webSocketConnectionString = "wss://loc-1-watch4.webpubsub.azure.com/client/hubs/Hub?access_token=";

    /** The constructor.
     *
     * @param userName The username for which we want to generate the URL.
     * @param authKey The key to be used for hashing.
     */
    WebSocketUrlGeneration(String userName, String authKey) {
        userNameString = userName;
        authKeyString = authKey;
    }

    /**
     * API to generate the URL.
     * @return Client URL for Azure pubsub service.
     * @throws URISyntaxException
     *
     * @Note: The documentation from Azure shows how to use classes WebPubSubClientAccessToken and WebPubSubServiceClient.
     * However, on Android, the class WebPubSubServiceClient fails to load at run-time.
     * Thus, by looking at the code at: https://learn.microsoft.com/en-us/azure/azure-web-pubsub/tutorial-pub-sub-messages?tabs=java%2CCloud
     * and by debugging the working code on PC, following routines were written.
     */
    public String run() throws URISyntaxException {
        GetClientAccessTokenOptions option = new GetClientAccessTokenOptions();
        option.setExpiresAfter(Duration.ofDays(2));
        option.addRole(roles[0]);
        option.addRole(roles[1]);
        option.setUserId(userNameString);
        String baseUrl = embeddedConnectionString;
        String key = authKeyString; //;
        AzureKeyCredential ak = new AzureKeyCredential(key);
        String authToken = getAuthenticationToken(baseUrl, option, ak);
        String webSocketUrl = webSocketConnectionString + authToken;
        Log.d("WebSocket3", webSocketUrl);
        return webSocketUrl;
    }

    /**
     * API for calculating base64 encoded connection string parameter.
     * @param audienceUrl The URL to which connection shall be established.
     * @param options Options, such as user-name, time duration etc.
     * @param credential Azure keys.
     * @return String containing base64 encoded connection parameters.
     */
    private String getAuthenticationToken(String audienceUrl, GetClientAccessTokenOptions options, AzureKeyCredential credential) {
        try {
            Duration expiresAfter = Duration.ofHours(1L);
            JWTClaimsSet.Builder claimsBuilder = (new JWTClaimsSet.Builder()).audience(audienceUrl);
            if (options != null) {
                expiresAfter = options.getExpiresAfter() == null ? expiresAfter : options.getExpiresAfter();
                String userId = options.getUserId();
                if (!CoreUtils.isNullOrEmpty(options.getRoles())) {
                    claimsBuilder.claim("role", options.getRoles());
                }

                if (!CoreUtils.isNullOrEmpty(userId)) {
                    claimsBuilder.subject(userId);
                }
            }

            claimsBuilder.expirationTime(Date.from(LocalDateTime.now().plus(expiresAfter).atZone(ZoneId.systemDefault()).toInstant()));
            JWTClaimsSet claims = claimsBuilder.build();
            JWSSigner signer = new MACSigner(credential.getKey().getBytes(StandardCharsets.UTF_8));
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (JOSEException var8) {
            //LOGGER.logThrowableAsError(var8);
            return null;
        }
    }
}
