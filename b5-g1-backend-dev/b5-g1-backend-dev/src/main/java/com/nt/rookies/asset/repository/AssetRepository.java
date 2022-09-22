package com.nt.rookies.asset.repository;

import java.util.List;
import java.util.Optional;

import com.nt.rookies.asset.entity.AssetEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AssetRepository extends JpaRepository<AssetEntity, String> {

	@Query("FROM AssetEntity a WHERE a.location = ?1 AND a.isDeleted = false " +
			"AND (a.state = 'AVAILABLE' OR a.state = 'NOT_AVAILABLE' OR a.state = 'ASSIGNED')")
	List<AssetEntity> getAllByLocationAndDefaultState(String location);

	@Query("FROM AssetEntity a WHERE a.location = ?1 AND a.isDeleted = false AND (a.state = 'WAITING_FOR_RECYCLING' OR a.state = 'RECYCLED')")
	List<AssetEntity> getAllByLocationAndNonDefaultState(String location);

	@Query("FROM AssetEntity a WHERE a.location = ?1 AND a.isDeleted = false AND a.assetCode = ?2")
	Optional<AssetEntity> findByAssetCode(String location, String assetCode);

	List<AssetEntity> findByStateEquals(AssetEntity.EState state);

	@Query(value = "select * from asset where category_id = :id and is_deleted = 0", nativeQuery = true)
	List<AssetEntity> getByCategoryID(@Param("id") Integer id);

}
