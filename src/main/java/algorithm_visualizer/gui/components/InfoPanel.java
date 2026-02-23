package algorithm_visualizer.gui.components;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * UI component displaying algorithm execution information.
 * <p>
 * Shows step counter, current status, and a log of algorithm events.
 * </p>
 */
public class InfoPanel extends VBox {

    private final Label stepCountLabel;
    private final Label currentStepLabel;
    private final TextArea logArea;

    public InfoPanel() {
        setSpacing(10);
        setPadding(new Insets(10));
        setPrefWidth(280);

        Label titleLabel = new Label("Algorithm Info");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        stepCountLabel = new Label("Step: 0 / 0");
        currentStepLabel = new Label("Status: Ready");
        currentStepLabel.setWrapText(true);

        Label logLabel = new Label("Log:");
        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setWrapText(true);

        // Make log area grow to fill remaining vertical space
        VBox.setVgrow(logArea, Priority.ALWAYS);

        getChildren().addAll(titleLabel, stepCountLabel, currentStepLabel, logLabel, logArea);
    }

    /**
     * Updates the step counter display.
     *
     * @param current the current step number
     * @param total   the total number of steps
     */
    public void setStepCount(int current, int total) {
        stepCountLabel.setText(String.format("Step: %d / %d", current, total));
    }

    /**
     * Updates the current step status message.
     *
     * @param message the status message
     */
    public void setCurrentStep(String message) {
        currentStepLabel.setText("Status: " + message);
    }

    /**
     * Appends a message to the log area.
     *
     * @param message the message to append
     */
    public void appendLog(String message) {
        logArea.appendText(message + "\n");
    }

    /** Clears the log area. */
    public void clearLog() {
        logArea.clear();
    }

    /** Resets the panel to its initial state. */
    public void reset() {
        stepCountLabel.setText("Step: 0 / 0");
        currentStepLabel.setText("Status: Ready");
        clearLog();
    }
}
