package com.nt.rookies.asset.dto.asset.response;

import com.nt.rookies.asset.dto.UserDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReturnRequestDTO {

    private Long requestId;

    private String requestBy;

    private String acceptBy;

    private LocalDateTime returnDate;

    private String state;

}
