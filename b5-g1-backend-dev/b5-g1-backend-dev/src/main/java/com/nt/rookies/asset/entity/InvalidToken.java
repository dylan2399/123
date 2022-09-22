package com.nt.rookies.asset.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "invalidtokens")
@Getter
@Setter
@ToString
public class InvalidToken {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  @Column(name = "token", columnDefinition = "TEXT")
  private String token;

  public InvalidToken(String token) {
    super();
    this.token = token;
  }

}
