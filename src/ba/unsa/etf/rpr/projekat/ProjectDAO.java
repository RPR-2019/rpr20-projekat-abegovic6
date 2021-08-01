package ba.unsa.etf.rpr.projekat;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.Scanner;

public class ProjectDAO {
    private static ProjectDAO instance = null;
    private static Connection connection;

    private PreparedStatement getAllNotesStatement;




    private ProjectDAO() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:projectdatabase.db");
            returnToDefault();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        try {
            getAllNotesStatement = connection.prepareStatement("SELECT * FROM notes");
        } catch (SQLException throwables) {
            createDatabase();
            try {
                getAllNotesStatement = connection.prepareStatement("SELECT * FROM notes");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public static ProjectDAO getInstance() {
        if(instance == null) instance = new ProjectDAO();
        return instance;
    }

    public static void removeInstance() {
        if (instance != null) {
            try {
                instance.connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        instance = null;
    }

    private void returnToDefault() {

    }

    private void createDatabase() {
        Scanner input = null;
        try {
            input = new Scanner(new FileInputStream("projectdatabase.db.sql"));
            String sqlStatement = "";
            while (input.hasNext()) {
                sqlStatement += input.nextLine();
                if ( sqlStatement.length() > 1 && sqlStatement.charAt( sqlStatement.length()-1 ) == ';') {
                    try {
                        Statement stmt = connection.createStatement();
                        stmt.execute(sqlStatement);
                        sqlStatement = "";
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            input.close();
        } catch (FileNotFoundException e) {
            System.out.println("The SQL database doesn't exist.. Continuing with empty database");
        }

    }




}
