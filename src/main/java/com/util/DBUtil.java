package com.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.hadoop.AnalyseJob;

public class DBUtil {
	
	private Properties pro;
	private Connection con;
	private FileReader fr;
	private Statement stmt;
	private ResultSet rs;

	public DBUtil() {
		loadDriver();
	}

	public Connection getCon() {
		return con;
	}

	/**
	 * @date 2013年8月26日 加载驱动
	 */
	public void loadDriver() {

		pro = new Properties();
		try {
//			String path = "./conf/";
//			String path ="/app/soft/app/hxdata/src/main/resources/";
			InputStream ips = Config.class.getClassLoader().getResourceAsStream("mysql.online.properties");
			pro.load(ips);
			Class.forName(pro.getProperty("DRIVER"));
			this.conData();
		} catch (FileNotFoundException fe) {
			System.out.println("指定路径的文件不存在或者无权限访问！！！");
			fe.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("无法找到所加载的驱动，或是驱动文件不存在！！！");
			e.printStackTrace();
		}
	}

	/**
	 * @date 2013年8月26日 插入操作
	 */
	public int insertSql(String sql) {
		try {
			stmt = con.createStatement();
			int count = stmt.executeUpdate(sql);
			return count;
		} catch (SQLException e) {
			System.out.println("bad sql:"+sql);
			e.printStackTrace();
			return 0;
		} 
	}
	
	
	/**
	 * @date 2013年9月3日
	 * 通用的查询操作
	 */
	public HashMap<Integer,ArrayList<String>> getList(String sql){
		HashMap<Integer,ArrayList<String>> map = new HashMap<Integer, ArrayList<String>>();
		ArrayList<String> arealist = new ArrayList<String>();
		ArrayList<String> urllist = new ArrayList<String>();
		ArrayList<String> phone_arealist = new ArrayList<String>();
		ArrayList<String> phone_urllist = new ArrayList<String>();
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			while(rs.next())
	          {
				int type = rs.getInt("type");
				String name = rs.getString("name");
				if(type==0){
					arealist.add(name);
				}else if(type ==1){
					urllist.add(name);
				}else if(type ==2){
					phone_arealist.add(name);
				}else if(type ==3){
					phone_urllist.add(name);
				}
	          }
			map.put(0, arealist);
			map.put(1, urllist);
			map.put(2, phone_arealist);
			map.put(3, phone_urllist);
		} catch (SQLException e) {
			System.out.println("bad sql:"+sql);
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * @date 2013年9月12日
	 * 根据name获取memo
	 */
	public String getDescByName(String name,int type){
		String sql = "select memo from log_conf where flag = 0 and name ='"+name+"' and type = "+type ;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			if(rs.next()){
				return rs.getString("memo");
			}else{
				return "";
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return "";
		}
	}
	public Map<String, Long> getCarBymoneyCost(Integer type,String stdate,String enddate) {
//		Logger logger = Logger.getLogger(AnalyseJob.class);// in (1,3,5)
		Map<String, Long> map=new HashMap<>();
		String sql = "select detail,act_time from money_cost where money<=10 and type="+type+" and act_time>='"+stdate+"' and act_time<'"+enddate+"'";
		try {
			stmt = getConnection().createStatement();
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				 try {
					String[] detail =  rs.getString("detail").split("target=\"_blank\">");
					String id = "";
					if (detail != null && detail.length > 1) {
						String[] detailT = detail[1].split("</a>");
						if (detailT != null && detailT.length > 0) {
							id = detailT[0];
//							Long idL = Long.parseLong(id);
							map.put(id,  DateUtil.str2Date(rs.getString("act_time"), "yyyy-MM-dd HH:mm").getTime());
						}
					}
				} catch (NumberFormatException e) {
					
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				}
				
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return map;
		}
		return map;
	}
	/**
	 * @date 2014年1月15日
	 * 根据name获取countType
	 */
	public int getCountTypeByName(String name,int type){
		String sql = "select count_type from log_conf where flag = 0 and name ='"+name+"' and type = "+type ;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			if(rs.next()){
				return rs.getInt("count_type");
			}else{
				return 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * @date 2013年8月26日 关闭数据库
	 */
	public void closeCon() {
		try {
			if (stmt != null)
				stmt.close();
			if (con != null)
				con.close();
			System.out.println("数据库正常关闭！");
		} catch (SQLException e) {
			System.out.println("数据库未正常关闭！");
			e.printStackTrace();
		}
	}

	public void conData() {
		try {
			con = DriverManager.getConnection(pro.getProperty("URL"), pro.getProperty("USER"),
					pro.getProperty("PASSWORD"));
			System.out.println("数据库连接成功！！！");
			
		} catch (SQLException e) {
			System.out.println("不能连接到指定的数据库，请检查相应的基本信息！！！");
			e.printStackTrace();
		}
	}
	
	public static Connection getConnection() {
		Connection con = null;
		Properties pro = new Properties();
		try {
//			String path = "./conf/";
//			String path ="/app/soft/app/hxdata/src/main/resources/";
//			FileReader fr = new FileReader(path + "mysql.online.properties");
			InputStream ips = Config.class.getClassLoader().getResourceAsStream("mysql.online.properties");
			pro.load(ips);
			Class.forName(pro.getProperty("DRIVER"));
			con = DriverManager.getConnection(pro.getProperty("URL_ONLY"), pro.getProperty("USER"),
					pro.getProperty("PASSWORD"));
		} catch (FileNotFoundException fe) {
			fe.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
	}
	
	public static Connection getConnection2() {
		Connection con = null;
		Properties pro = new Properties();
		try {
//			String path = "./conf/";
			InputStream ips = Config.class.getClassLoader().getResourceAsStream("mysql.online.properties");
			pro.load(ips);
			Class.forName(pro.getProperty("DRIVER"));
			con = DriverManager.getConnection(pro.getProperty("URL"), pro.getProperty("USER"),
					pro.getProperty("PASSWORD"));
		} catch (FileNotFoundException fe) {
			fe.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
	}
	
	public static Connection getConnection249() {
		Connection con = null;
		Properties pro = new Properties();
		try {
//			String path = "./conf/";
			InputStream ips = Config.class.getClassLoader().getResourceAsStream("mysql.online.properties");
			pro.load(ips);
			Class.forName(pro.getProperty("DRIVER"));
			con = DriverManager.getConnection(pro.getProperty("URL249"), pro.getProperty("USER249"),
					pro.getProperty("PASSWORD249"));
		} catch (FileNotFoundException fe) {
			fe.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
	}
	
}

