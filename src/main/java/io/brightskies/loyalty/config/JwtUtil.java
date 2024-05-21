package io.brightskies.loyalty.config;

import io.brightskies.loyalty.user.Entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Log4j2
@Service
public class JwtUtil {

    private final String SECRET_KEY = "secret";

    public String extractUsername(String token) {
        log.info("Running extractUsername(" + token + ") in JwtUtil...");
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        log.info("Running extractExpiration(" + token + ") in JwtUtil...");
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        log.info("Running extractClaim(" + token + "," + claimsResolver.toString() + ") in JwtUtil...");
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        log.info("Running extractAllClaims(" + token + ") in JwtUtil...");
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        log.info("Running isTokenExpired(" + token + ") in JwtUtil...");
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(User user) {
        log.info("Running generateToken(" + user.toString() + ") in JwtUtil...");
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, user.getEmail());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        log.info("Running createToken(" + claims.toString() + ", " + subject + ") in JwtUtil...");

        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    public Boolean validateToken(String token, User user) {
        log.info("Running validateToken(" + token + ", " + user.toString() + ") in JwtUtil...");
        final String username = extractUsername(token);
        return (username.equals(user.getEmail()) && !isTokenExpired(token));
    }
}
