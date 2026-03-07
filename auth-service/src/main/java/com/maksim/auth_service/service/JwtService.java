package com.maksim.auth_service.service;

import com.maksim.auth_service.dto.ValidateResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {
    @Value("${auth.jwt.secret}")
    private String SECRET;
    @Value("${auth.jwt.expiration}")
    private int JWT_EXPIRATION_MINUTES;

    public ValidateResponse validate(String token) {

        try {
            var claims = Jwts.parserBuilder().setSigningKey(getSignKey()).build()
                    .parseClaimsJws(token).getBody();
            var resp = ValidateResponse.builder()
                    .id(Integer.valueOf(claims.getSubject()))
                    .handle(claims.get("handle", String.class))
                    .build();
            if (resp.getHandle() != null) return resp;
        } catch (Exception ex) {
//            ex.printStackTrace();
        }
        return null;
    }


    public String generateToken(String handle, int userId) {
        return Jwts.builder()
                .claim("handle", handle)
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + (long) JWT_EXPIRATION_MINUTES * 60 * 1000))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }


    private Key getSignKey() {
        var bytes = SECRET.getBytes();
        return Keys.hmacShaKeyFor(bytes);
    }

}
