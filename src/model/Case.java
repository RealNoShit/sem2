package model;

public class Case {

    private final int caseID;
    private final String deceasedName;
    private final String customerName;

    /**
     * Creates a Case.
     */
    public Case(int caseID, String deceasedName, String customerName) {
        this.caseID = caseID;
        this.deceasedName = deceasedName;
        this.customerName = customerName;
    }

    public int getCaseID() {
        return caseID;
    }

    public String getDeceasedName() {
        return deceasedName;
    }

    public String getCustomerName() {
        return customerName;
    }

    @Override
    public String toString() {
        return "Case{" +
                "caseID=" + caseID +
                ", deceasedName='" + deceasedName + '\'' +
                ", customerName='" + customerName + '\'' +
                '}';
    }
}
