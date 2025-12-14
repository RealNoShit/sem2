package db;

/**
 * Central place for DB-related messages.
 * You can expand this or hook it up to a ResourceBundle later.
 */
public final class DBMessages {

    private DBMessages() {
    }

    public static final String CONNECTION_FAILED =
            "Could not connect to the database.";

    public static final String QUERY_FAILED =
            "An error occurred while executing a database query.";

    public static final String UPDATE_FAILED =
            "An error occurred while updating the database.";
}
