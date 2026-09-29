package gui;

import javafx.application.Application;

/**
 * A plain (non-Application) main class. JavaFX raises "missing runtime
 * components" if the class launched directly from the classpath is itself
 * an Application subclass; a separate launcher avoids that without any
 * module-path configuration.
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(App.class, args);
    }
}