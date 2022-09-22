package com.nt.rookies.asset.jwt;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nt.rookies.asset.repository.InvalidTokenRepository;
import com.nt.rookies.asset.service.JwtUserDetailsService;

import io.jsonwebtoken.ExpiredJwtException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

  @Autowired
  private JwtUserDetailsService jwtUserDetailsService;

  @Autowired
  private JwtTokenUtil jwtTokenUtil;
  @Autowired
  private InvalidTokenRepository invalidTokenRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {

    final String requestTokenHeader = request.getHeader("Authorization");
    String username = null;
    String jwtToken = null;

    // JWT Token is in the form "Bearer token". Remove Bearer word and get
    // only the Token
    if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
      jwtToken = requestTokenHeader.substring(7);
      try {
	username = jwtTokenUtil.getUsernameFromToken(jwtToken);
      } catch (IllegalArgumentException e) {
	logger.warn("Unable to get JWT Token");
	throw e;
      } catch (ExpiredJwtException e) {
	logger.warn("JWT Token has expired");
	throw e;
      }
    } else {
      logger.warn("JWT Token does not begin with Bearer String");
    }
    // Once we get the token validate it.
    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

      UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);

      // if token is valid configure Spring Security to manually set
      // authentication
      List<String> invalidTokens = invalidTokenRepository.findByToken(jwtToken).stream().map(x -> x.getToken())
	  .collect(Collectors.toList());
      if (jwtTokenUtil.validateToken(jwtToken, userDetails) && !invalidTokens.contains(jwtToken)) {

	UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
	    userDetails, null, userDetails.getAuthorities());
	usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	// After setting the Authentication in the context, we specify
	// that the current user is authenticated.
	// So it passes the Spring Security Configurations successfully.
	SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      }
    }

    chain.doFilter(request, response);
  }

}
