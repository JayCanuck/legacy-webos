/*
 * ThemeBuilderApp.java
 */

package ca.canucksoftware.clockthemebuilder;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class ThemeBuilderApp extends SingleFrameApplication {
    public static String[] args;
    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        ThemeBuilderView app = new ThemeBuilderView(this);
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
     * @return the instance of ThemeBuilderApp
     */
    public static ThemeBuilderApp getApplication() {
        return Application.getInstance(ThemeBuilderApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        if(args!=null && args.length>0) {
            ThemeBuilderApp.args = args;
        }
        launch(ThemeBuilderApp.class, args);
    }
}
