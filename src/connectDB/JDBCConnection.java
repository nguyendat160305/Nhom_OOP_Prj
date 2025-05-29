package connectDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

public class JDBCConnection {
	// PostgreSQL connection details
	static String url = "jdbc:postgresql://localhost:5432/QuanLyNhaThuoc";
	static String user = "postgres";
	static String password = "admin";

	public static PreparedStatement getStmt(String sql, Object... args) throws Exception {
		Connection con = DriverManager.getConnection(url, user, password);
		PreparedStatement stmt;
		if (sql.trim().startsWith("{")) {
			stmt = con.prepareCall(sql);
		} else {
			stmt = con.prepareStatement(sql);
		}

		for (int i = 0; i < args.length; i++) {
			Object arg = args[i];
			if (arg instanceof java.util.Date) {
				java.util.Date utilDate = (java.util.Date) arg;
				java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(utilDate.getTime());
				stmt.setObject(i + 1, sqlTimestamp, Types.TIMESTAMP);
			} else {
				stmt.setObject(i + 1, arg);
			}
		}
		return stmt;
	}

	public static int update(String sql, Object... args) {
		try {
			PreparedStatement stmt = JDBCConnection.getStmt(sql, args);
			try {
				return stmt.executeUpdate();
			} finally {
				stmt.getConnection().close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static ResultSet query(String sql, Object... args) throws Exception {
		PreparedStatement stmt = JDBCConnection.getStmt(sql, args);
		return stmt.executeQuery();
	}
}