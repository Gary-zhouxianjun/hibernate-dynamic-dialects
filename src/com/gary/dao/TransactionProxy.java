package com.gary.dao;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.log4j.Logger;

public class TransactionProxy implements InvocationHandler {
	protected Logger logger = Logger.getLogger(getClass());
	protected Object target; 
	protected String dataSource;
	public Object bind(Object target) {  
		if(Proxy.isProxyClass(target.getClass())){
			return Proxy.getInvocationHandler(target);
		}
        this.target = target;  
        //取得代理对象  
       return Proxy.newProxyInstance(target.getClass().getClassLoader(),  
             target.getClass().getInterfaces(), this);   //要绑定接口
    } 
	public Object bind(Object target, String dataSource) {  
		this.dataSource = dataSource;
       return bind(target);   //要绑定接口
    } 
	@Override
	public Object invoke(Object arg0, Method arg1, Object[] arg2)
			throws Throwable {
		// TODO Auto-generated method stub
		return invokeHandler(arg0, arg1, arg2);
	}
	
	protected Object invokeHandler(Object arg0, Method arg1, Object[] arg2)
			throws Throwable {
		// TODO Auto-generated method stub
		return arg1.invoke(target, arg2);
	}

}
