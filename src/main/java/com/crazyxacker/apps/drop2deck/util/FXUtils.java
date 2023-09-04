package com.crazyxacker.apps.drop2deck.util;

import com.crazyxacker.apps.drop2deck.FXApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;

public class FXUtils {

    public static void setTaskbarAppIcon(Stage primaryStage, Image appIconImage) {
        primaryStage.getIcons().add(appIconImage);
    }

    public static Image createAppIconImage() {
        return new Image("/icons/icon.png");
    }

    public static void addDefaultStylesheet(Scene scene) {
        scene.getStylesheets().addAll(
                FXApplication.class.getResource("/css/cupertino-dark.css").toExternalForm()
        );
    }

    public static FXMLLoader getLoader(String fxmlPath) {
        return new FXMLLoader(FXApplication.class.getResource(fxmlPath), null);
    }

    public static FXMLLoader loadFXMLAndGetLoader(String fxmlPath) {
        FXMLLoader loader = getLoader(fxmlPath);
        try {
            return loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> Pair<Parent, T> loadFXML(String fxmlPath) {
        FXMLLoader loader = getLoader(fxmlPath);
        try {
            return new Pair<>(loader.load(), loader.getController());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Parent loadFXMLAndGetParent(String fxmlPath) {
        FXMLLoader loader = getLoader(fxmlPath);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return loader.getRoot();
    }

    public static <T> T loadFXMLAndGetController(String fxmlPath) {
        FXMLLoader loader = getLoader(fxmlPath);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return loader.getController();
    }

    public static void clipRoundedCorners(Node node, double width, double height, int arcWidth, int arcHeight) {
        Rectangle rectangle = new Rectangle();
        rectangle.setWidth(width);
        rectangle.setHeight(height);
        rectangle.setArcWidth(arcWidth);
        rectangle.setArcHeight(arcHeight);
        node.setClip(rectangle);
    }

    public static void copyToClipboard(String clip) {
        ClipboardContent content = new ClipboardContent();
        content.putString(clip);

        Clipboard.getSystemClipboard().setContent(content);
    }

    public static void numericOnlyTextField(TextField field) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                field.setText(newValue.replaceAll("\\D", ""));
            }
        });
    }
}
