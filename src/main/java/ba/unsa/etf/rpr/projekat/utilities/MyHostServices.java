package ba.unsa.etf.rpr.projekat.utilities;

import javafx.application.HostServices;

public class MyHostServices {
    private static HostServices hostServices;

    private MyHostServices () {
    }

    public static HostServices getHostServices () {
        return hostServices;
    }

    public static void setHostServices (HostServices hostServices) {
        MyHostServices.hostServices = hostServices;
    }
}
