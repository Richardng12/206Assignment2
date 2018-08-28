package name_sayer;

import java.io.IOException;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.util.Duration;

public class RecordController {

    @FXML
    private Button recordButton;
    @FXML
    private ProgressBar recordingProgressBar;



//When the record button is pressed, any temporary audio files are deleted, and the button is also disabled so that the
    //user cannot press it again. After 5 seconds of recording, the user is directed to another scene, and the button is reenabled.
    @FXML
    public void buttonClick(ActionEvent e) {
        if (e.getSource().equals(recordButton)) {
            try {
                ProcessBuilder deleteOldAudio = new ProcessBuilder("bash", "-c", "rm -rf Creations/*.wav");
                deleteOldAudio.start();

            } catch (IOException error) {

            }
            new Thread(() -> {
                Platform.runLater(() -> {
                    recordButton.setDisable(true);
                    final Timeline progressTime = new Timeline(new KeyFrame(Duration.seconds(5.0),
                            actionEvent -> recordButton.setDisable(false)));
                  progressTime.setCycleCount(1);
                  progressTime.play();

                    recordAudio();
                    progressBar();
                });
                try {
                    Thread.sleep(5000);
                }
                catch(InterruptedException error) {
                }
                Platform.runLater(() ->
                        Main.loadRecordQueryScene());
            }).start();
        }
    }

    //Bash command which records audio

    public void recordAudio() {
        try {
            ProcessBuilder recordAudio = new ProcessBuilder("ffmpeg", "-f", "alsa", "-ac", "1", "-ar", "44100", "-i", "default", "-t", "5", "-strict", "-2", "Creations/TempAudio.wav");
            recordAudio.start();
        } catch (IOException error) {
        }
    }

    //Method which displays a progress bar for 5 seconds, and after 5 seconds the progress bar resets back to the start state.

    public void progressBar() {
        Timeline countProgress = new Timeline(new KeyFrame(Duration.seconds(5), new KeyValue(recordingProgressBar.progressProperty(), 1))
        );
        countProgress.setCycleCount(1);
        countProgress.play();
        countProgress.setOnFinished(event -> {
            recordingProgressBar.setProgress(0.0);
        });
    }

}


