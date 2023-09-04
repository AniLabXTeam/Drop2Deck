package com.crazyxacker.apps.drop2deck.skin;

import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.skin.TextFieldSkin;
import org.kordamp.ikonli.javafx.FontIcon;

public class VisiblePasswordFieldSkin extends TextFieldSkin {
    private static final String BULLET = "•";

    private static final String EYE = "mdi2e-eye";
    private static final String EYE_OFF = "mdi2e-eye-off";

    private boolean mask = true;

    public VisiblePasswordFieldSkin(PasswordField textField, Button showHideButton, FontIcon showHideIcon) {
        super(textField);

        showHideButton.setCursor(Cursor.HAND);
        showHideButton.setVisible(false);

        showHideButton.setOnMouseClicked(event -> {
            showHideIcon.setIconLiteral(mask ? EYE_OFF : EYE);
            mask = !mask;

            textField.setText(textField.getText());
            textField.end();

        });

        textField.textProperty().addListener((observable, oldValue, newValue) -> showHideButton.setVisible(!newValue.isEmpty()));
    }

    @Override
    protected String maskText(String txt) {
        if (getSkinnable() instanceof PasswordField && mask) {
            return BULLET.repeat(txt.length());
        } else {
            return txt;
        }
    }
}
