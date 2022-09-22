package com.nt.rookies.asset.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.nt.rookies.asset.entity.InvalidToken;

public interface InvalidTokenRepository extends CrudRepository<InvalidToken, Integer> {
  List<InvalidToken> findByToken(String token);

}
