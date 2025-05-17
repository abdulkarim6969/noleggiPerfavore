package com.example.progettoinfonoleggi.service.jwt;

import com.example.progettoinfonoleggi.model.token.RevokedToken;
import com.example.progettoinfonoleggi.repository.jwt.RevokedTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTservice {

    @Autowired
    private RevokedTokenRepository revokedTokenRepository;

    private String secretkey = "";

    public JWTservice() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            SecretKey sk = keyGen.generateKey();
            secretkey = Base64.getEncoder().encodeToString(sk.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String generateToken(String username) {
        try {
            Map<String, Object> claims = new HashMap<>();
            return Jwts.builder()
                    .claims()
                    .add(claims)
                    .subject(username)
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000))
                    .and()
                    .signWith(getKey())
                    .compact();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error generating Token");
            return null;
        }
    }

    private SecretKey getKey() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secretkey);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error generating Key");
            return null;
        }
    }

    public String extractUserName(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error extracting Username from token");
            return null;
        }
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        try {
            final Claims claims = extractAllClaims(token);
            return claimResolver.apply(claims);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error extracting Claim from token");
            return null;
        }
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error extracting Claim from token");
            return null;
        }
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String userName = extractUserName(token);
            return (userName != null && userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error Validating  token");

            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error eisTokenExpired");

            return true;
        }
    }

    private Date extractExpiration(String token) {
        try {
            return extractClaim(token, Claims::getExpiration);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error extractExpiration");

            return new Date(0); // default to epoch if failed
        }
    }

    public Boolean revokeToken(String token) {
        Date expiration = extractExpiration(token);
        RevokedToken revokedToken = new RevokedToken();
        revokedToken.setToken(token);
        revokedToken.setExpiration(expiration.toInstant());
        if (!revokedTokenRepository.existsByToken(token)) {
            System.out.println("savingg");
            revokedTokenRepository.save(revokedToken);
            return true;
        }
        else {
            System.out.println("not savingg");

            return false;
        }
    }

}
