package dao;

import connectDB.JDBCConnection;
import java.sql.ResultSet;
import entities.ThongKe;
import entities.ThongKeTheoNam;
import entities.ThongKeTheoThang;
import java.util.ArrayList;
import java.util.List;

public class ThongKeDAO {

    private final String SELECT_7_DAYS_AGO = """
        WITH dates AS (
          SELECT CURRENT_DATE - i AS date
          FROM generate_series(0, 6) AS s(i)
        )
        SELECT 
          d.date AS ngay,
          COALESCE(SUM(cthd.soLuong * t.donGia), 0) AS doanhthu,
          COALESCE(SUM(cthd.soLuong * t.giaNhap), 0) AS chiphi
        FROM dates d
        LEFT JOIN HoaDon hd ON DATE(hd.thoiGian) = d.date
        LEFT JOIN ChiTietHoaDon cthd ON cthd.idHD = hd.idHD
        LEFT JOIN Thuoc t ON t.idThuoc = cthd.idThuoc
        GROUP BY d.date
        ORDER BY d.date;
    """;

    private final String SELECT_FROM_YEAR_TO_YEAR = """
        SELECT 
            y.year AS nam,
            COALESCE(SUM(cthd.soLuong * t.donGia), 0) AS doanhthu,
            COALESCE(SUM(cthd.soLuong * t.giaNhap), 0) AS chiphi
        FROM (
            SELECT generate_series(?::int, ?::int) AS year
        ) y
        LEFT JOIN HoaDon hd ON EXTRACT(YEAR FROM hd.thoiGian)::int = y.year
        LEFT JOIN ChiTietHoaDon cthd ON cthd.idHD = hd.idHD
        LEFT JOIN Thuoc t ON t.idThuoc = cthd.idThuoc
        GROUP BY y.year
        ORDER BY y.year;
    """;

    private final String SELECT_MOUNTH_BY_YEAR = """
        SELECT 
            m AS thang,
            COALESCE(SUM(cthd.soLuong * t.donGia), 0) AS doanhthu,
            COALESCE(SUM(cthd.soLuong * t.giaNhap), 0) AS chiphi
        FROM generate_series(1, 12) AS m
        LEFT JOIN HoaDon hd ON EXTRACT(MONTH FROM hd.thoiGian)::int = m AND EXTRACT(YEAR FROM hd.thoiGian)::int = ?
        LEFT JOIN ChiTietHoaDon cthd ON cthd.idHD = hd.idHD
        LEFT JOIN Thuoc t ON t.idThuoc = cthd.idThuoc
        GROUP BY m
        ORDER BY m;
    """;

    private final String SELECT_DAYS_BY_MONTH_YEAR = """
        SELECT 
            d AS ngay,
            COALESCE(SUM(cthd.soLuong * t.donGia), 0) AS doanhthu,
            COALESCE(SUM(cthd.soLuong * t.giaNhap), 0) AS chiphi
        FROM generate_series(
            make_date(?, ?, 1),
            (make_date(?, ?, 1) + INTERVAL '1 MONTH - 1 day')::date,
            INTERVAL '1 day'
        ) AS d
        LEFT JOIN HoaDon hd ON DATE(hd.thoiGian) = d
        LEFT JOIN ChiTietHoaDon cthd ON cthd.idHD = hd.idHD
        LEFT JOIN Thuoc t ON t.idThuoc = cthd.idThuoc
        GROUP BY d
        ORDER BY d;
    """;

    public List<ThongKe> select7DaysAgo() {
        return this.selectBySql(SELECT_7_DAYS_AGO);
    }

    public List<ThongKe> selectDaysByMonthYear(int month, int year) {
        return this.selectBySql(SELECT_DAYS_BY_MONTH_YEAR, year, month, year, month);
    }

    public List<ThongKeTheoNam> selectFromYearToYear(int fromYear, int toYear) {
        return this.selectBySqlTheoNam(SELECT_FROM_YEAR_TO_YEAR, fromYear, toYear);
    }

    public List<ThongKeTheoThang> selectMounthsByYear(int year) {
        return this.selectBySqlTheoThang(SELECT_MOUNTH_BY_YEAR, year);
    }

    protected List<ThongKe> selectBySql(String sql, Object... args) {
        List<ThongKe> listE = new ArrayList<>();
        try {
            ResultSet rs = JDBCConnection.query(sql, args);
            while (rs.next()) {
                ThongKe e = new ThongKe();
                e.setThoiGian(rs.getDate("ngay"));
                e.setDoanhThu(rs.getDouble("doanhthu"));
                e.setChiPhi(rs.getDouble("chiphi"));
                listE.add(e);
            }
            rs.getStatement().getConnection().close();
            return listE;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected List<ThongKeTheoNam> selectBySqlTheoNam(String sql, Object... args) {
        List<ThongKeTheoNam> listE = new ArrayList<>();
        try {
            ResultSet rs = JDBCConnection.query(sql, args);
            while (rs.next()) {
                ThongKeTheoNam e = new ThongKeTheoNam();
                e.setNam(rs.getInt("nam"));
                e.setDoanhThu(rs.getDouble("doanhthu"));
                e.setChiPhi(rs.getDouble("chiphi"));
                listE.add(e);
            }
            rs.getStatement().getConnection().close();
            return listE;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected List<ThongKeTheoThang> selectBySqlTheoThang(String sql, Object... args) {
        List<ThongKeTheoThang> listE = new ArrayList<>();
        try {
            ResultSet rs = JDBCConnection.query(sql, args);
            while (rs.next()) {
                ThongKeTheoThang e = new ThongKeTheoThang();
                e.setThang(rs.getInt("thang"));
                e.setDoanhThu(rs.getDouble("doanhthu"));
                e.setChiPhi(rs.getDouble("chiphi"));
                listE.add(e);
            }
            rs.getStatement().getConnection().close();
            return listE;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
