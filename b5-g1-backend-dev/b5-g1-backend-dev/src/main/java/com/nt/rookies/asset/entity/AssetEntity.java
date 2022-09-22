package com.nt.rookies.asset.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "asset")
@Getter
@Setter
@ToString
public class AssetEntity extends BaseEntity {
  @Id
  @Column(name = "code")
  @GeneratedValue(generator = "asset-code-generator")
  @GenericGenerator(name = "asset-code-generator", strategy = "com.nt.rookies.asset.util.AssetCodeGenerator")
  private String assetCode;

  @Column(name = "name", length = 50)
  private String assetName;

  @Column(name = "install_date")
  private LocalDateTime installDate;

  @Column(name = "location", length = 50)
  private String location;

  @Column(name = "specification", length = 4000)
  private String specification;

  @Column(name = "is_deleted")
  private Boolean isDeleted;

  @ManyToOne
  @JoinColumn(name = "category_id")
  private CategoryEntity category;

  @OneToMany(mappedBy = "asset", fetch = FetchType.LAZY)
  private List<AssignmentEntity> assignments;

  @Enumerated(EnumType.STRING)
  @Column(name = "state", length = 50)
  private EState state;

  public enum EState {
    AVAILABLE, NOT_AVAILABLE, ASSIGNED, WAITING_FOR_RECYCLING, RECYCLED;

		public static Optional<EState> check(String val) {
			try {
				return Optional.of(EState.valueOf(val));
			}
			catch (Exception e) {
				e.printStackTrace();
				return Optional.empty();
			}
		}
	}
  
  @Override
  public String toString() {
    return "AssetEntity [assetCode=" + assetCode + ", assetName=" + assetName + ", installDate=" + installDate
	+ ", location=" + location + ", specification=" + specification + ", isDeleted=" + isDeleted + ", state="
	+ state + "]";
  }

}
