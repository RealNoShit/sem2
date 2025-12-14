package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CaseDAO implements CaseDAOIF {

    private final Connection connection;

    /**
     * Creates a CaseDAO.
     */
    public CaseDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Case findCaseByID(int caseID) {
        // Adjust table/column names to match your actual schema
        final String sql =
                "SELECT caseID, deceasedName, customerName " +
                "FROM Cases " +
                "WHERE caseID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, caseID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return buildObject(rs);
                }
            }
        } catch (SQLException e) {
            // You can change this to your own exception handling strategy
            throw new RuntimeException("Error finding case with id " + caseID, e);
        }

        return null;
    }

    /**
     * Builds a Case object from the current row of the given ResultSet.
     */
    public Case buildObject(ResultSet rs) throws SQLException {
        int id = rs.getInt("caseID");
        String deceasedName = rs.getString("deceasedName");
        String customerName = rs.getString("customerName");
        return new Case(id, deceasedName, customerName);
    }
}
