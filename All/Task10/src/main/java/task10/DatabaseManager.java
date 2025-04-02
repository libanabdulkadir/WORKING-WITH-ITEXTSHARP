package task10;

import java.sql.*;

class DatabaseManager {
    private Connection conn;

    public DatabaseManager() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/payment_db", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertData(String receipt, String student, String paymentMethod, String course, String amount) {
        String query = "INSERT INTO payments (receipt_no, student_name, payment_method, course_token, amount) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, receipt);
            ps.setString(2, student);
            ps.setString(3, paymentMethod);
            ps.setString(4, course);
            ps.setString(5, amount);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet getData() {
        String query = "SELECT * FROM payments";
        try {
            Statement stmt = conn.createStatement();
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Close the connection when no longer needed
    public void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
