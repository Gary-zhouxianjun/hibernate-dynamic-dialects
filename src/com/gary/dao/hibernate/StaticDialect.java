package com.gary.dao.hibernate;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.dialect.Dialect;
import org.hibernate.service.jdbc.dialect.internal.StandardDialectResolver;

public class StaticDialect extends StandardDialectResolver {

	private static final long serialVersionUID = 1075190080636694399L;
	
	private Map<String, Dialect> dialects = new HashMap<String, Dialect>();
	
	@Override
	protected Dialect resolveDialectInternal(DatabaseMetaData metaData)
			throws SQLException {
		// TODO Auto-generated method stub
		String databaseName = metaData.getDatabaseProductName();
		if(dialects.containsKey(databaseName))
			return dialects.get(databaseName);
		Dialect resolveDialectInternal = super.resolveDialectInternal(metaData);
		dialects.put(databaseName, resolveDialectInternal);
		return resolveDialectInternal;
	}
}
