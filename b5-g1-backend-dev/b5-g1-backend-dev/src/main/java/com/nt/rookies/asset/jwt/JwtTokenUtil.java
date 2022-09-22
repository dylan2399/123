package com.nt.rookies.asset.jwt;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.nt.rookies.asset.entity.UserEntity;
import com.nt.rookies.asset.service.UserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil implements Serializable {
  private static final long serialVersionUID = 1L;

  //public static final long JWT_TOKEN_VALIDITY = 60 * 60 * 1000; // 5 * 60 * 60 * 1000

  @Value("${app.jwtSecret}")
  private String secret;

  @Value("${app.jwtExpirationMs}")
  private long expiredTime;

  @Autowired
  private UserService userService;

  /**
   * Retrieve username from jwt token
   *
   * @param token
   * @return
   */
  public String getUsernameFromToken(String token) {
    return getClaimFromToken(token, Claims::getSubject);
  }

  /**
   * Retrieve expiration date from jwt token
   *
   * @param token
   * @return
   */
  public Date getExpirationDateFromToken(String token) {
    return getClaimFromToken(token, Claims::getExpiration);
  }

  /**
   * Claims from token
   *
   * @param token
   * @param claimsResolver
   * @return
   */
  public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = getAllClaimsFromToken(token);
    return claimsResolver.apply(claims);
  }

  /**
   * Validate the token
   *
   * @param token
   * @param userDetails
   * @return
   */
  public Boolean validateToken(String token, UserDetails userDetails) {
    final String username = getUsernameFromToken(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }

  public String generateToken(UserDetails userDetails) {
    UserEntity userEntity = userService.findByUsername(userDetails.getUsername());

    Map<String, Object> claims = new HashMap<>();
    claims.put("org", "nashtech");
    claims.put("role", userDetails.getAuthorities());
    claims.put("firstTimeLogin", userEntity.getFirstLogin());
    claims.put("adminLocation", userEntity.getLocation());
    claims.put("staffCode", userEntity.getStaffCode());
    claims.put("username",userEntity.getUsername());
    return generateToken(claims, userDetails.getUsername());
  }

  /**
   * for retrieving any information from token we will need the secret key
   *
   * @param token
   * @return
   */
  private Claims getAllClaimsFromToken(String token) {
    return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
  }

  /**
   * Check if the token has expired
   *
   * @param token
   * @return
   */
  public Boolean isTokenExpired(String token) {
    final Date expiration = getExpirationDateFromToken(token);
    return expiration.before(new Date());
  }

  /**
   * Generate the token 
   * 1. Define claims of the token, like Issuer, Expiration, Subject, and the ID 
   * 2. Sign the JWT using the HS512 algorithm and secret key. 
   * 3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
   *
   * @param claims
   * @param subject
   * @return
   */
  private String generateToken(Map<String, Object> claims, String subject) {
    return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
	.setExpiration(new Date(System.currentTimeMillis() + expiredTime)).signWith(SignatureAlgorithm.HS512, secret)
	.compact();
  }
}
