package ba.unsa.etf.rpr.projekat.utilities;

import ba.unsa.etf.rpr.projekat.dal.AccountModel;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

public class PrintReport extends JFrame {
    public void showReport(Connection conn, String file) throws JRException {
        String reportSrcFile = getClass().getResource("/reports/" + file + ".jrxml").getFile();
        String reportsDir = getClass().getResource("/reports/").getFile();

        JasperReport jasperReport = JasperCompileManager.compileReport(reportSrcFile);
        // Fields for resources path
        HashMap<String, Object> parameters = new HashMap<> ();
        parameters.put("reportsDirPath", reportsDir);
        parameters.put ("AccountID", AccountModel.getCurrentUser ().getId ());
        parameters.put ("REPORT_RESOURCE_BUNDLE", MyResourceBundle.getResourceBundle ());
        ArrayList<HashMap<String, Object>> list = new ArrayList<> ();
        list.add(parameters);
        JasperPrint print = JasperFillManager.fillReport(jasperReport, parameters, conn);
        JasperViewer viewer = new JasperViewer(print, false);
        viewer.setVisible(true);
    }
}

