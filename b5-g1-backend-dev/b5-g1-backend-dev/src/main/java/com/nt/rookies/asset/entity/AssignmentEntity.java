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
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "assignment")
@Getter
@Setter
public class AssignmentEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "assign_to", nullable = false)
	private UserEntity assignTo;

	@ManyToOne
	@JoinColumn(name = "assign_by", nullable = false)
	private UserEntity assignBy;

	@Column(name = "assigned_Date", nullable = false)
	private LocalDateTime assignDate;

	@Column(name = "note", length = 4000)
	private String note;


	@Column(name = "state")
	@Enumerated(EnumType.STRING)
	private AssignStateEnum state;

	@Column(name = "is_deleted")
	private Boolean isDeleted;

	@ManyToOne
	@JoinColumn(name = "asset_code", nullable = false)
	private AssetEntity asset;

	@OneToOne(mappedBy = "assignment")
	private ReturnRequestEntity request;


	@Override
	public String toString() {
		return "AssignmentEntity [id=" + id + ", assignDate=" + assignDate + ", note=" + note + ", state=" + state
				+ ", isDeleted=" + isDeleted + ", asset=" + asset + "]";
	}

	public enum AssignStateEnum {
		ACCEPTED, WAITING_FOR_ACCEPTANCE, DECLINED, WAITING_FOR_RETURNING;

		public static Optional<AssignStateEnum> check(String val) {
			try {
				return Optional.of(AssignStateEnum.valueOf(val));
			}
			catch (Exception e) {
				e.printStackTrace();
				return Optional.empty();
			}
		}
	}

}
