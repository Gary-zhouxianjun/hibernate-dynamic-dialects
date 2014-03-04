package com.gary.dao.hibernate;

public class DataSourceContextHolder {
      
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>();  
    
    public static void setDataSourceType(String customerType) { 
        contextHolder.set(customerType);  
    }  
      
    public static String getDataSourceType() {  
        return contextHolder.get();  
    }  
      
    public static void clearDataSourceType() {  
        contextHolder.remove();  
    }
}
