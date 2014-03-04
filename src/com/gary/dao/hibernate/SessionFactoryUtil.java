package com.gary.dao.hibernate;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import com.gary.dao.TransactionProxy;
import com.gary.dao.annotation.ClassPathScanHandler;
import com.gary.framework.config.ApplicationContextHolder;

public class SessionFactoryUtil {
	private static TransactionProxy tp = new HibernateTransactionProxy();
	private static boolean haveSpring = true;
	public static boolean enforceNoSpring = false;
	private static Map<String, SessionFactory> sessionFactoryMap = new HashMap<String, SessionFactory>();
	public static String defaultCfg = "hibernate";
	private static String cfgXml = ".cfg.xml";

	public static boolean isHaveSpring(String... dataSource) {
		if (sessionFactory == null)
			getSessionFactory(dataSource);
		return haveSpring ? !enforceNoSpring : haveSpring;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getTransaction(Object dao) {
		return (T) tp.bind(dao);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getTransaction(Object dao, String dataSource) {
		return (T) tp.bind(dao, dataSource);
	}

	@Autowired
	private static SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		SessionFactoryUtil.sessionFactory = sessionFactory;
	}

	public static SessionFactory getSessionFactory(String... dataSource) {
		if (sessionFactory != null && (dataSource == null || dataSource.length < 1) && !enforceNoSpring)
			return sessionFactory;
		try {
			if (!enforceNoSpring)
				sessionFactory = (SessionFactory) ApplicationContextHolder
						.getBean("sessionFactory");
		} catch (Exception e) {
			haveSpring = false;
		}
		if(!haveSpring || enforceNoSpring)
			return createHibernateSessionFactory(dataSource);
		return sessionFactory;
	}

	private static SessionFactory createHibernateSessionFactory(
			String... dataSource) {
		String dataSourceName = defaultCfg;
		if (dataSource != null && dataSource.length > 0
				&& dataSource[0] != null && !"".equals(dataSource[0])) {
			dataSourceName = dataSource[0];
			if (sessionFactoryMap.containsKey(dataSource[0])) {
				return sessionFactoryMap.get(dataSource[0]);
			}
		}
		Configuration configuration = null;
		configuration = new Configuration().configure(dataSourceName + cfgXml);
		Properties properties = configuration.getProperties();
		String mapping = (String) properties.get("mapping");
		if (mapping != null) {
			ClassPathScanHandler cpsh = new ClassPathScanHandler();
			String[] mappings = mapping.split(",");
			for (String string : mappings) {
				Set<Class<?>> classes = cpsh.getPackageAllClasses(string, true);
				for (Class<?> class1 : classes) {
					configuration.addAnnotatedClass(class1);
				}
			}
		}
		ServiceRegistry serviceRegistry = new ServiceRegistryBuilder()
				.applySettings(properties).buildServiceRegistry();
		sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		sessionFactoryMap.put(dataSourceName, sessionFactory);
		return sessionFactory;
	}
}
