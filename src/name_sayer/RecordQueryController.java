package name_sayer;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class RecordQueryController {

    @FXML
    private Button listenButton;

    @FXML
    private Button redoButton;

    @FXML
    private Button keepButton;

   
    private MenuController menuController;

    //setter to enable this controller to call MenuController's methods
    public void setMenuController(MenuController menuController) {
    	this.menuController = menuController;
    }


    //When the listen button is pressed, it calls the listenFunctionality method which allows the user to listen to
    //the recording, also all buttons are disabled for 5 seconds, and then reenabled so that the user cannot press anything else while listening
    @FXML
    public void buttonClick(ActionEvent e) {
        if (e.getSource().equals(listenButton)) {
        	new Thread(() -> {
                Platform.runLater(() -> {
                    listenButton.setDisable(true);
                    keepButton.setDisable(true);
                    redoButton.setDisable(true);
                    listenFunctionality();
                });
                try {
                    Thread.sleep(5000);
                }
                catch(InterruptedException ex) {
                }
                Platform.runLater(() -> {
                    listenButton.setDisable(false);
                    keepButton.setDisable(false);
                    redoButton.setDisable(false);
                });
            }).start();
        }
    //if redo button is pressed, calls redoFunctionality
        if (e.getSource().equals(redoButton)) {
            redoFunctionality();
        }
    //if keep button is pressed, calls keepFunctionality
        if (e.getSource().equals(keepButton)) {
        	 keepFunctionality();
    }
    }

    //Finds the path of the audio file and plays it
    public void listenFunctionality() {


    	String path = new File("Creations/TempAudio.wav").getAbsolutePath();
    	
    	try {
 
        	AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(path).getAbsoluteFile());
        	Clip clip = AudioSystem.getClip();
        	clip.open(audioInputStream);
        	clip.stop();
        	clip.start();
    }catch (Exception e){
    }
    }
    	
    
    
    //Deletes the old audio and switches scenes back to the recording scene
    public void redoFunctionality() {
    	try {

            ProcessBuilder deleteOldAudio = new ProcessBuilder("bash", "-c", "rm -rf Creations/*.wav");
            deleteOldAudio.start();
        } catch (IOException error) {

        }
        Main.loadRecordScene();
    	
    }
    //gets the creation name from the singleton instance, and merges the video and audio files in a new thread as to
    //stop the gui freezing, the user is then taken back to the mainmenu stage
    public void keepFunctionality() {

        DataSingleton dataSingleton = DataSingleton.getInstance();
        String creationName = dataSingleton.getCreationName();
        
        new Thread(() -> {
            Platform.runLater(() -> {
                try {
                    ProcessBuilder mergeVideo = new ProcessBuilder("ffmpeg","-y", "-i", "TempVideo.mp4", "-i", "TempAudio.wav", "-map", "0:v", "-map", "1:a","-strict", "-2", creationName + ".mp4");
                    mergeVideo.directory(new File("./Creations"));

                    Process mergeProcess = mergeVideo.start();
                    mergeProcess.waitFor();

                 ProcessBuilder delete = new ProcessBuilder("rm", "TempVideo.mp4", "TempAudio.wav");
                delete.directory(new File("./Creations"));
                delete.start();

                }
                catch(IOException | InterruptedException error) {

                }
            });
            try {
                Thread.sleep(500);
            }
            catch(InterruptedException error) {
            }
            Platform.runLater(() ->
                    menuController.listCreations());
        }).start();



    	
    	menuController.clearTextField();
    	Main.closeRecordQueryScene();
    }
}
    
   
