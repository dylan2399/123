package com.nt.rookies.asset.mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.nt.rookies.asset.dto.asset.response.AssignmentDTO;
import com.nt.rookies.asset.dto.assignment.ViewAssignmentDTO;
import com.nt.rookies.asset.entity.AssetEntity;
import com.nt.rookies.asset.entity.AssignmentEntity;
import com.nt.rookies.asset.entity.UserEntity;
import com.nt.rookies.asset.exception.AssetException;
import com.nt.rookies.asset.exception.AssignmentException;
import com.nt.rookies.asset.exception.NotFoundException;
import com.nt.rookies.asset.exception.UserException;
import com.nt.rookies.asset.repository.AssetRepository;
import com.nt.rookies.asset.repository.UserRepository;
import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AssignmentMapper {

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private AssetRepository assetRepository;

	@Autowired
	private UserRepository userRepository;

	public List<AssignmentDTO> convertToListDTO(List<AssignmentEntity> assignments) throws AssetException {
		try {
			List<AssignmentDTO> assignmentDTOList = new ArrayList<>();
			assignments.forEach(assignment -> assignmentDTOList.add(this.convertToDTO(assignment)));
			return assignmentDTOList;
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new AssetException(AssetException.ERR_CONVERT_DTO_ENTITY_FAIL);
		}
	}

	public AssignmentEntity convertToEntity(AssignmentDTO input) {
		AssignmentEntity result = new AssignmentEntity();
		AssetEntity toSaveAsset = assetRepository.findById(input.getAssetCode())
				.orElseThrow(() -> new AssetException(AssetException.ASSET_NOT_FOUND));
		UserEntity toSaveAssignTo = userRepository.findById(input.getAssignTo())
				.orElseThrow(() -> new UserException(UserException.USER_NOT_FOUND));
		UserEntity toSaveAssignBy = userRepository.findByUsername(input.getAssignBy())
				.orElseThrow(() -> new UserException(UserException.USER_NOT_FOUND));
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDateTime toSaveAssignDate = LocalDate.parse(input.getAssignDateString(), formatter).atStartOfDay();
		result.setAsset(toSaveAsset);
		result.setAssignTo(toSaveAssignTo);
		result.setAssignBy(toSaveAssignBy);
		result.setAssignDate(toSaveAssignDate);
		result.setNote(input.getNote());
		result.setCreatedBy(toSaveAssignBy.getStaffCode());
		result.setCreatedDate(LocalDateTime.now());
		result.setState(AssignmentEntity.AssignStateEnum.check(input.getState())
				.orElseThrow(() -> new AssignmentException(AssignmentException.ASSIGN_STATE_WRONG)));
		return result;
	}

	public AssignmentDTO convertToDTO(AssignmentEntity input) {
		AssignmentDTO result = new AssignmentDTO();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		result.setId(input.getId());
		result.setAssetCode(input.getAsset().getAssetCode());
		result.setAssetName(input.getAsset().getAssetName());
		result.setSpecification(input.getAsset().getSpecification());
		result.setAssignTo(input.getAssignTo().getStaffCode());
		result.setAssignBy(input.getAssignBy().getStaffCode());
		result.setAssignToUsername(input.getAssignTo().getUsername());
		result.setAssignByUsername(input.getAssignBy().getUsername());
		result.setAssignDateString(input.getAssignDate().format(formatter));
		result.setState(input.getState().name());
		result.setCreatedDateString(input.getCreatedDate().toString());
		result.setNote(input.getNote());

		return result;
	}

	public ViewAssignmentDTO convertToAssignmentDTO(AssignmentEntity assignment) {

		ViewAssignmentDTO assignmentDTO = modelMapper.map(assignment, ViewAssignmentDTO.class);
		DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		assignmentDTO.setId(assignment.getId());
		assignmentDTO.setAssetCode(String.valueOf(assignment.getAsset().getAssetCode()));
		assignmentDTO.setAssetName(String.valueOf(assignment.getAsset().getAssetName()));
		assignmentDTO.setSpecification(assignment.getAsset().getSpecification());
		assignmentDTO.setAssignedBy(assignment.getAssignBy().getUsername());
		assignmentDTO.setAssignedTo(assignment.getAssignTo().getUsername());
		assignmentDTO.setAssignedDate(assignment.getAssignDate().format(formatterDate));
		String stateformat = assignment.getState().toString().replaceAll("_", " ").toLowerCase();
		assignmentDTO.setState(stateformat.substring(0, 1).toUpperCase() + stateformat.substring(1));
		assignmentDTO.setNote(assignment.getNote());
		assignmentDTO.setReturning(assignment.getIsDeleted());
		return assignmentDTO;
	}

	public List<ViewAssignmentDTO> toDTOList(List<AssignmentEntity> listEntity) throws NotFoundException {
		List<ViewAssignmentDTO> listDTO;
		try {
			listDTO = listEntity.stream().map(this::convertToAssignmentDTO).collect(Collectors.toList());
		}
		catch (Exception ex) {
			throw new NotFoundException("Cannot find assignment entity");
		}
		return listDTO;
	}
}
