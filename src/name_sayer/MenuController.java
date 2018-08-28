package name_sayer;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.*;
import java.util.Optional;


public class MenuController {
    @FXML
    private TextField creationField;
    @FXML
    private Button playButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button createButton;

    @FXML
    private ListView<String> listView;


    private String _creationName;

    private String _selectName;


    private MediaView mv;

    private MediaPlayer mp;

    private Media me;


    @FXML
    private Pane mediaViewPlayer;


    //Deletes any temporary video or audio files upon startup, creates the creations directory, and also disables the buttons
    //so that the user cannot press anything if there are no Creations. We also populate the list view to check for any creations
    //on previous startups.
    @FXML
    public void initialize() {

        try {
            ProcessBuilder deleteOldVid = new ProcessBuilder("bash", "-c", "rm -rf Creations/TempAudio.wav");
            deleteOldVid.start();

        } catch (IOException error) {

        }

        try {
            ProcessBuilder deleteOldVid = new ProcessBuilder("bash", "-c", "rm -rf Creations/TempVideo.mp4");
            deleteOldVid.start();

        } catch (IOException error) {

        }


        disableButtons();
        try {
            ProcessBuilder makeDirectory = new ProcessBuilder("bash", "-c", "mkdir ./Creations");
            makeDirectory.start();
        } catch (IOException error) {

        }

        listCreations();

        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }


    //checks what button has been pressed and carries out the appropriate implementation for that particular button
    public void buttonClick(ActionEvent e) {

        if (e.getSource().equals(deleteButton)) {
            deleteCreation();

        }
        if (e.getSource().equals(playButton)) {
            playCreation();
        }
        if (e.getSource().equals(createButton)) {
            createCreation();
        }

    }

    //Check if valid input has been supplied when entering a creation name
    public void checkValidText() {
        String text = creationField.getText();
        boolean disableButton = text.isEmpty() || text.trim().isEmpty() || !text.matches("[-\\sa-zA-Z \\d  _]*");
        createButton.setDisable(disableButton);
    }

    //Disables the play and delete buttons unless a creation has been selected in the list of creations.
    public void handleClickListView() {
        playButton.disableProperty().bind(Bindings.isEmpty(listView.getSelectionModel().getSelectedItems()));
        deleteButton.disableProperty().bind(Bindings.isEmpty(listView.getSelectionModel().getSelectedItems()));
        _selectName = listView.getSelectionModel().getSelectedItem();
        viewPreview();
    }

    //Lists all the creations the user has made
    public void listCreations() {
        listView.getItems().clear();
        try {

            ProcessBuilder builder = new ProcessBuilder("bash", "-c", "ls | grep .mp4");
            builder.directory(new File("./Creations"));
            Process process = builder.start();


            InputStream stdout = process.getInputStream();


            try {
                process.waitFor();
            } catch (InterruptedException y) {
            }
            BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));

            String line = null;

            while ((line = stdoutBuffered.readLine()) != null) {
                line = line.substring(0, line.length() - 4);
                listView.getItems().add(line);
            }
        } catch (IOException x) {

        }
    }

    //Deletes a creation the user has selected, offers a prompt to check if the user really wants to delete or not.
    public void deleteCreation() {


        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);


        alert.setTitle("Confirm Deletion");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this Creation?");
        Optional<ButtonType> action = alert.showAndWait();

        if (action.get() == ButtonType.OK) {
            try {
                String extensionName = _selectName + ".mp4";
                mp.dispose();
                ProcessBuilder delete = new ProcessBuilder("rm", extensionName);
                delete.directory(new File("./Creations"));
                delete.start();

                listCreations();
                mediaViewPlayer.setVisible(false);


            } catch (IOException error) {

            }
        }
    }

    //Method to disable buttons upon initialization
    public void disableButtons() {
        createButton.setDisable(true);
        playButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    //Plays the creation the user has selected
    public void playCreation() {
    	mp.stop();
        mp.play();


    }

    //Creates a temporary video file of the creation name entered by the user, allows for overriding duplicate creation names.
    //Once the user presses the button, switches stages to the recording stage.
    public void createCreation() {

        _creationName = creationField.getText();
        String path = new File("Creations/" + _creationName + ".mp4").getAbsolutePath();
        DataSingleton dataSingleton = DataSingleton.getInstance();
        dataSingleton.setCreationName(_creationName);

        File name = new File(path);
        if (name.exists()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Creation name already exists.\nPress Yes to overwrite", ButtonType.YES, ButtonType.NO);
            alert.setTitle("Duplicate Creation Name");
            alert.setHeaderText(null);
            Optional<ButtonType> action = alert.showAndWait();
            System.out.println(_creationName);
            if (action.get() == ButtonType.YES) {
      
            		makeVideo();

            
                Main.loadRecordStage();

            }


        } else {

            makeVideo();
            Main.loadRecordStage();
        }
    }

//When a user clicks on a creation name on the list view, it shows a preview of the creation
    public void viewPreview() {
		if (this.mp != null && this.mp.getStatus() == Status.PLAYING) {
            this.mp.stop();
        }
		
        String extensionName = _selectName + ".mp4";
        if (!extensionName.equalsIgnoreCase("Null.mp4")) {
        	
            mediaViewPlayer.setVisible(true);
            String path = new File("Creations/" + extensionName).getAbsolutePath();
            me = new Media(new File(path).toURI().toString());
            mp = new MediaPlayer(me);
            mv = new MediaView(mp);
            mv.setFitHeight(650);
            mv.setFitWidth(900);

            mediaViewPlayer.getChildren().add(mv);
            mp.setAutoPlay(false);
        }
    }

    //Method that makes the temporary video file, also deletes temporary video files
    public void makeVideo() {
    	
    	 try {
             ProcessBuilder deleteOldVid = new ProcessBuilder("bash", "-c", "rm -rf Creations/TempVideo.mp4");
             deleteOldVid.start();

         } catch (IOException error) {

         }


        try {
        	
            ProcessBuilder makeVideo = new ProcessBuilder("ffmpeg", "-f", "lavfi", "-i", "color=c=black:s=900x650:d=5", "-vf", "drawtext=fontfile=/usr/share/fonts/truetype/dejavu/DejaVuSansMono.ttf:fontsize=30: fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text='" + _creationName + "'", "Creations/TempVideo.mp4");
            makeVideo.start();

        } catch (IOException error) {


        }


    }
//Method to clear the text field
    public void clearTextField() {
        creationField.clear();
    }
}

