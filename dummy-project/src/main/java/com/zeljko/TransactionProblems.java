package com.zeljko;

import java.sql.*;

public class TransactionProblems {

    public static final String QUERY = "SELECT * FROM customer_all WHERE id = ?";

    // PROBLEM - nema ni commit ni rollback
    public void problemNoCommitNoRollback(Connection conn, int customerId) throws SQLException {
        conn.setAutoCommit(false);
        PreparedStatement ps = conn.prepareStatement(QUERY);
        ps.setInt(1, customerId);
        ps.executeUpdate();
        ps.close();
    }

    // PROBLEM - ima rollback ali nema commit
    public void problemNoCommit(Connection conn, int customerId) throws SQLException {
        conn.setAutoCommit(false);
        try {
            PreparedStatement ps = conn.prepareStatement(QUERY);
            ps.setInt(1, customerId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            conn.rollback();
        }
    }

    // PROBLEM - ima commit ali nema rollback
    public void problemNoRollback(Connection conn, int customerId) throws SQLException {
        conn.setAutoCommit(false);
        PreparedStatement ps = conn.prepareStatement(QUERY);
        ps.setInt(1, customerId);
        ps.executeUpdate();
        ps.close();
        conn.commit();
    }

    // SAFE - ima i commit i rollback
    public void safeFullTransaction(Connection conn, int customerId) throws SQLException {
        conn.setAutoCommit(false);
        try {
            PreparedStatement ps = conn.prepareStatement(QUERY);
            ps.setInt(1, customerId);
            ps.executeUpdate();
            ps.close();
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    }
}
