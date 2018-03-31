/*
 * WebOSThemeBuilderApp.java
 */

package ca.canucksoftware.themebuilder;

import java.io.File;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class WebOSThemeBuilderApp extends SingleFrameApplication {

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        MainView app = new MainView(this);
        app.getFrame().setResizable(false);
        show(app);
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of WebOSThemeBuilderApp
     */
    public static WebOSThemeBuilderApp getApplication() {
        return Application.getInstance(WebOSThemeBuilderApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(WebOSThemeBuilderApp.class, args);
    }
}
