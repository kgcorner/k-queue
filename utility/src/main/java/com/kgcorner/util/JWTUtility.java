package com.kgcorner.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTUtility {
    private static final String HEADER_JWT="{\"typ\":\"jwt\",\"alg\":\"HS256\"}";
    private static final String PROPERTIES_FILE="/utility.manager.properties";
    private static final String JWT_SECRET_IDENTIFIER="jwt.secret";
    private static final String ISSUER="kgcorner";
    private static final String JWT_SECRET = "adYCGZjJcIx0pswtJ4TO";
    public static String generateJWT(String payload) {
        String jwt = null;
        try {
            Algorithm algorithmHS = Algorithm.HMAC256(JWT_SECRET);
            Map<String, Object> header = new HashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");
            Map<String, Object> claim = new HashMap<>();
            claim.put("payload", payload);
            claim.put("issuer", ISSUER);
            jwt = JWT.create().withClaim("payload", payload)
                    .withIssuer(ISSUER)
                    .withIssuedAt(new Date())
                    .withClaim("payload", payload)
                    .withHeader(header).sign(algorithmHS);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return jwt;
    }


    public static String verify(String jwt) {
        String payload = null;
        try {
            Algorithm algorithmHS = Algorithm.HMAC256(JWT_SECRET);
            DecodedJWT decodedJWT = JWT.decode(jwt);
            Claim claim = decodedJWT.getClaim("payload");
            if(claim != null)
                payload = claim.asString();
        }
        catch (JWTDecodeException x) {

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return payload;
    }

    private static InputStream getPropertiesFileStream() {
        InputStream is = JWTUtility.class.getResourceAsStream(PROPERTIES_FILE);
        return is;
    }
}

