package db;
import model.*;

public interface CaseDAOIF {

    /**
     * Finds a case by its ID.
     *
     * @param caseID ID of the case
     * @return the Case, or null if not found
     */
    Case findCaseByID(int caseID);
}
