package Farm;

import java.util.Scanner;

public abstract class FarmStart {
    protected String loggedInID;

    public FarmStart(String loggedInID) {
        this.loggedInID = loggedInID;
    }

    public abstract void start(Scanner sc);
}
