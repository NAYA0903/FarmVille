package Store;

import java.util.Scanner;

public abstract class Storestart {
    protected String loggedInID;

    public Storestart(String loggedInID) {
        this.loggedInID = loggedInID;
    }

    public abstract void start(Scanner sc);
}
