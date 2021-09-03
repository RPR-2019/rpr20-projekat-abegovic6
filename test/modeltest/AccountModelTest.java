package modeltest;

import ba.unsa.etf.rpr.projekat.ProjectDAO;
import ba.unsa.etf.rpr.projekat.javabean.Account;
import ba.unsa.etf.rpr.projekat.model.AccountModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AccountModelTest {
    private AccountModel accountModel;

    @BeforeAll
    void setAccountModel() {
        accountModel = ProjectDAO.getInstance ().getAccountModel ();
    }

    @Test
    void accountIsCreating() {
        //Account account = new Account ("firstname", "lastname", )

    }



}
