package com.crazyxacker.apps.drop2deck.controller;

import com.crazyxacker.apps.drop2deck.FXApplication;
import com.crazyxacker.apps.drop2deck.enums.Platform;
import com.crazyxacker.apps.drop2deck.ftp.FTPServer;
import com.crazyxacker.apps.drop2deck.network.StorLoggingFtplet;
import com.crazyxacker.apps.drop2deck.skin.VisiblePasswordFieldSkin;
import com.crazyxacker.apps.drop2deck.util.FXUtils;
import com.crazyxacker.apps.drop2deck.util.NetworkUtils;
import com.crazyxacker.apps.drop2deck.util.QRCodeUtils;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;

import static com.crazyxacker.apps.drop2deck.FXApplication.PREFERENCES;

public class MainController {
    private static final String DEFAULT_BUTTON_STYLE = "-fx-background-radius: 60; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: ";
    private static final String STARTED_COLOR = "#c0392b;";
    private static final String STOPPED_COLOR = "#0b82fb;";

    private static final String STARTED_ICON = "/icons/round_stop_circle_48.png";
    private static final String STOPPED_ICON = "/icons/round_play_circle_48.png";

    private static final String NO_DATA = "No data transfer...";

    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";
    private static final String PREF_PORT_INTERNAL = "port_internal";
    private static final String PREF_PORT_EXTERNAL = "port_external";

    private static final String FTP_URL_TEMPLATE = "ftp://%s:%s@%s:%s";

    private static final String DEFAULT_USERNAME = "deck";
    private static final String DEFAULT_PASSWORD = "deck";
    private static final int DEFAULT_PORT_INTERNAL = 9001;
    private static final int DEFAULT_PORT_EXTERNAL = 9002;

    private static final String GIT_URL = "https://github.com/CrazyXacker/Drop2Deck";

    // Root node
    @FXML
    private StackPane root;

    // Server config
    @FXML
    private VBox vbConfig;
    @FXML
    private TextField tfUsername;
    @FXML
    private PasswordField tfPassword;
    @FXML
    private Button btnShowHidePassword;
    @FXML
    private FontIcon fiShowHidePassword;
    @FXML
    private TextField tfPortInternal;
    @FXML
    private TextField tfPortExternal;

    // Start/Stop server button
    @FXML
    private JFXButton btnStartStop;
    @FXML
    public ImageView ivStartStop;

    // Started server details
    @FXML
    private GridPane gpDetails;

    // QR
    @FXML
    private ImageView ivQRInternal;
    @FXML
    private ImageView ivQRExternal;

    // FTP urls
    @FXML
    private TextField tfInternalUrl;
    @FXML
    private TextField tfExternalUrl;

    // Logs
    @FXML
    private Label lblInternalLogs;
    @FXML
    private Label lblExternalLogs;

    // Other buttons
    @FXML
    private Button btnUsername;
    @FXML
    private Button btnPassword;
    @FXML
    private Button btnInternalMemory;
    @FXML
    private Button btnExternalMemory;
    @FXML
    private Button btnCopyInternal;
    @FXML
    private Button btnCopyExternal;

    private FTPServer ftpServerInternal;
    private FTPServer ftpServerExternal;

    private final BooleanProperty serverStartedProperty = new SimpleBooleanProperty();

    private final Function<Label, StorLoggingFtplet.StorCallback> storCallbackFunction = label -> new StorLoggingFtplet.StorCallback() {
        @Override
        public void onStart(String fileName) {
            javafx.application.Platform.runLater(() -> lblInternalLogs.setText("Saving: " + fileName));
        }

        @Override
        public void onEnd(String fileName) {
            javafx.application.Platform.runLater(() -> lblInternalLogs.setText("Saved: " + fileName));
        }
    };

    @FXML
    public void initialize() {
        FXUtils.numericOnlyTextField(tfPortInternal);
        FXUtils.numericOnlyTextField(tfPortExternal);

        setPasswordFieldSkin();
        bindVisibility();
        bindTextFieldsInputChange();
        roundQRCorners();
        createOnServerStateChangeListener();
        fixButtonHeightOnWindows();

        loadDataFromPreferences();
    }

    private void loadDataFromPreferences() {
        tfUsername.setText(PREFERENCES.get(PREF_USERNAME, DEFAULT_USERNAME));
        tfPassword.setText(PREFERENCES.get(PREF_PASSWORD, DEFAULT_PASSWORD));
        tfPortInternal.setText(String.valueOf(PREFERENCES.getInt(PREF_PORT_INTERNAL, DEFAULT_PORT_INTERNAL)));
        tfPortExternal.setText(String.valueOf(PREFERENCES.getInt(PREF_PORT_EXTERNAL, DEFAULT_PORT_EXTERNAL)));
    }

    private void setPasswordFieldSkin() {
        tfPassword.setSkin(new VisiblePasswordFieldSkin(tfPassword, btnShowHidePassword, fiShowHidePassword));
    }

    private void bindVisibility() {
        vbConfig.visibleProperty().bind(serverStartedProperty.not());
        vbConfig.managedProperty().bind(vbConfig.visibleProperty());

        gpDetails.visibleProperty().bind(serverStartedProperty);
        gpDetails.managedProperty().bind(gpDetails.visibleProperty());
    }

