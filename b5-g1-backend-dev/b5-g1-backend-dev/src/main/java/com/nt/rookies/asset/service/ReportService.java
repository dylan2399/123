package com.nt.rookies.asset.service;

import com.nt.rookies.asset.dto.ReportDTO;
import com.nt.rookies.asset.entity.AssetEntity;
import com.nt.rookies.asset.entity.CategoryEntity;
import com.nt.rookies.asset.exception.CategoryException;
import com.nt.rookies.asset.repository.AssetRepository;
import com.nt.rookies.asset.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ReportService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private AssetRepository assetRepository;
    public List<ReportDTO> getReport(){
        List<ReportDTO> reports = new ArrayList<>();
        List<CategoryEntity> categories = categoryRepository.findAll();
        if(!categories.isEmpty()){
            for(CategoryEntity category:categories){
                List<AssetEntity> assets = assetRepository.getByCategoryID(category.getCategoryId());
                int available = 0;
                int notAvailable = 0;
                int assigned = 0;
                int waitingForRecycling = 0;
                int recycling = 0;
                if(!assets.isEmpty()) {
                    for (AssetEntity asset : assets) {
                        if (asset.getState() == AssetEntity.EState.AVAILABLE) {
                            available +=1;
                        }
                        if (asset.getState() == AssetEntity.EState.NOT_AVAILABLE) {
                            notAvailable +=1;
                        }
                        if (asset.getState() == AssetEntity.EState.ASSIGNED) {
                            assigned +=1;
                         }
                        if (asset.getState() == AssetEntity.EState.WAITING_FOR_RECYCLING) {
                            waitingForRecycling +=1;
                        }
                        if (asset.getState() == AssetEntity.EState.RECYCLED) {
                            recycling +=1;
                        }
                    }
                }
                int total = available+notAvailable+assigned+waitingForRecycling+recycling;
                reports.add(new ReportDTO(category.getCategoryName(),total,assigned,available,notAvailable,waitingForRecycling,recycling));

            }
        }
        return reports;
    }
}
