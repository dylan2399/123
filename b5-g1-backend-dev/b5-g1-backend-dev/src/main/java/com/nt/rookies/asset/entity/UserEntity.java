package com.nt.rookies.asset.entity;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "user", uniqueConstraints = { @UniqueConstraint(columnNames = "username") })
@Getter
@Setter
@ToString
public class UserEntity extends BaseEntity {

  @Id
  @Column(name = "staff_code")
  @GeneratedValue(generator = "staff-code-generator")
  @GenericGenerator(name = "staff-code-generator",
          parameters = @Parameter(name = "prefix", value = "SD"), strategy = "com.nt.rookies.asset.util.CodeGenerator")
  private String staffCode;

  @Column(name = "username")
  private String username;

  @Column(name = "password")
  private String password;

  @Column(name = "type", length = 50)
  private String type;

  @Column(name = "gender", length = 50)
  private String gender;

  @Column(name = "joined_date")
  private LocalDate joinedDate;

  @Column(name = "dob")
  private LocalDate dob;

  @Column(name = "fname", length = 50)
  private String firstName;

  @Column(name = "lname", length = 50)
  private String lastName;

  @Column(name = "location")
  private String location;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 50)
  private EStatus status;

  @Column(name = "is_first_login")
  private Boolean firstLogin;

  @OneToMany(mappedBy = "assignTo", fetch = FetchType.LAZY)
  private List<AssignmentEntity> assignToList;

  @OneToMany(mappedBy = "assignBy", fetch = FetchType.LAZY)
  private List<AssignmentEntity> assignByList;

  public enum EType {
    ADMIN, STAFF
  }

	public enum EStatus {
		ENABLE,
		DISABLE
	}
}
