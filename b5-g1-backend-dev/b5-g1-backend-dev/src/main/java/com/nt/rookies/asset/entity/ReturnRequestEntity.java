package com.nt.rookies.asset.entity;


import java.time.LocalDateTime;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "return_request")
@Getter
@Setter
public class ReturnRequestEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "request_id")
	private Integer requestId;

	@Column(name = "request_by")
	private String requestBy;

	@Column(name = "accept_by")
	private String acceptBy;

	@Column(name = "return_date")
	private LocalDateTime returnDate;

	@Column(name = "state")
	@Enumerated(EnumType.STRING)
	private ReturnState state;

	@OneToOne
	@JoinColumn(name = "assignment_id", nullable = false)
	private AssignmentEntity assignment;

	public enum ReturnState{
		COMPLETED, WAITING_FOR_RETURNING, CANCEL;
		public static Optional<ReturnState> check(String val) {
			try {
				return Optional.of(ReturnState.valueOf(val));
			}
			catch (Exception e) {
				e.printStackTrace();
				return Optional.empty();
			}
		}
	}



}
