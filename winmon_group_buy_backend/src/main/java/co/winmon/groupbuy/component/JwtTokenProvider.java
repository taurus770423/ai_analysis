package co.winmon.groupbuy.component;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final String secret;
    private final long expirationMs;
    private final JWSSigner signer;
    private final JWSVerifier verifier;

    public JwtTokenProvider(@Value("${jwt.secret-key}") String secret,
                            @Value("${jwt.expiration-ms}") long expirationMs) throws JOSEException {
        this.secret = secret;
        this.expirationMs = expirationMs;

        //
        this.signer = new MACSigner(secret.getBytes());
        this.verifier = new MACVerifier(secret.getBytes());
    }

    //
    public String generateToken(Authentication authentication) {
        String lineUserId = authentication.getName();
        return createToken(lineUserId);
    }

    //
    public String generateToken(String lineUserId) {
        return createToken(lineUserId);
    }

    private String createToken(String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        //
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(subject)
                .issueTime(now)
                .expirationTime(expiryDate)
                .build();

        //
        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.HS512),
                claimsSet
        );

        try {
            //
            signedJWT.sign(signer);
            //
            return signedJWT.serialize();
        } catch (JOSEException e) {
            //
            throw new RuntimeException("Failed to sign JWT token", e);
        }
    }

    //
    public String getUserIdFromToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (ParseException e) {
            //
            throw new RuntimeException("Failed to parse JWT token", e);
        }
    }

    //
    public boolean validateToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            //
            if (!signedJWT.verify(verifier)) {
                return false;
            }

            //
            Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (expiration == null || expiration.before(new Date())) {
                return false;
            }

            return true;
        } catch (Exception ex) {
            //
            return false;
        }
    }
}