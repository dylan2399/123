package com.nt.rookies.asset.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.nt.rookies.asset.dto.AssetDTO;
import com.nt.rookies.asset.dto.asset.response.AssignmentDTO;
import com.nt.rookies.asset.dto.asset.response.DetailAssetDTO;
import com.nt.rookies.asset.dto.asset.response.ViewAssetDTO;
import com.nt.rookies.asset.entity.AssetEntity;
import com.nt.rookies.asset.entity.AssignmentEntity;
import com.nt.rookies.asset.entity.CategoryEntity;
import com.nt.rookies.asset.exception.AssetException;
import com.nt.rookies.asset.repository.AssignmentRepository;
import com.nt.rookies.asset.repository.CategoryRepository;
import com.nt.rookies.asset.service.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AssetMapper {

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	ModelMapper modelMapper;

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	AssignmentRepository assignmentRepository;

	public AssetDTO convertToDto(AssetEntity asset) {
		try {
			AssetDTO dto = modelMapper.map(asset, AssetDTO.class);
			CategoryEntity category = categoryRepository.findById(asset.getCategory().getCategoryId())
					.orElseThrow(() -> new AssetException(AssetException.ASSET_CATEGORY_NOT_FOUND));
			if (dto.getCode() != null) {
				List<AssignmentEntity> assigns = assignmentRepository.findHistoricalByAssetCode(dto.getCode());
				if (!assigns.isEmpty()) {
					dto.setAssignments(assigns.stream()
							.map(assignmentEntity ->
									"Assignment " + assignmentEntity.getId() + ": " + assignmentEntity.getAsset().getAssetName())
							.collect(Collectors.toList()));
				}
				else {
					List<String> list = new ArrayList<>();
					dto.setAssignments(list);
				}
			}
			String state = setStateDTO(asset.getState().name());
			dto.setState(state);
			DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			return dto;
		}
		catch (Exception ex) {
			logger.warn(ex.getMessage());
			throw new AssetException(AssetException.ERR_CONVERT_DTO_ENTITY_FAIL);
		}
	}

	public DetailAssetDTO convertToDetailDTO(AssetEntity asset) throws AssetException {
		try {
			DetailAssetDTO detailAssetDTO = modelMapper.map(asset, DetailAssetDTO.class);

			List<AssignmentDTO> filteredAssignmentDTOCollection = detailAssetDTO.getAssignments().stream()
					.filter(e -> !e.getIsDeleted()).collect(Collectors.toList());
			detailAssetDTO.setAssignments(filteredAssignmentDTOCollection);
			String state = setStateDTO(asset.getState().name());
			detailAssetDTO.setState(state);
			return detailAssetDTO;
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new AssetException(AssetException.ERR_CONVERT_DTO_ENTITY_FAIL);
		}
	}

	public ViewAssetDTO convertToViewDto(AssetEntity asset) {
		try {
			ViewAssetDTO dto = modelMapper.map(asset, ViewAssetDTO.class);
			String state = setStateDTO(asset.getState().name());
			dto.setState(state);
			return dto;
		}
		catch (Exception ex) {
			throw new AssetException(AssetException.ERR_CONVERT_DTO_ENTITY_FAIL);
		}
	}

	public AssetEntity convertToEntity(AssetDTO dto) {
		try {
			AssetEntity entity = modelMapper.map(dto, AssetEntity.class);
			DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			entity.setInstallDate(LocalDate.parse(dto.getInstallDate(), formatterDate).atStartOfDay());
			return entity;
		}
		catch (Exception ex) {
			logger.warn(ex.getMessage());
			throw new AssetException(AssetException.ERR_CONVERT_DTO_ENTITY_FAIL);
		}
	}

	public List<ViewAssetDTO> convertToListDTO(List<AssetEntity> assets) throws AssetException {
		try {
			return assets.stream().map(asset -> convertToViewDto(asset))
					.collect(Collectors.toList());
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new AssetException(AssetException.ERR_CONVERT_DTO_ENTITY_FAIL);
		}
	}

	public List<AssetDTO> toListDto(List<AssetEntity> listEntity) {
		List<AssetDTO> listDto = new ArrayList<>();
		listEntity.forEach(e -> {
			listDto.add(this.convertToDto(e));
		});
		return listDto;
	}

	public String setStateDTO(String stateEnum) {
		switch (stateEnum) {
			case "AVAILABLE":
				return "Available";
			case "NOT_AVAILABLE":
				return "Not Available";
			case "ASSIGNED":
				return "Assigned";
			case "WAITING_FOR_RECYCLING":
				return "Waiting for recycling";
			case "RECYCLED":
				return "Recycled";
			default:
				throw new AssetException(AssetException.ASSET_STATE_WRONG);
		}
	}

}