    private void bindTextFieldsInputChange() {
        tfUsername.textProperty().addListener((observable, oldValue, newValue) -> PREFERENCES.put(PREF_USERNAME, newValue));
        tfPassword.textProperty().addListener((observable, oldValue, newValue) -> PREFERENCES.put(PREF_PASSWORD, newValue));
        tfPortInternal.textProperty().addListener((observable, oldValue, newValue) -> PREFERENCES.putInt(PREF_PORT_INTERNAL, Integer.parseInt(newValue)));
        tfPortExternal.textProperty().addListener((observable, oldValue, newValue) -> PREFERENCES.putInt(PREF_PORT_EXTERNAL, Integer.parseInt(newValue)));
    }

    private void roundQRCorners() {
        FXUtils.clipRoundedCorners(ivQRInternal, 300, 300, 22, 22);
        FXUtils.clipRoundedCorners(ivQRExternal, 300, 300, 22, 22);
    }

    private void createOnServerStateChangeListener() {
        serverStartedProperty.addListener((observable, oldValue, started) -> {
            ivStartStop.setImage(new Image(started ? STARTED_ICON : STOPPED_ICON));
            btnStartStop.setStyle(DEFAULT_BUTTON_STYLE + (started ? STARTED_COLOR : STOPPED_COLOR));
            lblInternalLogs.setText(NO_DATA);
            lblExternalLogs.setText(NO_DATA);

            fillFtpServerInfo();
        });
    }

    private void fixButtonHeightOnWindows() {
        if (FXApplication.CURRENT_PLATFORM == Platform.WINDOWS) {
            btnUsername.setMinHeight(32);
            btnPassword.setMinHeight(32);
            btnInternalMemory.setMinHeight(32);
            btnExternalMemory.setMinHeight(32);
            btnCopyInternal.setMinHeight(32);
            btnCopyExternal.setMinHeight(32);
            btnShowHidePassword.setMinHeight(32);
        }
    }

    private void fillFtpServerInfo() {
        Optional.of(NetworkUtils.getLocalIPs())
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0))
                .ifPresent(ip -> {
                    String internalUrl = String.format(FTP_URL_TEMPLATE, tfUsername.getText(), tfPassword.getText(), ip, tfPortInternal.getText());
                    String externalUrl = String.format(FTP_URL_TEMPLATE, tfUsername.getText(), tfPassword.getText(), ip, tfPortExternal.getText());

                    tfInternalUrl.setText(internalUrl);
                    tfExternalUrl.setText(externalUrl);

                    ivQRInternal.setImage(null);
                    ivQRExternal.setImage(null);

                    ivQRInternal.setImage(new Image(QRCodeUtils.createQRCodeUrl(internalUrl), true));
                    ivQRExternal.setImage(new Image(QRCodeUtils.createQRCodeUrl(externalUrl), true));
                });
    }

    @FXML
    private void startStopServer() {
        try {
            if (ftpServerInternal == null && ftpServerExternal == null) {
                ftpServerInternal = createServer(
                        lblInternalLogs,
                        FXApplication.CURRENT_PLATFORM.getInternalPath(),
                        tfPortInternal.getText()
                );
                ftpServerExternal = createServer(
                        lblExternalLogs,
                        FXApplication.CURRENT_PLATFORM.getExternalPath(),
                        tfPortExternal.getText()
                );

                ftpServerInternal.start();
                ftpServerExternal.start();
            } else {
                destroyServers();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showErrorDialog(ex.getMessage());
        }

        serverStartedProperty.set(ftpServerInternal != null && ftpServerExternal != null);
    }

    private FTPServer createServer(Label lblLogs, String path, String port) throws IOException {
        return FTPServer.create(
                new HashMap<>(){{
                    put(lblLogs.getId(), new StorLoggingFtplet(storCallbackFunction.apply(lblLogs)));
                }},
                path,
                tfUsername.getText(),
                tfPassword.getText(),
                Integer.parseInt(port)
        );
    }

    @FXML
    private void copyInternalUrl() {
        FXUtils.copyToClipboard(tfInternalUrl.getText());
    }

    @FXML
    private void copyExternalUrl() {
        FXUtils.copyToClipboard(tfExternalUrl.getText());
    }

    @FXML
    private void showGit() {
        FXApplication.HOST_SERVICES.showDocument(GIT_URL);
    }

    @FXML
    private void exit() {
        Optional.of(root)
                .map(Node::getScene)
                .map(Scene::getWindow)
                .map(Stage.class::cast)
                .ifPresent(Stage::close);
    }

    private void showErrorDialog(String reason) {
        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setStyle("-fx-background-color: #28282e");
        layout.setHeading(new Label("Error"));
        layout.setBody(new Label("Unable to start server!\n\nReason: " + reason));

        new JFXDialog(root, layout, JFXDialog.DialogTransition.CENTER).show();
    }

    private void destroyServers() {
        if (ftpServerInternal != null) {
            ftpServerInternal.stop();
        }
        if (ftpServerExternal != null) {
            ftpServerExternal.stop();
        }

        ftpServerInternal = null;
        ftpServerExternal = null;
    }
}
