package ctrl;
import model.*;
import db.*;

public class CaseController {

    private final CaseDAOIF caseDAO;

    /**
     * Creates a CaseController.
     */
    public CaseController(CaseDAOIF caseDAO) {
        this.caseDAO = caseDAO;
    }

    /**
     * Finds a Case by its ID.
     */
    public Case findCaseByID(int caseID) {
        return caseDAO.findCaseByID(caseID);
    }
}

