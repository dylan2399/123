package com.nt.rookies.asset.mapper;


import com.nt.rookies.asset.dto.ReturnRequestDTO;
import com.nt.rookies.asset.dto.ViewReturnRequestDTO;
import com.nt.rookies.asset.entity.ReturnRequestEntity;
import com.nt.rookies.asset.exception.RequestException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class RequestMapper {
	private static final Logger logger = LoggerFactory.getLogger(UserMapper.class);

	@Autowired
	ModelMapper modelMapper;

	public ReturnRequestDTO convertToDto(ReturnRequestEntity request) {
		try {
			ReturnRequestDTO dto = modelMapper.map(request, ReturnRequestDTO.class);
			return dto;
		} catch (Exception ex) {
			logger.warn(ex.getMessage());
			throw new RequestException(RequestException.ERR_CONVERT_DTO_ENTITY_FAIL);
		}

	}

	public ReturnRequestEntity convertToEntity(ReturnRequestDTO dto) {
		try {
			ReturnRequestEntity request = modelMapper.map(dto, ReturnRequestEntity.class);
			return request;
		} catch (Exception ex) {
			logger.warn(ex.getMessage());
			throw new RequestException(RequestException.ERR_CONVERT_DTO_ENTITY_FAIL);
		}
	}

	public List<ReturnRequestDTO> toListDto(List<ReturnRequestEntity> listEntity) {
		List<ReturnRequestDTO> listDto = new ArrayList<>();

		listEntity.forEach(e -> {
			listDto.add(this.convertToDto(e));
		});
		return listDto;
	}

	public ViewReturnRequestDTO convertViewDto(ReturnRequestEntity entity) {
		ViewReturnRequestDTO returnRequest = new ViewReturnRequestDTO();
		returnRequest.setRequestId(entity.getRequestId());
		returnRequest.setState(entity.getState().name());
		return returnRequest;
	}

	public List<ViewReturnRequestDTO> toViewListDto(List<ReturnRequestEntity> listEntity) {
		List<ViewReturnRequestDTO> listDto = new ArrayList<>();

		listEntity.forEach(e -> {
			listDto.add(this.convertViewDto(e));
		});
		return listDto;
	}

	public ViewReturnRequestDTO convertToDTO(ReturnRequestEntity returnRequest) {
		ViewReturnRequestDTO dto = modelMapper.map(returnRequest, ViewReturnRequestDTO.class);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		dto.setRequestId(returnRequest.getRequestId());
		dto.setRequestBy(returnRequest.getRequestBy());
		dto.setAssignDateString(returnRequest.getCreatedDate().format(formatter));
		dto.setState(returnRequest.getState().toString());
		if (Objects.isNull(returnRequest.getAcceptBy())) {
			dto.setAcceptBy(null);
		} else {
			dto.setAcceptBy(returnRequest.getAcceptBy());
		}

		if (returnRequest.getReturnDate() == null) {
			dto.setReturnDate(null);
		} else {
			dto.setReturnDate(returnRequest.getReturnDate().format(formatter));
		}
		return dto;
	}
}
