package com.nt.rookies.asset.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CheckPasswordDTO {

    private String username;

    private String oldPassword;
}
