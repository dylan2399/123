package com.nt.rookies.asset.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;



@Data
@Getter
@Setter
public class DisableUserDTO {
    private String staffCode;
    private String status;
    private String firstName;
    private String lastName;
    private String dob;
    private String joinedDate;
    private String gender;
    private String type;
}
