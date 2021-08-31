package ba.unsa.etf.rpr.projekat;

import javafx.application.HostServices;

public class MyHostServices {
    private static HostServices hostServices;

    public static HostServices getHostServices () {
        return hostServices;
    }

    public static void setHostServices (HostServices hostServices) {
        MyHostServices.hostServices = hostServices;
    }
}
