package db;

import model.Case;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CaseDAO implements CaseDAOIF {

    private final Connection connection;

    public CaseDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Case findCaseByID(int caseID) {
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
                return null;
            }

        } catch (SQLException e) {
            throw new DataAccessException(DBMessages.QUERY_FAILED, e);
        }
    }

    private Case buildObject(ResultSet rs) throws SQLException {
        int id = rs.getInt("caseID");
        String deceasedName = rs.getString("deceasedName");
        String customerName = rs.getString("customerName");
        return new Case(id, deceasedName, customerName);
    }
}
