package com.gary.dao.jdbc;

import java.lang.reflect.Method;
import java.sql.Connection;

import com.gary.dao.TransactionProxy;

public class JdbcTransactionProxy extends TransactionProxy {
	@Override
	protected Object invokeHandler(Object arg0, Method method, Object[] args)
			throws Throwable {
		Method m = target.getClass().getMethod("getDBConn");
		Method mgr = target.getClass().getMethod("getDbConnMgr");
		DBConn dbConn = (DBConn)m.invoke(target);
		Connection connection = dbConn.getConnection();
		DBConnMgr dbMgr = (DBConnMgr)mgr.invoke(target);
		try {
			Object invoke = method.invoke(target, args);
			connection.commit();
			return invoke;
		} catch (Exception e) {
			connection.rollback();
			logger.warn("数据库操作失败,回滚数据!", e);
			return null;
		} finally {
			dbMgr.freeConnection(dataSource, dbConn);
		}
	}
}
