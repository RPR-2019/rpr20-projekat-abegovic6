package ba.unsa.etf.rpr.projekat;

import ba.unsa.etf.rpr.projekat.database.ProjectDAO;

public class Main {

    public static void main(String[] args) {
        ProjectDAO projectDAO = ProjectDAO.getInstance();
        ProjectDAO.removeInstance();
    }
}
