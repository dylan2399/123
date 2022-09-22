package com.nt.rookies.asset.dto.asset.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DetailAssetDTO {

    private String assetCode;

    private String assetName;

    private String state;

    private LocalDateTime installDate;

    private String location;

    private String specification;

    private String category;

    private List<AssignmentDTO> assignments;

}
