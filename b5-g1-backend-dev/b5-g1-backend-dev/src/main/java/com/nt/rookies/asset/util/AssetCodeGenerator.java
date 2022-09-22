package com.nt.rookies.asset.util;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;

import com.nt.rookies.asset.entity.AssetEntity;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;


public class AssetCodeGenerator implements IdentifierGenerator {

	private Integer maxId = 0;

	@Override
	public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
		IdentifierGenerator.super.configure(type, params, serviceRegistry);
	}

	@Override
	public Serializable generate(SharedSessionContractImplementor session, Object obj)
			throws HibernateException {

		AssetEntity asset = (AssetEntity) obj;
		String query = "from AssetEntity a where a.assetCode like '" +
				asset.getCategory().getPrefix() + "%' order by a.assetCode desc";
		List<AssetEntity> list = session.createQuery(query).setMaxResults(1).getResultList();

		Integer maxId = list.stream().map(a -> String.valueOf(a.getAssetCode())
						.replace(asset.getCategory().getPrefix(), ""))
				.mapToInt(a -> Integer.parseInt(a)).max().orElse(0);

		return asset.getCategory().getPrefix() +  String.format("%06d", (maxId+1));
	}
}
