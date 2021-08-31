package ba.unsa.etf.rpr.projekat.dao;

import ba.unsa.etf.rpr.projekat.model.Account;
import ba.unsa.etf.rpr.projekat.model.Label;
import ba.unsa.etf.rpr.projekat.model.LabelColor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class LabelDAO {
    private static LabelDAO instance;

    private final PreparedStatement createLabelStatement;
    private final PreparedStatement getAllLabelsForAccountStatement;
    private final PreparedStatement deleteLabelStatement;
    private final PreparedStatement updateLabelStatement;
    private final PreparedStatement getNewIdLabelStatement;
    private final PreparedStatement getLabelFromIdStatement;

    private final PreparedStatement getAllLabelsIdForNoteStatement;
    private final PreparedStatement deleteLabelIdStatement;


    private LabelDAO (Connection conn) throws SQLException {

        createLabelStatement = conn.prepareStatement("INSERT INTO label (id, accountId, labelName, " +
                "description, labelColor) VALUES (?, ?, ?, ?, ?)");
        getAllLabelsForAccountStatement = conn.prepareStatement("SELECT * FROM label WHERE accountId = ?");
        getLabelFromIdStatement = conn.prepareStatement("SELECT * FROM label WHERE id = ?");
        updateLabelStatement = conn.prepareStatement("UPDATE label SET (labelName, description, labelColor) " +
                "= (?,?,?) WHERE id = ?");
        deleteLabelStatement = conn.prepareStatement("DELETE FROM label WHERE id = ?");
        getNewIdLabelStatement = conn.prepareStatement("SELECT MAX(id) + 1 FROM label");

        getAllLabelsIdForNoteStatement = conn.prepareStatement("SELECT labelId FROM intertable WHERE noteId = ?");
        deleteLabelIdStatement = conn.prepareStatement("DELETE FROM intertable WHERE labelId = ?");
    }

    public static LabelDAO getInstance(Connection conn) throws SQLException {
        if(instance == null) instance = new LabelDAO (conn);
        return instance;
    }

    private LabelColor stringToLabelColor(String string) {
        return LabelColor.valueOf(string);
    }

    private Label getLabelFromResultSet(ResultSet resultSetLabel) {
        try {
            Label label = new Label();
            label.setId(resultSetLabel.getInt(1));
            label.setAccountId(resultSetLabel.getInt(2));
            label.setLabelName(resultSetLabel.getString(3));
            label.setDescription(resultSetLabel.getString(4));
            label.setLabelColor(stringToLabelColor(resultSetLabel.getString(5)));

            return label;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    private Label getLabelFromId(int labelId) {
        try {
            getLabelFromIdStatement.setInt(1, labelId);
            ResultSet resultSet = getLabelFromIdStatement.executeQuery();
            if(resultSet.next())
                return getLabelFromResultSet(resultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

    public List<Label> getAllLabelsForAccount(Account account) {
        try {
            getAllLabelsForAccountStatement.setInt(1, account.getId());
            return getLabelListFromResultSet(getAllLabelsForAccountStatement.executeQuery());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Collections.emptyList ();
    }

    private List<Label> getLabelListFromResultSet(ResultSet resultSetLabel) {
        try {
            List<Label> labels = new ArrayList<> ();
            while(resultSetLabel.next()) {
                labels.add(getLabelFromResultSet(resultSetLabel));
            }
            return labels;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Collections.emptyList();
    }

    public List<Label> getLabelListForNote (int id) {
        try {
            List<Label> labels = new ArrayList<> ();

            getAllLabelsIdForNoteStatement.setInt (1, id);
            ResultSet resultSet = getAllLabelsIdForNoteStatement.executeQuery ();
            while (resultSet.next ()) {
                labels.add (getLabelFromId (resultSet.getInt (1)));
            }
            return labels;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Collections.emptyList();
    }

    public boolean createLabel(Label label) {
        try {
            ResultSet resultSet = getNewIdLabelStatement.executeQuery();
            if(resultSet.next()) {
                label.setId(resultSet.getInt(1));
            } else {
                label.setId(1);
            }
            createLabelStatement.setInt(1, label.getId());
            createLabelStatement.setInt(2, label.getAccountId());
            createLabelStatement.setString(3, label.getLabelName());
            createLabelStatement.setString(4, label.getDescription());
            createLabelStatement.setString(5, label.getLabelColor().name());

            createLabelStatement.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }

        return true;
    }

    public void updateLabel(Label label) {

        try {
            updateLabelStatement.setInt(4, label.getId());
            updateLabelStatement.setString(1, label.getLabelName());
            updateLabelStatement.setString(2, label.getDescription());
            updateLabelStatement.setString(3, label.getLabelColor().name());
            updateLabelStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void deleteLabel(int id) {
        try {
            deleteLabelStatement.setInt (1, id);
            deleteLabelStatement.executeUpdate ();
            deleteLabelIdStatement.setInt (1, id);
            deleteLabelIdStatement.executeUpdate ();
        } catch (SQLException throwables) {
            throwables.printStackTrace ();
        }

    }


}
