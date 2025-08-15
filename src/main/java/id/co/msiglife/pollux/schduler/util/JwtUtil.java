package id.co.msiglife.pollux.schduler.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private String secret = StaticValue.SECRET_KEY_LOGIN_TOKEN;
    private String expirationTime = StaticValue.EXP_TIME_LOGIN_TOKEN;
    private Key key = Keys.hmacShaKeyFor(secret.getBytes());;

    public Claims getAllClaimsFromToken(String token) {
    	Claims claims = null;
    	try {
			claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
		} catch (Exception e) {
			e.printStackTrace();			
		}
    	return claims;
    }

    public Date getExpirationDateFromToken(String token) {
    	Date expDate = null;
    	try {
			expDate = getAllClaimsFromToken(token).getExpiration();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return expDate;
    }

    public Boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        if(expiration == null) {
        	return true;
        } else {
        	return expiration.before(new Date());
        }
    }

    public Boolean validateToken(String token) {
    	Boolean isExpired = isTokenExpired(token);
    	return !isExpired;
    }

    public Integer getRoleIdFromJwtToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("role_id", Integer.class);
    }

    public Integer getUserIdFromJwtToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("user_id", Integer.class);
    }

}
