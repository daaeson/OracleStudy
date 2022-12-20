package com.sist.dao;
import java.util.*;
import java.sql.*;
public class FoodDAO {

		// 연결 객체
		private Connection conn;
		// SQL 전송, 결과값 읽기
		private PreparedStatement ps;
		// 오라클 주소, 유저명, 비밀번호, 드라이버명 => final 
		// => 보안을 위해 xml | properties 파일로 WEB-INF에 저장
		// 경로( \\, /) 문제
		private final String URL="jdbc:oracle:thin:@localhost:1521:XE";
		private final String DRIVER="oracle.jdbc.driver.OracleDriver";
		private final String USER="hr";
		private final String PWD="happy";
		
		// 드라이버 등록
		public FoodDAO()
		{
			// 한 번만 등록이 가능 => 객체 생성 시 한 번만 호출 : 자동로그인, ID 저장
			try
			{
				// 등록 => Class.forName(), Properties 이용
				Class.forName(DRIVER);	// DriverManager => 메모리 할당
				// 패키지명.클래스명을 등록 => 메모리 할당을 요청
				// => MyBatis, Spring, Spring-Boot : 클래스 관리자 => 등록 => 싱글톤 패턴
				// 스프링 => new를 사용하지 않는다 => 메모리 할당 => 어노테이션
				/*
					@Repository
					class A
					
					class B
					{
						@Autowired
						A a;
					}
				*/
			}catch(Exception ex) {}
		}
		// 오라클 연결 => DBCP => ORM
		public void getConnection()
		{
			try
			{
				conn=DriverManager.getConnection(URL,USER,PWD);
			}catch(Exception ex) {}
		}
		// 오라클 닫기
		public void disConnection()
		{
			try
			{
				if(ps!=null) ps.close();
				if(conn!=null) conn.close();
			}catch(Exception ex) {}
		}
		
		// 기능
		// 1. 목록 출력 => inline View, index
		public ArrayList<FoodVO> foodListData(int page)
		{
			ArrayList<FoodVO> list = new ArrayList<FoodVO>();
			try
			{
				// 1. 연결
				getConnection();
				// 2. SQL 문장 제작
				String sql = "SELECT fno,name,poster,score,num "
						+ "FROM (SELECT fno,name,poster,score,rownum as num "
						+ "FROM (SELECT /*+ INDEX_ASC(food_location pk_food_location)*/ fno,name,poster,score "	//index 사용하여 정렬
						+ "FROM food_location)) "
						+ "WHERE num BETWEEN ? AND ?";
				// 3. 오라클로 전송
				ps=conn.prepareStatement(sql);
				// 4. ?에 값을 채운다
				int rowSize=20;
				int start=(rowSize*page)-(rowSize-1);		// rownum : 1부터 시작 => 오라클 문법
				int end=rowSize*page;
				/*
					1page => 1 ~ 20 => start=(20*1)-19=1
					2page => 21 ~ 40 => start=(20*2)-19=21
					3page => 41 ~ 60
				*/
				ps.setInt(1, start);
				ps.setInt(2, end);
				// 5. 실행 요청
				ResultSet rs=ps.executeQuery();
				while(rs.next())
				{
					FoodVO vo=new FoodVO();
					vo.setFno(rs.getInt(1));
					vo.setName(rs.getString(2));
					vo.setPoster(rs.getString(3));
					vo.setScore(rs.getDouble(4));
					// vo : 맛집 하나에 대한 정보를 가지고 있다
					list.add(vo);	// list 안에 맛집 정보 20개를 추가
				}
				rs.close();
				
			}catch(Exception ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				disConnection();
			}
			return list;
		}
		
		
}
