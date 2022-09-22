package com.nt.rookies.asset.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class ReportDTO {
    private String categoryName;
    private int total;
    private int assigned;
    private int available;
    private int notAvailable;
    private int waitingForRecycling;
    private int recycling;
}
