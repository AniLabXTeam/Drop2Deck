package com.crazyxacker.apps.drop2deck;

import com.crazyxacker.apps.drop2deck.enums.Platform;
import com.crazyxacker.apps.drop2deck.jna.DwmAttribute;
import com.crazyxacker.apps.drop2deck.jna.StageOps;
import com.crazyxacker.apps.drop2deck.util.FXUtils;
import com.crazyxacker.apps.drop2deck.util.PlatformUtils;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.prefs.Preferences;

public class FXApplication extends Application {
    private static final String APP_NAME = "Drop2Deck";
    public static Platform CURRENT_PLATFORM;
    public static Preferences PREFERENCES;
    public static HostServices HOST_SERVICES;

    @Override
    public void init() {
        // Detect on which OS app is launching
        CURRENT_PLATFORM = PlatformUtils.detectPlatform();

        // Load preferences
        PREFERENCES = Preferences.userRoot();

        // Get host services
        HOST_SERVICES = getHostServices();
    }

    @Override
    public void start(Stage primaryStage) {
        // Load main FXML file
        Parent parent = FXUtils.loadFXMLAndGetParent("/fxml/Main.fxml");

        // Create scene
        Scene scene = new Scene(parent);
        FXUtils.addDefaultStylesheet(scene);

        // Configure stage
        FXUtils.setTaskbarAppIcon(primaryStage, FXUtils.createAppIconImage());
        primaryStage.setTitle(APP_NAME);
        primaryStage.setWidth(1280);
        primaryStage.setHeight(800);
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(400);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Enable dark window style and Mica material on Windows
        manipulateWithNativeWindow();
    }

    private void manipulateWithNativeWindow() {
        if (CURRENT_PLATFORM == Platform.WINDOWS) {
            // Enable Mica material and dark mode
            StageOps.WindowHandle handle = new StageOps.WindowHandle(APP_NAME);

            // Enable the dark mode
            StageOps.dwmSetBooleanValue(handle, DwmAttribute.DWMWA_USE_IMMERSIVE_DARK_MODE, true);

            // Enable the Mica material
            if (!StageOps.dwmSetIntValue(handle, DwmAttribute.DWMWA_SYSTEMBACKDROP_TYPE, DwmAttribute.DWMSBT_MAINWINDOW.value)) {
                StageOps.dwmSetBooleanValue(handle, DwmAttribute.DWMWA_MICA_EFFECT, true); // This is the "old" way
            }
        }
    }
}
