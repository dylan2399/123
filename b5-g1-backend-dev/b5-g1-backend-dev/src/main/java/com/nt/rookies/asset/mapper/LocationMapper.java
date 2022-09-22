package com.nt.rookies.asset.mapper;

import com.nt.rookies.asset.entity.UserEntity;
import com.nt.rookies.asset.exception.UserException;
import com.nt.rookies.asset.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {
    @Autowired
    private UserRepository userRepository;

    public String getLocationFromUsername(String username) throws UserException {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserException(UserException.USER_NOT_FOUND));

        return userEntity.getLocation();
    }

}
