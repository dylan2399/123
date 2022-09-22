package com.nt.rookies.asset.dto;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AssetDTO {

	private String code;

	private String assetName;

	private String state;

	private String categoryName;

	private String specification;

	private String installDate;

	private List<String> assignments;

	private String location;

	private String createdBy;

	//private String createdBy;
}
