package com.kakaobank.repository.db;

import com.kakaobank.domain.school.School;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.*;

public class DQLService extends SQLiteManager {

    private final static Logger log = LogManager.getLogger(DQLService.class);

    public DQLService(String dbFileName) {
        super(dbFileName);
    }

    // 데이터 조회 함수

    public List<School> selectSchoolList(String schoolName, String levelPattern) {
        String sql = "SELECT * FROM schools WHERE name LIKE ? AND name LIKE ?";

        List<School> result = new ArrayList<>();

        Connection conn = ensureConnection();
        PreparedStatement pstmt = null;


        try {

            pstmt = conn.prepareStatement(sql);


            pstmt.setObject(1, "%" + schoolName + "%");
            pstmt.setObject(2, "%" + levelPattern);


            result = schoolRowMapper(pstmt.executeQuery());

        } catch (SQLException e) {
            log.error("select school list error", e);
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }

                closeConnection();

            } catch (SQLException e) {
                log.error("select school close connection error", e);
            }
        }


        return new ArrayList<>(result);
    }

    private List<School> schoolRowMapper(ResultSet rs) throws SQLException {
        List<School> schools = new ArrayList<>();
        while (rs.next()) {
            School school = School.builder()
                    .gender(rs.getString("gender"))
                    .location(rs.getString("location"))
                    .name(rs.getString("name"))
                    .property(rs.getString("property"))
                    .build();

            schools.add(school);
        }

        return new ArrayList<>(schools);
    }
}
