package algorithm_visualizer.gui.components;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * UI component providing playback controls for algorithm visualization.
 * <p>
 * Contains buttons for run, pause, step forward/backward, reset, and a speed
 * slider.
 * </p>
 */
public class ControlPanel extends VBox {

    private final Button runButton;
    private final Button stepForwardButton;
    private final Button stepBackwardButton;
    private final Button pauseButton;
    private final Button resetButton;
    private final Slider speedSlider;

    private Runnable onRun;
    private Runnable onStepForward;
    private Runnable onStepBackward;
    private Runnable onPause;
    private Runnable onReset;

    public ControlPanel() {
        setSpacing(10);
        setPadding(new Insets(10));

        runButton = new Button("▶ Run");
        runButton.setPrefWidth(80);

        pauseButton = new Button("⏸ Pause");
        pauseButton.setPrefWidth(80);

        stepBackwardButton = new Button("◀ Step");
        stepBackwardButton.setPrefWidth(80);

        stepForwardButton = new Button("Step ▶");
        stepForwardButton.setPrefWidth(80);

        resetButton = new Button("↺ Reset");
        resetButton.setPrefWidth(80);

        HBox buttonsRow1 = new HBox(10, runButton, pauseButton, resetButton);
        HBox buttonsRow2 = new HBox(10, stepBackwardButton, stepForwardButton);

        Label speedLabel = new Label("Speed:");
        speedSlider = new Slider(0.1, 5.0, 1.0);
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMajorTickUnit(1.0);
        speedSlider.setPrefWidth(200);

        HBox speedRow = new HBox(10, speedLabel, speedSlider);

        getChildren().addAll(buttonsRow1, buttonsRow2, speedRow);

        // Wire up buttons
        runButton.setOnAction(e -> {
            if (onRun != null)
                onRun.run();
        });
        pauseButton.setOnAction(e -> {
            if (onPause != null)
                onPause.run();
        });
        stepForwardButton.setOnAction(e -> {
            if (onStepForward != null)
                onStepForward.run();
        });
        stepBackwardButton.setOnAction(e -> {
            if (onStepBackward != null)
                onStepBackward.run();
        });
        resetButton.setOnAction(e -> {
            if (onReset != null)
                onReset.run();
        });
    }

    /** Sets the callback for the Run button. */
    public void setOnRun(Runnable handler) {
        this.onRun = handler;
    }

    /** Sets the callback for the Step Forward button. */
    public void setOnStepForward(Runnable handler) {
        this.onStepForward = handler;
    }

    /** Sets the callback for the Step Backward button. */
    public void setOnStepBackward(Runnable handler) {
        this.onStepBackward = handler;
    }

    /** Sets the callback for the Pause button. */
    public void setOnPause(Runnable handler) {
        this.onPause = handler;
    }

    /** Sets the callback for the Reset button. */
    public void setOnReset(Runnable handler) {
        this.onReset = handler;
    }

    /** Returns the current speed slider value. */
    public double getSpeed() {
        return speedSlider.getValue();
    }

    /** Returns the speed slider for binding. */
    public Slider getSpeedSlider() {
        return speedSlider;
    }

    /**
     * Updates button states based on playback status.
     *
     * @param playing true if currently playing
     */
    public void setPlaying(boolean playing) {
        runButton.setDisable(playing);
        pauseButton.setDisable(!playing);
    }
}
