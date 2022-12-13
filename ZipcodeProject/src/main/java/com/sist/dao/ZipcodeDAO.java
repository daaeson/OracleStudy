package com.sist.dao;
import java.util.*;
import java.sql.*;
/*
	회원가입의 일부 ==> 필수 조건 1) 아이디 중복 체크, 2) 우편번호 검색
*/
public class ZipcodeDAO {
	private Connection conn;	// 오라클 연결하는 인터페이스
	private PreparedStatement ps; 	// 송수신 => SQL 문장 전송 후 실행, 결과값 받기
	// 오라클 주소 설정
	// jdbc:업체명:드라이버종류:@오라클주소(IP):PORT:데이터베이스명
	// jdbc:oracle:thin(연결만담당하는-무료로제공):@localhost:1521:XE
	// jdbc:mysql://localhost:3306?mydb
	// MS-SQL = 1433
	private final String URL="jdbc:oracle:thin:@localhost:1521:XE";
	// 드라이버 설치
	public ZipcodeDAO()
	{
		try
		{
			Class.forName("oracle.jdbc.driver.OracleDriver");
			// Class.forName ==> 클래스 정보를 읽어온다(리플렉션)
			// 1) 메모리 할당 2) 메소드를 호출 3) 변수 초기값...
			// 리플렉션 주로 사용 => 스프링, MyBatis
			// 패키지명.클래스명 => 메모리 할당, 등록을 요청
		}catch(Exception ex) {}
	}
	// 연결 ==> 검색
	public void getConnection()
	{
		try
		{
			conn=DriverManager.getConnection(URL,"hr","happy");
			// conn hr/happy
		}catch(Exception ex) {}
	}
	// 해제
	public void disConnection()
	{
		try
		{
			if(ps!=null) ps.close();
			if(conn!=null) conn.close(); 	// LIKE
		}catch(Exception ex) {}
	}
	// 기능 2개
	// 1. 우편번호 검색 ==> 지번/길 (다음에서 제공하는 라이브러리)
	public ArrayList<ZipcodeVO> postFind(String dong)
	{
		ArrayList<ZipcodeVO> list = new ArrayList<ZipcodeVO>();
		try
		{
			// 1. 연결
			getConnection();
			// 2. SQL 문장 제작
			String sql = "SELECT zipcode,sido,gugun,dong,NVL(bunji,' ') "
					+"FROM zipcode "
					+"WHERE dong LIKE '%'||?||'%'";
			// 3. SQL 문장을 오라클로 전송
			ps = conn.prepareStatement(sql);
			// 4. ?에 값을 채운다
			ps.setString(1, dong);
			// 5. 실행 후에 결과값을 가지고 온다
			ResultSet rs = ps.executeQuery();	// 한 줄씩 읽어온다
			while(rs.next())
			{
				ZipcodeVO vo = new ZipcodeVO();
				vo.setZipcode(rs.getString(1));
				vo.setSido(rs.getString(2));
				vo.setGugun(rs.getString(3));
				vo.setDong(rs.getString(4));
				vo.setBunji(rs.getString(5));
				list.add(vo);
			}
			rs.close();
		}catch(Exception ex)
		{
			// 오류 확인
			ex.printStackTrace();
		}
		finally
		{
			// 해제
			disConnection();
		}
		return list;
	}
	// 2. 검색 개수 확인
	public int postCount(String dong)
	{
		int count=0;
		try
		{
			// 1. 연결
			getConnection();
			// 2. SQL 문장
			String sql = "SELECT COUNT(*) "
					+ "FROM zipcode "
					+ "WHERE dong LIKE '%'||?||'%'";
			// 3. 오라클로 전송
			ps=conn.prepareStatement(sql);
			// 4. ?에 값을 채운다
			ps.setString(1, dong);
			// 5. 실행 후 결과값을 읽어온다
			ResultSet rs = ps.executeQuery();
			// 6. count에 저장
			rs.next();
			count=rs.getInt(1);
			rs.close();
		}catch(Exception ex)
		{
			// 오류 확인
			ex.printStackTrace();
		}
		finally
		{
			// 해제
			disConnection();
		}
		return count;
	}
	
	
	
}








