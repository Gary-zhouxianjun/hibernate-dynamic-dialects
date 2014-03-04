package com.gary.dao.hibernate.impl;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;

import com.gary.dao.dto.Between;
import com.gary.dao.dto.SqlFieldBean;
import com.gary.dao.dto.SqlOrderBy;
import com.gary.dao.dto.SqlWhere;
import com.gary.dao.hibernate.DataSourceContextHolder;
import com.gary.dao.hibernate.IGenericDAO;
import com.gary.dao.hibernate.SessionFactoryUtil;
import com.gary.dao.result.Page;

public abstract class GenericDAOImpl<T> extends DataSourceContextHolder implements
		IGenericDAO<T> {

	abstract protected Class<T> getEntityClass();

	abstract protected String getDataSource();
	
	Validator validator = Validation
			.buildDefaultValidatorFactory().getValidator();

	public Session getSession() {
		if (sessionFactory != null)
			return sessionFactory.getCurrentSession();
		return SessionFactoryUtil.getSessionFactory(getDataSource()).getCurrentSession();
	}

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public void delete(T t) {
		getSession().delete(t);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(Integer id) {
		return (T) getSession().get(getEntityClass(), id);
	}

	@SuppressWarnings("unchecked")
	public T load(Integer id){
		return (T) getSession().load(getEntityClass(), id);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<T> list() {
		return getSession().createQuery(
				"from " + getEntityClass().getSimpleName()).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> list(Map<SqlWhere, Object> where) {
		StringBuffer hql = new StringBuffer("from ");
		hql.append(getEntityClass().getSimpleName());
		Map<String, Object> params = setWhere(hql, where);
		Query query = getSession().createQuery(hql.toString());
		setParams(query, params);
		return query.list();
	}

	@Override
	public Serializable save(T t) {
		return getSession().save(t);
	}

	@Override
	public void saveOrUpdate(T t){
		getSession().saveOrUpdate(t);
	}
	
	@Override
	public void update(T t) {
		getSession().update(t);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(Map<SqlWhere, Object> where) {
		StringBuffer hql = new StringBuffer("from ");
		hql.append(getEntityClass().getSimpleName());
		Map<String, Object> params = setWhere(hql, where);
		Query query = getSession().createQuery(hql.toString());
		setParams(query, params);
		return (T) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(String hql, Map<String, Object> params) {
		Query query = getSession().createQuery(hql);
		setParams(query, params);
		return (T) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getFirst(String hql, Map<String, Object> params) {
		Query query = getSession().createQuery(hql);
		setParams(query, params);
		return (T) query.setFirstResult(0).setMaxResults(1).uniqueResult();
	}

	public List<?> transformerQuery(String sql, String[] names, Class<?> resultClass){
		SQLQuery query = getSession().createSQLQuery(sql);
		query.setResultTransformer(Transformers.aliasToBean(resultClass)).list();
		for (String name : names) {
			query.addScalar(name);
		}
		return query.list();
	}
	
	public List<?> namedQuery(String name, Map<String, Object> params){
		Query query = getSession().getNamedQuery(name);
		setParams(query, params);
		return query.list();
	}
	
	public List<?> findByEntity(Object entity){
		return getCriteria(entity.getClass()).add(Example.create(entity)).list();
	}
	
	public Criteria getCriteria(){
		return getSession().createCriteria(getEntityClass());
	}
	
	public Criteria getCriteria(Class<?> entity){
		return getSession().createCriteria(entity);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<T> list(String hql, Map<String, Object> params) {
		Query query = getSession().createQuery(hql);
		setParams(query, params);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> list(String hql, Map<String, Object> params, int recordNum) {
		Query query = getSession().createQuery(hql);
		setParams(query, params);
		return query.setFirstResult(0).setMaxResults(recordNum).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Page<T> list(String hql, Map<String, Object> params, int pageSize,
			int page) {
		int count = getCount(hql, params);
		Query query = getSession().createQuery(hql);
		setParams(query, params);
		return (Page<T>) getPage(query, page, pageSize, count);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Page<T> list(Map<SqlWhere, Object> where, int pageSize, int page) {
		StringBuffer hql = new StringBuffer("from ");
		hql.append(getEntityClass().getSimpleName());
		Map<String, Object> params = setWhere(hql, where);
		Query query = getSession().createQuery(hql.toString());
		setParams(query, params);
		int count = getCount(hql.toString(), params);
		return (Page<T>) getPage(query, page, pageSize, count);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Page<T> list(Map<SqlWhere, Object> where, int pageSize, int page,
			SqlOrderBy[] order) {
		StringBuffer hql = new StringBuffer("from ");
		hql.append(getEntityClass().getSimpleName());
		Map<String, Object> params = setWhere(hql, where);
		order(hql, order);
		Query query = getSession().createQuery(hql.toString());
		setParams(query, params);
		int count = getCount(hql.toString(), params);
		return (Page<T>) getPage(query, page, pageSize, count);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Page<T> list(int pageSize, int page) {
		StringBuffer hql = new StringBuffer("from ");
		hql.append(getEntityClass().getSimpleName());
		int count = getCount(hql.toString());
		Query query = getSession().createQuery(hql.toString());
		return (Page<T>) getPage(query, page, pageSize, count);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> list(Map<SqlWhere, Object> where, SqlOrderBy[] order) {
		StringBuffer hql = new StringBuffer("from ");
		hql.append(getEntityClass().getSimpleName());
		Map<String, Object> params = setWhere(hql, where);
		order(hql, order);
		Query query = getSession().createQuery(hql.toString());
		setParams(query, params);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> list(SqlOrderBy[] order) {
		StringBuffer hql = new StringBuffer("from ");
		hql.append(getEntityClass().getSimpleName());
		order(hql, order);
		Query query = getSession().createQuery(hql.toString());
		return query.list();
	}

	@Override
	public Page<?> list(String hql, Map<String, Object> params, int pageSize,
			int page, SqlOrderBy[] order) {
		int count = getCount(hql, params);
		hql = order(new StringBuffer(hql), order);
		Query query = getSession().createQuery(hql);
		setParams(query, params);
		return getPage(query, page, pageSize, count);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> list(String hql, Map<String, Object> params, int recordNum,
			SqlOrderBy[] order) {
		hql = order(new StringBuffer(hql), order);
		Query query = getSession().createQuery(hql);
		setParams(query, params);
		return query.setFirstResult(0).setMaxResults(recordNum).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> list(String hql, Map<String, Object> params,
			SqlOrderBy[] order) {
		hql = order(new StringBuffer(hql), order);
		Query query = getSession().createQuery(hql);
		setParams(query, params);
		return query.list();
	}

	private void setParams(Query query, Map<String, Object> params) {
		if (params == null)
			return;
		for (String key : params.keySet()) {
			query.setParameter(key, params.get(key));
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Page<?> getPage(Query query, int page, int pageSize, int count) {
		query.setFirstResult((page - 1) * pageSize);
		query.setMaxResults(pageSize);
		Page p = new Page();
		p.setCount(count);
		p.setItems(query.list());
		p.setPageSize(pageSize);
		p.setPageNum(page);
		return p;
	}

	private String order(StringBuffer hql, SqlOrderBy[] order) {
		if (order != null && order.length > 0) {
			hql.append(" order by ");
			for (SqlOrderBy o : order) {
				hql.append(o.getField()).append(" ").append(o.getOrder())
						.append(",");
			}
			hql.deleteCharAt(hql.length() - 1);
		}
		return hql.toString();
	}

	@SuppressWarnings("unused")
	private String where(StringBuffer hql, Map<SqlWhere, Object> where) {
		if (where == null)
			return hql.toString();
		hql.append(" where ");
		for (SqlWhere key : where.keySet()) {
			if (!hql.toString().endsWith("where "))
				hql.append(key.getWay());
			hql.append(key.getField());
			Object val = where.get(key);
			if (val instanceof String) {
				if (SqlFieldBean.LIKE.equals(key.getCompare()))
					hql.append(" like '").append(val).append("'");
				else if (SqlFieldBean.IN.equals(key.getCompare()))
					hql.append(" in(").append(val).append(")");
				else if (SqlFieldBean.NOT_IN.equals(key.getCompare()))
					hql.append(" not in(").append(val).append(")");
				else
					hql.append(key.getCompare()).append("'").append(val)
							.append("'");
			} else if(val instanceof Between){
				Between between = (Between)val;
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(between.getFormat());
				hql.append(" BETWEEN ").append(simpleDateFormat.format(between.getStart())).append(" and ").append(simpleDateFormat.format(between.getEnd()));
			} else {
				hql.append(key.getCompare()).append(val);
			}
		}
		return hql.toString();
	}
	
	private Map<String, Object> setWhere(StringBuffer hql, Map<SqlWhere, Object> where) {
		if (where == null)
			return null;
		Map<String, Object> params = new HashMap<String, Object>();
		hql.append(" where ");
		for (SqlWhere key : where.keySet()) {
			if (!hql.toString().endsWith("where "))
				hql.append(key.getWay());
			hql.append(key.getField());
			Object val = where.get(key);
			if (val instanceof String) {
				if (SqlFieldBean.LIKE.equals(key.getCompare())){
					hql.append(" like :").append(key.getField());
				}else if (SqlFieldBean.IN.equals(key.getCompare()))
					hql.append(" in(:").append(val).append(")");
				else if (SqlFieldBean.NOT_IN.equals(key.getCompare()))
					hql.append(" not in(:").append(val).append(")");
				else
					hql.append(key.getCompare()).append(":").append(key.getField());
			} else if(val instanceof Between){
				Between between = (Between)val;
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(between.getFormat());
				hql.append(" BETWEEN ").append(":startDate and :endDate");
				params.put("startDate", simpleDateFormat.format(between.getStart()));
				params.put("endDate", simpleDateFormat.format(between.getEnd()));
				continue;
			} else {
				hql.append(key.getCompare()).append(":").append(key.getField());
			}
			params.put(key.getField(), val);
		}
		return params;
	}

	private String getRowCountHql(String hqlBuilder) {
		String hql = hqlBuilder;

		int fromIndex = hql.toLowerCase().indexOf("from");
		String projectionHql = hql.substring(0, fromIndex);

		hql = hql.substring(fromIndex);
		String rowCountHql = hql.replace("fetch", "");

		int index = rowCountHql.indexOf("order by");
		if (index > 0) {
			rowCountHql = rowCountHql.substring(0, index);
		}
		if (projectionHql.indexOf("select") == -1) {
			return "select count(*) " + rowCountHql;
		} else {
			return projectionHql.replace("select", "select count(") + ") "
					+ rowCountHql;
		}
	}

	public int getCount(String hql, Map<String, Object> params) {
		Query query = getSession().createQuery(getRowCountHql(hql));
		setParams(query, params);
		return ((Number) query.iterate().next()).intValue();
	}

	public int getCount(String hql) {
		Query query = getSession().createQuery(getRowCountHql(hql));
		return ((Number) query.iterate().next()).intValue();
	}

	public Set<ConstraintViolation<T>> validator(T t) {
		return validator.validate(t, getEntityClass());
	}
	
	public <D> D transcation(){
		return SessionFactoryUtil.getTransaction(this, getDataSource());
	}
	
	private String toLowerCase(String name){
		return name.substring(0, 1).toLowerCase() + name.substring(1);
	}
}
