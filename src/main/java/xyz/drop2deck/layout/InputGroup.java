package xyz.drop2deck.layout;

import javafx.beans.InvalidationListener;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

/**
 * A layout that helps combine multiple controls into a group that looks
 * like a single control.
 *
 * <p>Without it, you would have to manually add the ".left-pill", ".center-pill"
 * and ".right-pill" styles classes to each control in such combination.
 * The InputGroup removes this ceremony. Since it inherits from HBox, you can use
 * the same API.
 */
public class InputGroup extends HBox {
    public static final String LEFT_PILL = "left-pill";
    public static final String CENTER_PILL = "center-pill";
    public static final String RIGHT_PILL = "right-pill";

    /**
     * Creates a new empty InputGroup.
     */
    public InputGroup() {
        super();
        init();
    }

    /**
     * Creates an InputGroup with the given children.
     *
     * @param children The initial set of children for this pane.
     */
    public InputGroup(Node... children) {
        super(children);
        init();
    }

    protected void init() {
        setAlignment(Pos.CENTER_LEFT);
        getStyleClass().add("input-group");

        updateStyles();
        getChildren().addListener((InvalidationListener) o -> updateStyles());
    }

    // We don't clean up style classes if a control is removed from the input group.
    // However, they will be fixed if the same control is added to the input group again.
    protected void updateStyles() {
        for (int i = 0; i < getChildren().size(); i++) {
            Node n = getChildren().get(i);

            n.getStyleClass().removeAll(
                    LEFT_PILL, CENTER_PILL, RIGHT_PILL
            );

            if (i == getChildren().size() - 1) {
                if (i != 0) {
                    n.getStyleClass().add(RIGHT_PILL);
                }
            } else if (i == 0) {
                n.getStyleClass().add(LEFT_PILL);
            } else {
                n.getStyleClass().add(CENTER_PILL);
            }
        }
    }
}