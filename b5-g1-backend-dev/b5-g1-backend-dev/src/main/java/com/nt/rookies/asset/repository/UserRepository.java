package com.nt.rookies.asset.repository;

import java.util.List;
import java.util.Optional;

import com.nt.rookies.asset.entity.UserEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<UserEntity, String> {

	Optional<UserEntity> findByUsername(String username);

	boolean existsByUsername(String username);

	@Query(value = "select username\n" +
			" from user\n" +
			" where username like :username% \n" +
			" order by staff_code DESC\n" +
			" LIMIT 1",
			nativeQuery = true)
	String findLastUsername(@Param("username") String username);

	List<UserEntity> findAllByStatus(UserEntity.EStatus status);

	List<UserEntity> findAllByStatusAndType(String status, String type);

	@Query(value = "select *\n" +
			" from user\n" +
			" where status = 'ENABLE' and username like %:searchKey% or staff_code like %:searchKey% \n",
			nativeQuery = true)
	List<UserEntity> findUserBySearchKey(@Param("searchKey") String searchKey);

	@Query(value = "select *\n" +
			" from user\n" +
			" where status = 'ENABLE' and type = :filter and username like %:searchKey% or staff_code like %:searchKey% \n",
			nativeQuery = true)
	List<UserEntity> findUserBySearchKeyAndFilter(@Param("searchKey") String searchKey, @Param("filter") String filter);

	@Query(value = "select * from user where status = 'ENABLE' and location = :location and staff_code <> :staffCode",nativeQuery = true)
	List<UserEntity> findByLocation(@Param("location") String location,@Param("staffCode") String staffCode);

	@Query(value = "select * from user where status = 'ENABLE' and location = :location " +
			"and staff_code NOT IN (:staffCode, :newUserCode)"
			, nativeQuery = true)
	List<UserEntity> findAllWithNewUserTop(@Param("location") String location,
			@Param("staffCode") String staffCode, @Param("newUserCode") String newUserCode);

	Optional<UserEntity> findFirstByLocationAndCreatedByOrderByCreatedDateDesc(String location, String createdBy);

	Optional<UserEntity> findFirstByLocationAndLastUpdatedByOrderByLastUpdatedDateDesc(String location, String createdBy);
}
