package ba.unsa.etf.rpr.projekat;

public class Main {

    public static void main(String[] args) {
        ProjectDAO projectDAO = ProjectDAO.getInstance();
        ProjectDAO.removeInstance();
    }
}
