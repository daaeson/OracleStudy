package com.sist.dao;
// 오라클 연결
import java.util.*;
import java.sql.*;
public class BoardDAO {
	private Connection conn;
	private PreparedStatement ps;
	private final String URL = "jdbc:oracle:thin:@localhost:1521:XE";
	
	// 1. 드라이버 등록
	public BoardDAO()
	{
		try
		{
			Class.forName("oracle.jdbc.driver.OracleDriver");
		}catch(Exception ex) {}
	}
	// 2. 오라클 연결
	public void getConnection()
	{
		try
		{
			conn=DriverManager.getConnection(URL,"hr","happy");
		}catch(Exception ex) {}
	}
	// 3. 오라클 해제
	public void disConnection()
	{
		try
		{
			if(ps!=null) ps.close();
			if(conn!=null) conn.close();
		}catch(Exception ex) {}
	}
	// 4. 기능 설정
	// ==> 페이징(X) ==> 이미지(맛집=>댓글)
	//  1) 목록 출력 => SELECT (ORDER BY)
	public ArrayList<BoardVO> boardListData()
	{
		ArrayList<BoardVO> list = new ArrayList<BoardVO>();
		try
		{
			// 1. 연결
			getConnection(); 	// 메소드는 한 개의 기능을 반복적으로 재사용할 수 있도록 생성
			// 2. SQL 문장 제작
			String sql = "SELECT no,subject,name,TO_CHAR(regdate,'YYYY/MM/DD'),hit "
					+"FROM freeboard "
					+"ORDER BY no DESC";
			// 3. 오라클 서버로 전송
			ps=conn.prepareStatement(sql);
			// 4. 결과값 받기
			ResultSet rs=ps.executeQuery();
			// 5. 결과값을 ArrayList에 담는다
			// 오라클에 저장된 데이터는 자동 정렬되어 있지 않음
			while(rs.next())	// 메모리에 출력된 첫번째 위치에 커서를 이동한다
			{
				BoardVO vo = new BoardVO();
				vo.setNo(rs.getInt(1));
				vo.setSubject(rs.getString(2));
				vo.setName(rs.getString(3));
				vo.setDbday(rs.getString(4));
				vo.setHit(rs.getInt(5));
				list.add(vo);
			}
			rs.close();
			
			
			
		}catch(Exception ex) 
		{
			ex.printStackTrace();// 오류 확인
		}
		finally
		{
			disConnection(); // 오라클 연결 해제
		}
		return list;
	}
	//  2) 게시물 추가 => INSERT
	public void boardInsert(BoardVO vo)
	{
		try
		{
			// 1. 연결
			getConnection();
			// 2. SQL 문장
			String sql = "INSERT INTO freeboard(no,name,subject,content,pwd) VALUES("
					+"(SELECT NVL(MAX(no)+1,1) FROM freeboard),?,?,?,?)";
			// 3. 오라클로 전송
			ps=conn.prepareStatement(sql);
			// 4. 실행 전에 물음표에 값 채우기
			ps.setString(1, vo.getName());
			ps.setString(2, vo.getSubject());
			ps.setString(3, vo.getContent());
			ps.setString(4, vo.getPwd());
			// 5. 실행 명령
			ps.executeUpdate();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			disConnection();
		}
	}
	//  3) 상세보기 => SELECT (WHERE)
	public BoardVO boardDetailData(int no)		// no : 게시물 번호 => 사용자로부터 받아서 데이터를 넘겨준다 (Primary Key를 이용하여 상세보기)
	{
		BoardVO vo = new BoardVO();
		try
		{
			// 1. 연결
			getConnection();
			// 2. 전송할 SQL 문장 제작
			String sql="UPDATE freeboard SET "
					+"hit=hit+1 "
					+"WHERE no="+no; // 물음표 안에 값을 채우기
			// 3. 전송
			ps=conn.prepareStatement(sql);
			// 4. 실행
			ps.executeUpdate();	// 실행 후 커밋까지 하는 명령
			// 5. 전송할 SQL 문장 제작
			sql="SELECT no,name,subject,content,regdate,hit "
					+ "FROM freeboard "
					+ "WHERE no="+no;
			// 6. 오라클로 전송
			ps=conn.prepareStatement(sql);
			// 7. 실행 후 결과값 읽기
			ResultSet rs = ps.executeQuery();
			// 8. 커서의 위치 이동
			rs.next();
			// 9. VO에 값을 채운다
			vo.setNo(rs.getInt(1));
			vo.setName(rs.getString(2));
			vo.setSubject(rs.getString(3));
			vo.setContent(rs.getString(4));
			vo.setRegdate(rs.getDate(5));
			vo.setHit(rs.getInt(6));
			rs.close();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			disConnection();
		}
		return vo;
	}
	
	//  4) 수정 => UPDATE 		// 본인 여부 확인 (비밀번호 비교)
	//  5) 삭제 => DELETE			// 본인 여부 확인 (비밀번호 비교)
	public void boardDelete(int no)
	{
		try
		{
			// 1. 연결
			getConnection();
			// 2. SQL 문장
			String sql="DELETE FROM freeboard "
					+"WHERE no="+no;
			// 3. SQL 문장 전송
			ps=conn.prepareStatement(sql);
			// 4. 실행 요청
			ps.executeUpdate();	// commit()
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			disConnection();
		}
	}
	
	//  6) 검색 => SELECT (LIKE)




}










