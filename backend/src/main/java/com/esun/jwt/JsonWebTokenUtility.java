package com.esun.jwt;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import jakarta.annotation.PostConstruct;
@Component
public class JsonWebTokenUtility {
    @Value("${jwt.token.expire}")
    private long expireMinutes;
    @Value("${jwt.secret.base64}")
    private String jwtSecretBase64;
    private final String issuer = "financial-product";
    private byte[] signingKey;
    @PostConstruct
    public void initializeSigningKey() {
        try {
            signingKey = Base64.getDecoder().decode(jwtSecretBase64);
            if (signingKey.length < 64) {
                throw new IllegalStateException(
                        "JWT HS512 signing key must be at least 64 bytes");
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(
                    "Invalid JWT signing key Base64 format", e);
        }
    }
    public String createToken(String userId) {
        Instant now = Instant.now();
        Instant expire = now.plusSeconds(expireMinutes * 60);
        try {
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .issuer(issuer)
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(expire))
                    .subject(userId)
                    .build();
            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS512),
                    claimsSet);
            signedJWT.sign(new MACSigner(signingKey));
            return signedJWT.serialize();
        } catch (Exception e) {
            throw new IllegalStateException(
                    "JWT create failed", e);
        }
    }
    public String validateToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            if (!JWSAlgorithm.HS512.equals(
                    signedJWT.getHeader().getAlgorithm())) {
                return null;
            }
            JWSVerifier verifier = new MACVerifier(signingKey);
            if (signedJWT.verify(verifier)
                    && issuer.equals(claimsSet.getIssuer())
                    && claimsSet.getExpirationTime() != null
                    && new Date().before(claimsSet.getExpirationTime())) {
                return claimsSet.getSubject();
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
