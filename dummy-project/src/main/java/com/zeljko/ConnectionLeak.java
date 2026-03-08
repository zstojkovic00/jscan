package com.zeljko;

import java.sql.*;

public class ConnectionLeak {
    public static final String QUERY = "SELECT * FROM customer_all WHERE id = ?";
    private Connection conn;


    public void findCustomerById(int customerId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(QUERY);
        ps.setInt(1, customerId);
        ResultSet rs = ps.executeQuery();
        // leak - nema close()
    }

    public void findCustomerByIdSafe(int customerId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(QUERY)) {
            ps.setInt(1, customerId);
            ps.executeQuery();
        }
    }

    public void findCustomerBySafe2(int customerId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(QUERY);
        ps.setInt(1, customerId);
        ResultSet rs = ps.executeQuery();
        closeHelper(ps, rs);
    }

    public void closeHelper(PreparedStatement ps, ResultSet rs) throws SQLException {
        ps.close();
        rs.close();
    }
}
