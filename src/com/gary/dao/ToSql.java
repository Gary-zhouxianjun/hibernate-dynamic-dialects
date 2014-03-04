package com.gary.dao;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Map;

import com.gary.dao.dto.SqlBean;
import com.gary.dao.dto.SqlFieldBean;
import com.gary.dao.dto.SqlOrderBy;
import com.gary.dao.dto.SqlWhere;

public class ToSql {
	@SuppressWarnings("unchecked")
	private static <T> T getObject(Object o){
		return (T) o;
	}
	private static int getMethodFirstUpperCase(String name){
		for(int i = 0;i < name.length();i++){   
		    Character c = name.charAt(i);
		    if(Character.isUpperCase(c)){
		    	return i;
		    }
		}
		return 0;
	}
	private static void formatInsert(StringBuffer sql,Method method,String table){
		sql.append(table).append(".");
    	SqlFieldBean sqlfieldbean = method.getAnnotation(SqlFieldBean.class);  
    	if("".equals(sqlfieldbean.field()))
			sql.append(method.getName().substring(3,method.getName().length()));
		else
			sql.append(sqlfieldbean.field());
    	sql.append(",");
	}
	private static void xx(StringBuffer sql,Method method,String table){
		SqlFieldBean sqlfieldbean = method.getAnnotation(SqlFieldBean.class); 
		sql.append(table).append(".");
    	if("".equals(sqlfieldbean.field())){
			sql.append(method.getName().substring(getMethodFirstUpperCase(method.getName()),method.getName().length()));
    	}else
			sql.append(sqlfieldbean.field());
    	if(sqlfieldbean.compare().equals(SqlFieldBean.LIKE_AFTER) || sqlfieldbean.compare().equals(SqlFieldBean.LIKE_BEFORE) || sqlfieldbean.compare().equals(SqlFieldBean.LIKE_ALL))
    		sql.append(" like ");
    	else
    		sql.append(sqlfieldbean.compare());
	}
	private static void xx1(StringBuffer sql,Method method,String table){
		if(sql.indexOf("set") > 0)
			sql.append(" ").append(table).append(".");
		else
			sql.append(" set ").append(table).append(".");
    	SqlFieldBean sqlfieldbean = method.getAnnotation(SqlFieldBean.class);  
    	if("".equals(sqlfieldbean.field()))
			sql.append(method.getName().substring(3,method.getName().length()));
		else
			sql.append(sqlfieldbean.field());
    	sql.append(" = ");
	}
	private static void xx2(StringBuffer sql,Method method,String table){
		SqlFieldBean sqlfieldbean = method.getAnnotation(SqlFieldBean.class);  
		sql.append(table).append(".");
    	if("".equals(sqlfieldbean.field())){
			sql.append(method.getName().substring(getMethodFirstUpperCase(method.getName()),method.getName().length()));
    	}else
			sql.append(sqlfieldbean.field());
    	sql.append(" in(");
	}
	/**
	 * 生成 增加 sql语句
	 * @param obj 实体BEAN
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static String toInsertSql(Object obj) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		String table = null;
		Method[] methods = null;
		try {
			Class<?> c = Class.forName(obj.getClass().getName());
			methods = c.getDeclaredMethods();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		StringBuffer sql = new StringBuffer("insert into ");
		StringBuffer vals = new StringBuffer("values(");
		boolean hasSqlBean = obj.getClass().isAnnotationPresent(SqlBean.class); 
		if(!hasSqlBean)
			return null;
		SqlBean sqlbean = obj.getClass().getAnnotation(SqlBean.class);
		if("".equals(sqlbean.table()))
			table = obj.getClass().getSimpleName();
		else
			table = sqlbean.table();
		if(table == null)
			return null;
		sql.append(table).append("(");
		for (Method method : methods) {
			boolean hasAnnotation = method.isAnnotationPresent(SqlFieldBean.class);   
            if (hasAnnotation) {
            	Object re = method.invoke(obj);
				if(re != null){
	            	Class<?> cs = method.getReturnType();
					if (cs.equals(String.class) || cs.equals(Character.class)) {
						formatInsert(sql, method, table);
						vals.append("'").append(re).append("',");
					}else if(cs.equals(Boolean.class)){
						formatInsert(sql, method, table);
						Boolean r = (Boolean)re;
						vals.append(r ? 1 : 0).append(",");
					}else if(cs.equals(Date.class)){
						boolean hasDate = method.isAnnotationPresent(SqlFieldBean.Date.class); 
						if(hasDate){
							formatInsert(sql, method, table);
							vals.append("'").append(new SimpleDateFormat(method.getAnnotation(SqlFieldBean.Date.class).value()).format((Date)re)).append("',");
						}
					}else if(cs.equals(java.util.Date.class)){
						boolean hasDate = method.isAnnotationPresent(SqlFieldBean.Date.class); 
						if(hasDate){
							formatInsert(sql, method, table);
							vals.append("'").append(new SimpleDateFormat(method.getAnnotation(SqlFieldBean.Date.class).value()).format((java.util.Date)re)).append("',");
						}
					}else if(cs.equals(Long.class) || cs.equals(Double.class) || cs.equals(Float.class) || cs.equals(Integer.class) || cs.equals(BigDecimal.class) || cs.equals(Short.class)){
						formatInsert(sql, method, table);
						vals.append(re).append(",");
					}
				}
             }   
		}
		sql = sql.toString().endsWith(",") ? sql.deleteCharAt(sql.length() - 1).append(")") : sql.append(")");
		vals = vals.toString().endsWith(",") ? vals.deleteCharAt(vals.length() - 1).append(")") : vals.append(")");
		return sql.append(vals) + ";";
	}
	/**
	 * 生成 删除 sql语句
	 * @param obj 实体BEAN
	 * @param where 如没有则null
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static String toDeleteSql(Object obj, Map<SqlWhere, Object> where) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		boolean isWhere = false;
		String table = null;
		Method[] methods = null;
		try {
			Class<?> c = Class.forName(obj.getClass().getName());
			methods = c.getDeclaredMethods();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		StringBuffer sql = new StringBuffer("delete from ");
		boolean hasSqlBean = obj.getClass().isAnnotationPresent(SqlBean.class); 
		if(!hasSqlBean)
			return null;
		SqlBean sqlbean = obj.getClass().getAnnotation(SqlBean.class);
		if("".equals(sqlbean.table()))
			table = obj.getClass().getSimpleName();
		else
			table = sqlbean.table();
		if(table == null)
			return null;
		sql.append(table);
		for (Method method : methods) {
			boolean hasAnnotation = method.isAnnotationPresent(SqlFieldBean.class);   
            if (hasAnnotation) {
            	Object re = method.invoke(obj);
				if(re != null){
					String compare = method.getAnnotation(SqlFieldBean.class).compare();
					if (!isWhere) {
						sql.append(" where ");
						isWhere = true;
					}
	            	Class<?> cs = method.getReturnType();
	            	if (cs.equals(String.class) || cs.equals(Character.class)) {
						xx(sql, method,table);
						if(compare.equals(SqlFieldBean.LIKE_ALL))
							sql.append("'%").append(re).append("%' and ");
						else if(compare.equals(SqlFieldBean.LIKE_AFTER))
							sql.append("'").append(re).append("%' and ");
						else if(compare.equals(SqlFieldBean.LIKE_BEFORE))
							sql.append("'%").append(re).append("' and ");
						else
							sql.append("'").append(re).append("' and ");
					}else if(cs.equals(Boolean.class)){
						xx(sql, method,table);
						Boolean r = (Boolean)re;
						sql.append(r ? 1 : 0).append(" and ");
					}else if(cs.equals(Date.class)){
						boolean hasDate = method.isAnnotationPresent(SqlFieldBean.Date.class); 
						if(hasDate){
							xx(sql, method,table);
							if(compare.equals(SqlFieldBean.LIKE_ALL))
								sql.append("'%").append(new SimpleDateFormat(method.getAnnotation(SqlFieldBean.Date.class).value()).format((Date)re)).append("%' and ");
							else if(compare.equals(SqlFieldBean.LIKE_AFTER))
								sql.append("'").append(new SimpleDateFormat(method.getAnnotation(SqlFieldBean.Date.class).value()).format((Date)re)).append("%' and ");
							else if(compare.equals(SqlFieldBean.LIKE_BEFORE))
								sql.append("'%").append(new SimpleDateFormat(method.getAnnotation(SqlFieldBean.Date.class).value()).format((Date)re)).append("' and ");
							else
								sql.append("'").append(new SimpleDateFormat(method.getAnnotation(SqlFieldBean.Date.class).value()).format((Date)re)).append("' and ");
						}
						
					}else if(cs.equals(java.util.Date.class)){
						boolean hasDate = method.isAnnotationPresent(SqlFieldBean.Date.class); 
						if(hasDate){
							xx(sql, method,table);
							if(compare.equals(SqlFieldBean.LIKE_ALL))
								sql.append("'%").append(new SimpleDateFormat(method.getAnnotation(SqlFieldBean.Date.class).value()).format((java.util.Date)re)).append("%' and ");
							else if(compare.equals(SqlFieldBean.LIKE_AFTER))
								sql.append("'").append(new SimpleDateFormat(method.getAnnotation(SqlFieldBean.Date.class).value()).format((java.util.Date)re)).append("%' and ");
							else if(compare.equals(SqlFieldBean.LIKE_BEFORE))
								sql.append("'%").append(new SimpleDateFormat(method.getAnnotation(SqlFieldBean.Date.class).value()).format((java.util.Date)re)).append("' and ");
							else
								sql.append("'").append(new SimpleDateFormat(method.getAnnotation(SqlFieldBean.Date.class).value()).format((java.util.Date)re)).append("' and ");
						}
					}else if(cs.equals(Long.class) || cs.equals(Double.class) || cs.equals(Float.class) || cs.equals(Integer.class) || cs.equals(BigDecimal.class) || cs.equals(Short.class)){
						xx(sql, method,table);
						sql.append(re).append(" and ");
					}else{
						boolean hasSet = method.isAnnotationPresent(SqlFieldBean.Set.class); 
						Collection<?> set = getObject(re);
						if(hasSet && set.size() > 0){
							SqlFieldBean.Set sqlset = method.getAnnotation(SqlFieldBean.Set.class);
							if(String.class.equals(sqlset.value()) || sqlset.value().equals(Character.class)){
								xx2(sql, method,table);
								for (Object object : set) {
									sql.append("'").append(object).append("',");
								}
								sql.deleteCharAt(sql.length() - 1).append(") and ");
							}else if(sqlset.value().equals(Boolean.class)){
								xx2(sql, method,table);
								for (Object object : set) {
									Boolean r = (Boolean)object;
									sql.append(r ? 1 : 0).append(",");
								}
								sql.deleteCharAt(sql.length() - 1).append(") and ");
							}else if(sqlset.value().equals(Date.class)){
								boolean hasDate = method.isAnnotationPresent(SqlFieldBean.Date.class); 
								if(hasDate){
									xx2(sql, method,table);
									for (Object object : set) {
										sql.append("'").append(new SimpleDateFormat(method.getAnnotation(SqlFieldBean.Date.class).value()).format((Date)object)).append("',");
									}
								}
								sql.deleteCharAt(sql.length() - 1).append(") and ");
							}else if(sqlset.value().equals(java.util.Date.class)){
								boolean hasDate = method.isAnnotationPresent(SqlFieldBean.Date.class); 
								if(hasDate){
									xx2(sql, method,table);
									for (Object object : set) {
										sql.append("'").append(new SimpleDateFormat(method.getAnnotation(SqlFieldBean.Date.class).value()).format((java.util.Date)object)).append("',");
									}
								}
								sql.deleteCharAt(sql.length() - 1).append(") and ");
							}else if(sqlset.value().equals(Long.class) || sqlset.value().equals(Double.class) || sqlset.value().equals(Float.class) || sqlset.value().equals(Integer.class) || sqlset.value().equals(BigDecimal.class) || sqlset.value().equals(Short.class)){
								xx2(sql, method,table);
								for (Object object : set) {
									sql.append(object).append(",");
								}
								sql.deleteCharAt(sql.length() - 1).append(") and ");
							}
						}
					}
				}
             }   
		}
		if(where != null && where.size() > 0){
			if (!isWhere) {
				sql.append(" where ");
				isWhere = true;
			}
			for (SqlWhere key : where.keySet()) {
				sql.append(table).append(".").append(key.getField());
				Object val = where.get(key);
				if(val instanceof String){
					if(SqlFieldBean.LIKE_ALL.equals(key.getCompare()))
						sql.append(" like '%").append(val).append("%' and ");
					else if(SqlFieldBean.LIKE_AFTER.equals(key.getCompare()))
						sql.append(" like '").append(val).append("%' and ");
					else if(SqlFieldBean.LIKE_BEFORE.equals(key.getCompare()))
						sql.append(" like '%").append(val).append("' and ");
					else if(SqlFieldBean.IN.equals(key.getCompare()))
						sql.append(" in(").append(val).append(") and ");
					else if(SqlFieldBean.NOT_IN.equals(key.getCompare()))
						sql.append(" not in(").append(val).append(") and ");
					else
						sql.append(key.getCompare()).append("'").append(val).append("' and ");
				}else
					sql.append(key.getCompare()).append(val).append(" and ");
			}
		}
		return sql.toString().endsWith("and ") ? sql.substring(0,sql.length() - 5) + ";" : sql + ";";
	}
	/**
	 * 生成 更新 sql语句
	 * @param obj 实体BEAN
	 * @param where 如果没有可以传null 
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static String toUpdateSql(Object obj, Map<SqlWhere, Object> where) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		boolean isUpdate = false;
		String table = null;
		Method[] methods = null;
		try {
			Class<?> c = Class.forName(obj.getClass().getName());
			methods = c.getDeclaredMethods();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		StringBuffer sql = new StringBuffer("update ");
		boolean hasSqlBean = obj.getClass().isAnnotationPresent(SqlBean.class); 
		if(!hasSqlBean)
			return null;
		SqlBean sqlbean = obj.getClass().getAnnotation(SqlBean.class);
		if("".equals(sqlbean.table()))
			table = obj.getClass().getSimpleName();
		else
			table = sqlbean.table();
		if(table == null)
			return null;
		sql.append(table);
		for (Method method : methods) {
			boolean hasAnnotation = method.isAnnotationPresent(SqlFieldBean.class);   
            if (hasAnnotation) {
            	Object re = method.invoke(obj);
				if(re != null){
	            	Class<?> cs = method.getReturnType();
					if (cs.equals(String.class) || cs.equals(Character.class)) {
						isUpdate = true;
						xx1(sql, method,table);
						sql.append("'").append(re).append("',");
					}else if(cs.equals(Boolean.class)){
						isUpdate = true;
						xx1(sql, method,table);
						Boolean r = (Boolean)re;
						sql.append(r ? 1 : 0).append(",");
					}else if(cs.equals(Date.class)){
						boolean hasDate = method.isAnnotationPresent(SqlFieldBean.Date.class); 
						if(hasDate){
							isUpdate = true;
							xx1(sql, method,table);
							sql.append("'").append(new SimpleDateFormat(method.getAnnotation(SqlFieldBean.Date.class).value()).format((Date)re)).append("',");
						}
					}else if(cs.equals(java.util.Date.class)){
						boolean hasDate = method.isAnnotationPresent(SqlFieldBean.Date.class); 
						if(hasDate){
							isUpdate = true;
							xx1(sql, method,table);
							sql.append("'").append(new SimpleDateFormat(method.getAnnotation(SqlFieldBean.Date.class).value()).format((java.util.Date)re)).append("',");
						}
					}else if(cs.equals(Long.class) || cs.equals(Double.class) || cs.equals(Float.class) || cs.equals(Integer.class) || cs.equals(BigDecimal.class) || cs.equals(Short.class)){
						isUpdate = true;
						xx1(sql, method,table);
						sql.append(re).append(",");
					}
				}
             }   
		}
		if(where != null && where.size() > 0){
			if (isUpdate) 
				sql.deleteCharAt(sql.length() - 1);
			sql.append(" where ");
			for (SqlWhere key : where.keySet()) {
				sql.append(table).append(".").append(key.getField());
				Object val = where.get(key);
				if(val instanceof String){
					if(SqlFieldBean.LIKE_ALL.equals(key.getCompare()))
						sql.append(" like '%").append(val).append("%' and ");
					else if(SqlFieldBean.LIKE_AFTER.equals(key.getCompare()))
						sql.append(" like '").append(val).append("%' and ");
					else if(SqlFieldBean.LIKE_BEFORE.equals(key.getCompare()))
						sql.append(" like '%").append(val).append("' and ");
					else if(SqlFieldBean.IN.equals(key.getCompare()))
						sql.append(" in(").append(val).append(") and ");
					else if(SqlFieldBean.NOT_IN.equals(key.getCompare()))
						sql.append(" not in(").append(val).append(") and ");
					else
						sql.append(key.getCompare()).append("'").append(val).append("' and ");
				}else
					sql.append(key.getCompare()).append(val).append(" and ");
			}
			return sql.toString().endsWith("and ") ? sql.substring(0,sql.length() - 5) + ";" : sql + ";";
		}
		return sql.toString().endsWith(",") ? sql.substring(0,sql.length() - 1) + ";" : sql + ";";
	}
	
	/**
	 * 生成查询sql语句
	 * @param obj 实体BEAN
	 * @param where 如果没有可以传null 
	 * @param order 如果没有可以传null 
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static String toQuerySql(Object obj, Map<SqlWhere, Object> where, SqlOrderBy[] order) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		Method[] methods = null;
		String table = null;
		try {
			Class<?> c = Class.forName(obj.getClass().getName());
			methods = c.getDeclaredMethods();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		StringBuffer sql = new StringBuffer("select * from ");
		boolean hasSqlBean = obj.getClass().isAnnotationPresent(SqlBean.class); 
		if(!hasSqlBean)
			return null;
		SqlBean sqlbean = obj.getClass().getAnnotation(SqlBean.class);
		if("".equals(sqlbean.table()))
			table = obj.getClass().getSimpleName();
		else
			table = sqlbean.table();
		if(table == null)
			return null;
		sql.append(table);
		fs(obj, methods, sql, table);
		where(where, sql, table);
		order(order, sql, table);
		return sql.toString().endsWith("and ") ? sql.substring(0,sql.length() - 5) + ";" : sql + ";";
	}
	/**
	 * 生成查询sql语句
	 * @param result 要查询的字段 以逗号分隔(可选)
	 * @param obj 实体BEAN
	 * @param where 如果没有可以传null 
	 * @param order 如果没有可以传null 
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static String toQuerySql(String result,Object obj, Map<SqlWhere, Object> where, SqlOrderBy[] order) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		Method[] methods = null;
		String table = null;
		try {
			Class<?> c = Class.forName(obj.getClass().getName());
			methods = c.getDeclaredMethods();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		StringBuffer sql = new StringBuffer("select ").append(result == null ? "*" : result).append(" from ");
		boolean hasSqlBean = obj.getClass().isAnnotationPresent(SqlBean.class); 
		if(!hasSqlBean)
			return null;
		SqlBean sqlbean = obj.getClass().getAnnotation(SqlBean.class);
		if("".equals(sqlbean.table()))
			table = obj.getClass().getSimpleName();
		else
			table = sqlbean.table();
		if(table == null)
			return null;
		sql.append(table);
		fs(obj, methods, sql, table);
		where(where, sql, table);
		order(order, sql, table);
		return sql.toString().endsWith("and ") ? sql.substring(0,sql.length() - 5) + ";" : sql + ";";
	}
	private static void fs(Object obj,Method[] methods,StringBuffer sql,String table) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		for (Method method : methods) {
			boolean hasAnnotation = method.isAnnotationPresent(SqlFieldBean.class);   
            if (hasAnnotation) {
				Object re = method.invoke(obj);
				if(re != null){
					String compare = method.getAnnotation(SqlFieldBean.class).compare();
					if (sql.indexOf(" where ") < 0) {
						sql.append(" where ");
					}
					Class<?> cs = method.getReturnType();
					if (cs.equals(String.class) || cs.equals(Character.class)) {
						xx(sql, method,table);
						if(compare.equals(SqlFieldBean.LIKE_ALL))
							sql.append("'%").append(re).append("%' and ");
						else if(compare.equals(SqlFieldBean.LIKE_AFTER))
							sql.append("'").append(re).append("%' and ");
						else if(compare.equals(SqlFieldBean.LIKE_BEFORE))
							sql.append("'%").append(re).append("' and ");
						else
							sql.append("'").append(re).append("' and ");
					}else if(cs.equals(Boolean.class)){
						xx(sql, method,table);
						Boolean r = (Boolean)re;
						sql.append(r ? 1 : 0).append(" and ");
					}else if(cs.equals(Date.class)){
						boolean hasDate = method.isAnnotationPresent(SqlFieldBean.Date.class); 
						if(hasDate){
							xx(sql, method,table);
							if(compare.equals(SqlFieldBean.LIKE_ALL))
								sql.append("'%").append(new SimpleDateFormat(method.getAnnotation(SqlFieldBean.Date.class).value()).format((Date)re)).append("%' and ");
							else if(compare.equals(SqlFieldBean.LIKE_AFTER))
								sql.append("'").append(new SimpleDateFormat(method.getAnnotation(SqlFieldBean.Date.class).value()).format((Date)re)).append("%' and ");
							else if(compare.equals(SqlFieldBean.LIKE_BEFORE))
								sql.append("'%").append(new SimpleDateFormat(method.getAnnotation(SqlFieldBean.Date.class).value()).format((Date)re)).append("' and ");
							else
								sql.append("'").append(new SimpleDateFormat(method.getAnnotation(SqlFieldBean.Date.class).value()).format((Date)re)).append("' and ");
						}
						
					}else if(cs.equals(java.util.Date.class)){
						boolean hasDate = method.isAnnotationPresent(SqlFieldBean.Date.class); 
						if(hasDate){
							xx(sql, method,table);
							if(compare.equals(SqlFieldBean.LIKE_ALL))
								sql.append("'%").append(new SimpleDateFormat(method.getAnnotation(SqlFieldBean.Date.class).value()).format((java.util.Date)re)).append("%' and ");
							else if(compare.equals(SqlFieldBean.LIKE_AFTER))
								sql.append("'").append(new SimpleDateFormat(method.getAnnotation(SqlFieldBean.Date.class).value()).format((java.util.Date)re)).append("%' and ");
							else if(compare.equals(SqlFieldBean.LIKE_BEFORE))
								sql.append("'%").append(new SimpleDateFormat(method.getAnnotation(SqlFieldBean.Date.class).value()).format((java.util.Date)re)).append("' and ");
							else
								sql.append("'").append(new SimpleDateFormat(method.getAnnotation(SqlFieldBean.Date.class).value()).format((java.util.Date)re)).append("' and ");
						}
					}else if(cs.equals(Long.class) || cs.equals(Double.class) || cs.equals(Float.class) || cs.equals(Integer.class) || cs.equals(BigDecimal.class) || cs.equals(Short.class)){
						xx(sql, method,table);
						sql.append(re).append(" and ");
					}else{
						boolean hasSet = method.isAnnotationPresent(SqlFieldBean.Set.class); 
						if(hasSet){
							Collection<?> set = getObject(re);
							if(set.size() > 0){
								SqlFieldBean.Set sqlset = method.getAnnotation(SqlFieldBean.Set.class);
								if(String.class.equals(sqlset.value()) || sqlset.value().equals(Character.class)){
									xx2(sql, method,table);
									for (Object object : set) {
										sql.append("'").append(object).append("',");
									}
									sql.deleteCharAt(sql.length() - 1).append(") and ");
								}else if(sqlset.value().equals(Boolean.class)){
									xx2(sql, method,table);
									for (Object object : set) {
										Boolean r = (Boolean)object;
										sql.append(r ? 1 : 0).append(",");
									}
									sql.deleteCharAt(sql.length() - 1).append(") and ");
								}else if(sqlset.value().equals(Date.class)){
									boolean hasDate = method.isAnnotationPresent(SqlFieldBean.Date.class); 
									if(hasDate){
										xx2(sql, method,table);
										for (Object object : set) {
											sql.append("'").append(new SimpleDateFormat(method.getAnnotation(SqlFieldBean.Date.class).value()).format((Date)object)).append("',");
										}
									}
									sql.deleteCharAt(sql.length() - 1).append(") and ");
								}else if(sqlset.value().equals(java.util.Date.class)){
									boolean hasDate = method.isAnnotationPresent(SqlFieldBean.Date.class); 
									if(hasDate){
										xx2(sql, method,table);
										for (Object object : set) {
											sql.append("'").append(new SimpleDateFormat(method.getAnnotation(SqlFieldBean.Date.class).value()).format((java.util.Date)object)).append("',");
										}
									}
									sql.deleteCharAt(sql.length() - 1).append(") and ");
								}else if(sqlset.value().equals(Long.class) || sqlset.value().equals(Double.class) || sqlset.value().equals(Float.class) || sqlset.value().equals(Integer.class) || sqlset.value().equals(BigDecimal.class) || sqlset.value().equals(Short.class)){
									xx2(sql, method,table);
									for (Object object : set) {
										sql.append(object).append(",");
									}
									sql.deleteCharAt(sql.length() - 1).append(") and ");
								}
							}
						}
					}
				}
             }   
		}
	}
	private static void where(Map<SqlWhere, Object> where,StringBuffer sql,String table){
		if(where != null && where.size() > 0){
			if (sql.indexOf(" where ") < 0) {
				sql.append(" where ");
			}
			for (SqlWhere key : where.keySet()) {
				sql.append(table).append(".").append(key.getField());
				Object val = where.get(key);
				if(val instanceof String){
					if(SqlFieldBean.LIKE_ALL.equals(key.getCompare()))
						sql.append(" like '%").append(val).append("%' and ");
					else if(SqlFieldBean.LIKE_AFTER.equals(key.getCompare()))
						sql.append(" like '").append(val).append("%' and ");
					else if(SqlFieldBean.LIKE_BEFORE.equals(key.getCompare()))
						sql.append(" like '%").append(val).append("' and ");
					else if(SqlFieldBean.IN.equals(key.getCompare()))
						sql.append(" in(").append(val).append(") and ");
					else if(SqlFieldBean.NOT_IN.equals(key.getCompare()))
						sql.append(" not in(").append(val).append(") and ");
					else
						sql.append(key.getCompare()).append("'").append(val).append("' and ");
				}else{
					sql.append(key.getCompare()).append(val).append(" and ");
				}
			}
		}
	}
	private static void order(SqlOrderBy[] order,StringBuffer sql,String table){
		if(order != null && order.length > 0){
			if(sql.toString().endsWith("and ")){
				sql = new StringBuffer(sql.substring(0, sql.length() - 5));
				sql.append(" order by ");
			}else if(sql.toString().endsWith(table)){
				sql.append(" order by ");
			}
			for (SqlOrderBy o : order) {
				sql.append(table).append(".").append(o.getField()).append(" ").append(o.getOrder()).append(",");
			}
			sql.deleteCharAt(sql.length() - 1);
		}
	}
}
