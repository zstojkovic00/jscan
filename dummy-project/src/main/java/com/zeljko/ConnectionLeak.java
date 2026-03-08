package com.zeljko;

import java.sql.*;

public class ConnectionLeak {
    public static final String QUERY = "SELECT * FROM customer_all WHERE id = ?";
    private Connection conn;


    // LEAK - resursi nisu zatvoreni na kraju metode
    public void leakNoClose(int customerId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(QUERY);
        ps.setInt(1, customerId);
        ResultSet rs = ps.executeQuery();
    }

    // SAFE - try-with-resources automatski zatvara ps
    public void safeTryWithResources(int customerId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(QUERY)) {
            ps.setInt(1, customerId);
            ps.executeQuery();
        }
    }

    // LEAK - ako se desi exception pre closeHelper, resursi cure
    public void leakCloseHelperWithoutFinally(int customerId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(QUERY);
        ResultSet rs = ps.executeQuery();
        ps.setInt(1, customerId);
        closeHelper(ps, rs);
    }

    // SAFE - closeHelper je u finally
    public void safeCloseHelperInFinally(int customerId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(QUERY);
        ResultSet rs = null;
        try {
            ps.setInt(1, customerId);
            rs = ps.executeQuery();
        } finally {
            closeHelper(ps, rs);
        }
    }

    public void closeHelper(PreparedStatement ps, ResultSet rs) throws SQLException {
        ps.close();
        rs.close();
    }
}
