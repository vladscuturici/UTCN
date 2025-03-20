package ro.tuc.ds2020.authorization;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenValidator {

    @Value("${jwt.secret-key}")
    private String secretKey;

    public boolean validateJwtToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
            return !claims.getExpiration().before(new java.util.Date());
        } catch (Exception e) {
            System.out.println("Invalid JWT token: " + e.getMessage());
            System.out.println(token);
            return false;
        }
    }

    public String extractUsernameFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            System.out.println("Failed to extract username: " + e.getMessage());
            return null;
        }
    }

    public String extractUuidFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("uuid", String.class);
        } catch (Exception e) {
            System.out.println("Failed to extract UUID: " + e.getMessage());
            return null;
        }
    }

    public String extractRoleFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("role", String.class);
        } catch (Exception e) {
            System.out.println("Failed to extract role: " + e.getMessage());
            return null;
        }
    }
}