package in.vikramaditya.MudrikaVyavastha.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JWTUtil {

    @Value("${jwt.secret.key}")
    private String secretString;

    private Key secretKey;

    // token validity (ms) - 1 hour
    private final long jwtExpirationMs = 60L * 60L * 1000L;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secretString.getBytes());
    }

    /**
     * Generate token using username as subject.
     * You can add extra claims via the claims map.
     */
    public String generateToken(String username) {
        return generateToken(new HashMap<>(), username);
    }

    public String generateToken(Map<String, Object> extraClaims, String username) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(username)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + jwtExpirationMs))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Optional: create a refreshed token (same subject, new expiry).
     */
    public String refreshToken(String token) {
        String username = extractUsernameSafe(token);
        if (username == null) return null;
        return generateToken(username);
    }

    /**
     * Extract username (subject) from token.
     * This returns null if token invalid/expired.
     */
    public String extractUsernameSafe(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (ExpiredJwtException e) {
            // token expired -> return null (caller can decide)
            return null;
        } catch (JwtException e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    // Preferred: throws ExpiredJwtException or JwtException to caller if you want strict behavior
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        if (claims == null) return null;
        return claimsResolver.apply(claims);
    }

    /**
     * Validate token by username
     */
    public boolean validateToken(String token, String username) {
        try {
            String tokenUsername = extractUsername(token);
            if (tokenUsername == null) return false;
            return tokenUsername.equals(username) && !isTokenExpired(token);
        } catch (ExpiredJwtException e) {
            return false;
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * Validate token by UserDetails (helper overload)
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        if (userDetails == null) return false;
        return validateToken(token, userDetails.getUsername());
    }

    private boolean isTokenExpired(String token) {
        Date exp = extractExpiration(token);
        if (exp == null) return true;
        return exp.before(new Date());
    }

    /**
     * Parses and returns all claims.
     * Returns null on errors (expired/invalid).
     * Uses small clock skew to tolerate time drift between servers.
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .setAllowedClockSkewSeconds(600) // allow 10 minutes skew
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            // rethrow if you want strict behavior, or return null (we return null)
            return null;
        } catch (JwtException e) {
            // invalid signature / malformed / unsupported
            return null;
        }
    }
}
