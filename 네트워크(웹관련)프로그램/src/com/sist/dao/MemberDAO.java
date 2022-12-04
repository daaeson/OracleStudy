package com.sist.dao;
import java.sql.*;
import java.util.*;/*
					--INSERT INTO member VALUES('hong','1234','홍길동','남자');
					--INSERT INTO member VALUES('park','1234','박문수','남자');
					--INSERT INTO member VALUES('shim','1234','심청이','여자');
					--COMMIT;*/

public class MemberDAO {

	private Connection conn;
	private PreparedStatement ps;
	private final String URL = "jdbc:oracle:thin:@localhost:1521:xe";
	
	// 드라이버 등록
	public MemberDAO()
	{
		try
		{
			Class.forName("oracle.jdbc.driver.OracleDriver");
		}catch(Exception ex) {}
	}
	// 연결
	public void getConnection()
	{
		try
		{
			conn = DriverManager.getConnection(URL,"hr","happy");
		}catch(Exception ex) {}
	}
	// 해제
	public void disConnection()
	{
		try
		{
			if(ps!=null) ps.close();
			if(conn!=null) conn.close();
			// exit
		}catch(Exception ex) {}
	}
	// 기능 => 로그인
	public String isLogin(String id, String pwd)
	{
		String result = "";
		try
		{
			// 연결
			getConnection();
			// SQL문장 제작
			String sql = "SELECT COUNT(*) FROM member WHERE id ="+id+"'";
			ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			rs.next();
			int count = rs.getInt(1);
			if(count==0)
			{
				result="NOID";
			}
			else
			{
				sql = "SELECT pwd,name FROM member WHERE id ="+id+"'";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				rs.next();
				String db_pwd = rs.getString(1);
				String name = rs.getString(2);
				
				if(db_pwd.equals(pwd))
				{
					result = name;
				}
				else
				{
					result = "NOPWD";
				}
			}
			// 오라클 전송
			// 결과값 받기
		}catch(Exception ex)
		{
			ex.printStackTrace();// 오류확인
		}
		
		finally
		{
			disConnection();// 해제
		}
		return result;
	}
}
