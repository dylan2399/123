package com.nt.rookies.asset.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nt.rookies.asset.entity.InvalidToken;
import com.nt.rookies.asset.entity.UserEntity;
import com.nt.rookies.asset.entity.UserEntity.EStatus;
import com.nt.rookies.asset.jwt.JwtRequest;
import com.nt.rookies.asset.jwt.JwtResponse;
import com.nt.rookies.asset.jwt.JwtTokenUtil;
import com.nt.rookies.asset.repository.InvalidTokenRepository;
import com.nt.rookies.asset.service.JwtUserDetailsService;
import com.nt.rookies.asset.service.UserService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class JwtAuthenticationController {
  @Autowired
  private AuthenticationManager authenticationManager;
  @Autowired
  private JwtTokenUtil jwtTokenUtil;
  @Autowired
  private JwtUserDetailsService userDetailsService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private UserService userService;

  @Autowired
  private InvalidTokenRepository invalidTokenRepository;

  @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
  public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest account) throws Exception {
    authenticate(account.getUsername(), account.getPassword());
    final UserDetails userDetails = userDetailsService.loadUserByUsername(account.getUsername());
    final String token = jwtTokenUtil.generateToken(userDetails);
    return new ResponseEntity<>(new JwtResponse(token), HttpStatus.OK);
  }

  @PostMapping("/update-password-first-time")
  public ResponseEntity<JwtResponse> updatePasswordFirstTime(@RequestBody JwtRequest account,
      @RequestHeader(name = "UpdatePasswordFirstTimeToken") String clientToken) {
    UserEntity entity = userService.findByUsername(account.getUsername());

    String newPassword = passwordEncoder.encode(account.getPassword());

    entity.setPassword(newPassword);
    entity.setFirstLogin(false);
    UserEntity updatedEntity = userService.updatePasswordFirstTime(entity);

    final UserDetails userDetails = userDetailsService.loadUserByUsername(updatedEntity.getUsername());

    final String token = jwtTokenUtil.generateToken(userDetails);

    invalidTokenRepository.save(new InvalidToken(clientToken));

    return new ResponseEntity<JwtResponse>(new JwtResponse(token), HttpStatus.OK);
  }

  private void authenticate(String username, String password) throws Exception {
    try {
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
      UserEntity user = userService.findByUsername(username);
      if (user.getStatus() == EStatus.DISABLE) {
	throw new DisabledException("USER_DISABLED");
      }
    } catch (DisabledException e) {
      throw new Exception("USER_DISABLED", e);
    } catch (BadCredentialsException e) {
      throw new Exception("INVALID_CREDENTIALS", e);
    }
  }


}
