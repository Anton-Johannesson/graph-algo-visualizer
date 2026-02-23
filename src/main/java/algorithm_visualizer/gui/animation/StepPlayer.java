package algorithm_visualizer.gui.animation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import algorithm_visualizer.steps.AlgorithmStep;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * Controls playback of algorithm steps for visualization.
 * <p>
 * Manages step-by-step or continuous playback with adjustable speed.
 * Provides callbacks for step execution, index changes, and completion.
 * </p>
 */
public class StepPlayer {

    private List<AlgorithmStep> steps = new ArrayList<>();
    private int currentIndex = -1;
    private Timeline timeline;
    private double stepsPerSecond = 1.0;
    private boolean playing = false;

    private Consumer<AlgorithmStep> onStep;
    private Consumer<Integer> onIndexChange;
    private Runnable onComplete;
    private Runnable onPlayStateChange;

    public StepPlayer() {
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        updateTimelineRate();
    }

    /**
     * Loads a list of algorithm steps for playback.
     *
     * @param steps the steps to play
     */
    public void load(List<AlgorithmStep> steps) {
        stop();
        this.steps = new ArrayList<>(steps);
        this.currentIndex = -1;
        notifyIndexChange();
    }

    /** Starts continuous playback of steps. */
    public void play() {
        if (steps.isEmpty() || currentIndex >= steps.size() - 1) {
            return;
        }
        playing = true;
        updateTimelineRate();
        timeline.play();
        notifyPlayStateChange();
    }

    /** Pauses playback. */
    public void pause() {
        playing = false;
        timeline.pause();
        notifyPlayStateChange();
    }

    /** Stops playback and resets to beginning. */
    public void stop() {
        playing = false;
        timeline.stop();
        currentIndex = -1;
        notifyPlayStateChange();
        notifyIndexChange();
    }

    /** Advances to the next step. */
    public void stepForward() {
        if (currentIndex < steps.size() - 1) {
            currentIndex++;
            AlgorithmStep step = steps.get(currentIndex);
            if (onStep != null) {
                onStep.accept(step);
            }
            notifyIndexChange();

            if (currentIndex >= steps.size() - 1 && onComplete != null) {
                pause();
                onComplete.run();
            }
        }
    }

    /** Goes back to the previous step. */
    public void stepBackward() {
        if (currentIndex > 0) {
            currentIndex--;
            notifyIndexChange();
            // Note: stepping backward requires replaying from start to currentIndex
            // This is handled by the controller
        } else if (currentIndex == 0) {
            currentIndex = -1;
            notifyIndexChange();
        }
    }

    /**
     * Sets the playback speed.
     *
     * @param stepsPerSecond number of steps per second (clamped to 0.1-10.0)
     */
    public void setSpeed(double stepsPerSecond) {
        this.stepsPerSecond = Math.max(0.1, Math.min(10.0, stepsPerSecond));
        updateTimelineRate();
    }

    private void updateTimelineRate() {
        timeline.stop();
        timeline.getKeyFrames().clear();
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1.0 / stepsPerSecond), e -> stepForward()));
        if (playing) {
            timeline.play();
        }
    }

    /** Sets the callback invoked for each step. */
    public void setOnStep(Consumer<AlgorithmStep> handler) {
        this.onStep = handler;
    }

    /** Sets the callback invoked when the step index changes. */
    public void setOnIndexChange(Consumer<Integer> handler) {
        this.onIndexChange = handler;
    }

    /** Sets the callback invoked when playback completes. */
    public void setOnComplete(Runnable handler) {
        this.onComplete = handler;
    }

    /** Sets the callback invoked when play/pause state changes. */
    public void setOnPlayStateChange(Runnable handler) {
        this.onPlayStateChange = handler;
    }

    /** Returns the current step index (-1 if not started). */
    public int getCurrentIndex() {
        return currentIndex;
    }

    /** Returns the total number of steps. */
    public int getTotalSteps() {
        return steps.size();
    }

    /** Returns true if currently playing. */
    public boolean isPlaying() {
        return playing;
    }

    /** Returns the list of loaded steps. */
    public List<AlgorithmStep> getSteps() {
        return steps;
    }

    private void notifyIndexChange() {
        if (onIndexChange != null) {
            onIndexChange.accept(currentIndex);
        }
    }

    private void notifyPlayStateChange() {
        if (onPlayStateChange != null) {
            onPlayStateChange.run();
        }
    }
}
