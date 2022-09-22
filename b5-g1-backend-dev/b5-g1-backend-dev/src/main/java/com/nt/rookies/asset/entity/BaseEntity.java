package com.nt.rookies.asset.entity;


import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column
	private LocalDateTime createdDate;

	@Column
	private String createdBy;

	@Column
	private LocalDateTime lastUpdatedDate;

	@Column
	private String lastUpdatedBy;
}
