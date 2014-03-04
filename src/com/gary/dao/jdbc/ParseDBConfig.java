package com.gary.dao.jdbc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.gary.dao.dto.DBConfigDto;

/**
 * @author Gary 操作配置文件类 读 写 修改 删除等操作
 */
public class ParseDBConfig {

	/**
	 * 读取xml配置文件
	 * 
	 * @param path
	 * @return
	 */
	public static Vector<DBConfigDto> readConfigInfo(String path) {
		Vector<DBConfigDto> dsConfig = null;
		FileInputStream fi = null;
		try {
			fi = new FileInputStream(path);// 读取路径文件
			dsConfig = new Vector<DBConfigDto>();
			SAXBuilder sb = new SAXBuilder();
			Document doc = sb.build(fi);
			Element root = doc.getRootElement();
			List<?> pools = root.getChildren();
			Element pool = null;
			Iterator<?> allPool = pools.iterator();
			while (allPool.hasNext()) {
				pool = (Element) allPool.next();
				DBConfigDto dscBean = new DBConfigDto();
				dscBean.setType(pool.getChild("type").getText());
				dscBean.setName(pool.getChild("name").getText());
				dscBean.setDriver(pool.getChild("driver").getText());
				dscBean.setUrl(pool.getChild("url").getText());
				dscBean.setUsername(pool.getChild("username").getText());
				dscBean.setPassword(pool.getChild("password").getText());
				dscBean.setMaxconn(Integer.parseInt(pool.getChild("maxconn").getText()));
				dscBean.setMaxsize(Integer.parseInt(pool.getChild("maxsize").getText()));
				dsConfig.add(dscBean);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		finally {
			try {
				fi.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return dsConfig;
	}

	/**
	 * 增加配置文件
	 * 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void addConfigInfo(String path, DBConfigDto dsb) {
		FileInputStream fi = null;
		FileOutputStream fo = null;
		try {
			fi = new FileInputStream(path);// 读取xml流

			SAXBuilder sb = new SAXBuilder();

			Document doc = sb.build(fi); // 得到xml
			Element root = doc.getRootElement();
			List pools = root.getChildren();// 得到xml子树

			Element newpool = new Element("pool"); // 创建新连接池

			Element pooltype = new Element("type"); // 设置连接池类型
			pooltype.setText(dsb.getType());
			newpool.addContent(pooltype);

			Element poolname = new Element("name");// 设置连接池名字
			poolname.setText(dsb.getName());
			newpool.addContent(poolname);

			Element pooldriver = new Element("driver"); // 设置连接池驱动
			pooldriver.addContent(dsb.getDriver());
			newpool.addContent(pooldriver);

			Element poolurl = new Element("url");// 设置连接池url
			poolurl.setText(dsb.getUrl());
			newpool.addContent(poolurl);

			Element poolusername = new Element("username");// 设置连接池用户名
			poolusername.setText(dsb.getUsername());
			newpool.addContent(poolusername);

			Element poolpassword = new Element("password");// 设置连接池密码
			poolpassword.setText(dsb.getPassword());
			newpool.addContent(poolpassword);

			Element poolmaxconn = new Element("maxconn");// 设置连接池最大连接
			poolmaxconn.setText(String.valueOf(dsb.getMaxconn()));
			newpool.addContent(poolmaxconn);
			
			Element poolmaxsize = new Element("maxsize");// 设置连接池最大缓存
			poolmaxsize.setText(String.valueOf(dsb.getMaxsize()));
			newpool.addContent(poolmaxsize);
			pools.add(newpool);// 将child添加到root
			Format format = Format.getPrettyFormat();
			format.setIndent("");
			format.setEncoding("utf-8");
			XMLOutputter outp = new XMLOutputter(format);
			fo = new FileOutputStream(path);
			outp.output(doc, fo);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		}
	}

	/**
	 * 删除配置文件
	 */
	public static void delConfigInfo(String path, String name) {
		FileInputStream fi = null;
		FileOutputStream fo = null;
		try {
			fi = new FileInputStream(path);// 读取路径文件
			SAXBuilder sb = new SAXBuilder();
			Document doc = sb.build(fi);
			Element root = doc.getRootElement();
			List<?> pools = root.getChildren();
			Element pool = null;
			Iterator<?> allPool = pools.iterator();
			while (allPool.hasNext()) {
				pool = (Element) allPool.next();
				if (pool.getChild("name").getText().equals(name)) {
					pools.remove(pool);
					break;
				}
			}
			Format format = Format.getPrettyFormat();
			format.setIndent("");
			format.setEncoding("utf-8");
			XMLOutputter outp = new XMLOutputter(format);
			fo = new FileOutputStream(path);
			outp.output(doc, fo);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		finally {
			try {
				fi.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		/*String path = "db.xml";
		DBConnMgr mgr = DBConnMgr.getInstance(path);
		DBConn con = mgr.getConnection("qmail");
		Statement stm = con.createStatement();
		String sql = "select * from tbl_emails where keyword = ?";
		PreparedStatement pst = con.prepareStatement(sql);
		pst.setString(1, "gary");
		ResultSet rs = pst.executeQuery();
		while (rs.next()) {
			System.out.println(rs.getObject(1));
		}
		mgr.freeConnection("qmail", con);
		con = mgr.getConnection("V1000DB");
		System.out.println(con);
		mgr.freeConnection("V1000DB", con);
		mgr.getConnection("V1000DB");
		mgr.freeConnection("V1000DB1", con);*/
		/*String path = "db.xml";
		DBConnMgr mgr = DBConnMgr.getInstance(path);
		DBConn con = mgr.getConnection("emails");*/
		/*List<Map<String, Object>> list = null;
		
		long startTime=System.currentTimeMillis();
		list = con.cacheQuery("select * from tbl_emails where keyWord = 'gary'", null);
		long endTime=System.currentTimeMillis();
		System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
		
		startTime=System.currentTimeMillis();
		list = con.cacheQuery("select * from tbl_emails where keyWord = 'gary'", null);
		endTime=System.currentTimeMillis();
		System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
		
		startTime=System.currentTimeMillis();
		list = con.cacheQuery("select * from tbl_emails where keyWord = 'berry'", null);
		endTime=System.currentTimeMillis();
		System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
		
		startTime=System.currentTimeMillis();
		list = con.cacheQuery("select * from tbl_emails where keyWord = 'moudl'", null);
		endTime=System.currentTimeMillis();
		System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
		
		startTime=System.currentTimeMillis();
		list = con.cacheQuery("select * from tbl_emails where keyWord = '众方'", null);
		endTime=System.currentTimeMillis();
		System.out.println("程序运行时间： "+(endTime-startTime)+"ms");

		startTime=System.currentTimeMillis();
		list = con.cacheQuery("select * from tbl_emails", null);
		endTime=System.currentTimeMillis();
		System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
		mgr.freeConnection("emails", con);*/
		
		/*List list = new ArrayList();
		for (int i = 0; i < 3; i++) {
			List listc = new ArrayList();
			Object[] objs = {"email"+i,"url","keyWord",null,"queryUserName",99,null,null,null,null,null,null};
			listc.addAll(Arrays.asList(objs));
			list.add(listc);
		}
		int r[] = con.batchExecuet("insert into tbl_emails values(?,?,?,?,?,?,?,?,?,?,?,?)", list);
		System.out.println(r);*/
		
		/*List list2 = new ArrayList();
		for (int i = 0; i < 3; i++) {
			list2.add("update tbl_emails set keyWord = '"+i+"' where mail = 'email"+i+"'");
		}
		int r[] = con.batchExecuet(list2);
		for (int i : r) {
			System.out.println(i);
		}*/
		
	}
}