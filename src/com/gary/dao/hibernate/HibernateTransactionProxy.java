package com.gary.dao.hibernate;

import java.lang.reflect.Method;

import org.hibernate.Transaction;

import com.gary.dao.TransactionProxy;

public class HibernateTransactionProxy extends TransactionProxy {
	@Override
	protected Object invokeHandler(Object arg0, Method method, Object[] args)
			throws Throwable {
		if (SessionFactoryUtil.isHaveSpring(dataSource))
			return method.invoke(target, args);
		else {
			Transaction tx = SessionFactoryUtil.getSessionFactory(dataSource)
					.getCurrentSession().beginTransaction();
			try {
				Object invoke = method.invoke(target, args);
				tx.commit();
				return invoke;
			} catch (Exception e) {
				tx.rollback();
				logger.warn("数据库操作失败,回滚数据!", e);
				return null;
			}
		}
	}
}
