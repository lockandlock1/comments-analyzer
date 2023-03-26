package com.kakaobank.repository.db;

import com.kakaobank.service.CommentsAnalyzerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteManager {

    private final static Logger log = LogManager.getLogger(SQLiteManager.class);

    private static final String SQLITE_JDBC_DRIVER = "org.sqlite.JDBC";

    private static final String SQLITE_FILE_DB_URL_PREFIX = "jdbc:sqlite:src/main/resources/";
    private static final String SQLITE_FILE_DB_URL = "jdbc:sqlite:src/main/resources/schools.db";


    private static final boolean OPT_AUTO_COMMIT = false;
    private static final int OPT_VALID_TIMEOUT = 500;


    // Database 접속정보
    private Connection conn = null;
    private String driver = null;
    private String url = null;


    public SQLiteManager(){
        this(SQLITE_FILE_DB_URL);
    }
    public SQLiteManager(String dbFileName) {
        // JDBC Driver 설정
        this.driver = SQLITE_JDBC_DRIVER;
        this.url = SQLITE_FILE_DB_URL_PREFIX + dbFileName;
    }


    public Connection createConnection() {
        try {
            Class.forName(this.driver);
            this.conn = DriverManager.getConnection(this.url);

            // 로그 출력

            this.conn.setAutoCommit(OPT_AUTO_COMMIT);

        } catch (ClassNotFoundException | SQLException e) {
            log.error("create connection error", e);
        }

        return this.conn;
    }


    public void closeConnection() {
        try {
            if( this.conn != null ) {
                this.conn.close();
            }
        } catch (SQLException e) {
            log.error("close connection error", e);
        } finally {
            this.conn = null;
        }
    }

    // DB 재연결 함수
    public Connection ensureConnection() {
        try {
            if( this.conn == null || this.conn.isValid(OPT_VALID_TIMEOUT) ) {
                closeConnection();      // 연결 종료
                createConnection();     // 연결
            }
        } catch (SQLException e) {
            log.error("ensure connection error", e);
        }

        return this.conn;
    }

    // DB 연결 객체 가져오기
    public Connection getConnection() {
        return this.conn;
    }
}
