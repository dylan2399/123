package com.nt.rookies.asset.jwt;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class JwtResponse implements Serializable {
  private static final long serialVersionUID = 1L;

  private String username;

  private String jwtToken;

  public JwtResponse(String jwtToken) {
    super();
    this.jwtToken = jwtToken;
  }


}
