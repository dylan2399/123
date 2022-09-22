package com.nt.rookies.asset.util;

import java.io.Serializable;
import java.util.Properties;
import java.util.stream.Stream;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;


public class CodeGenerator implements IdentifierGenerator {

	private String prefix;

	@Override
	public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
		IdentifierGenerator.super.configure(type, params, serviceRegistry);
		prefix = params.getProperty("prefix");
	}

	@Override
	public Serializable generate(SharedSessionContractImplementor session, Object obj)
			throws HibernateException {
		String query = String.format("select %s from %s",
				session.getEntityPersister(obj.getClass().getName(), obj)
						.getIdentifierPropertyName(),
				obj.getClass().getSimpleName());
		Stream ids = session.createQuery(query).stream();

		Integer max = ids.map(id -> String.valueOf(id).replace(prefix, ""))
				.mapToInt(value -> Integer.parseInt(String.valueOf(value))).max().orElse(0);

//		String max = (String) ids.map(id -> String.valueOf(id).replace(prefix,""))
//				.max(Comparator.comparing(String::valueOf)).orElse("0000");
//		if(max.equals("0000")){
//			return prefix+"0001";
//		}
//		for(char c : max.toCharArray()){
//			if(c != 0){
//				break;
//			}
//			prefix = prefix + c;
//		}
		return prefix + String.format("%04d", (max + 1));
	}

}
