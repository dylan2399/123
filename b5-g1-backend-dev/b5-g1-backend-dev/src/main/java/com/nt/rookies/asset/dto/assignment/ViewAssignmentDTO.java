package com.nt.rookies.asset.dto.assignment;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ViewAssignmentDTO {

    private Integer id;

    private String assetCode;

    private String assetName;

    private String specification;

    private String assignedTo;

    private String assignedBy;

    private String assignedDate;

    private String state;

    private String note;

    private boolean isReturning;

}
