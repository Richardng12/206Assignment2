package name_sayer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Main extends Application {
	

    private static Scene Record;
    private static Scene Main;
    private static Scene Query;
    private static Stage _recordStage = new Stage();


    //Sets the initial stage and loads the other FXML files for the other stages
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader((getClass().getResource("MainMenu.fxml")));
        FXMLLoader loader2 = new FXMLLoader((getClass().getResource("RecordQuery.fxml")));
        Parent MainRoot = loader.load();
        Parent RecordRoot = FXMLLoader.load(getClass().getResource("Record.fxml"));
        Parent RecordQueryRoot = loader2.load();
        primaryStage.setTitle("Name Sayer");
        
        primaryStage.setResizable(false);
        _recordStage.setResizable(false);
        
        //Give reference from the MenuController to the RecordQueryContrller so that the RecordQueryController can use
        //MenuController's methods.
        RecordQueryController recordQueryController = loader2.getController();
        MenuController menucontroller = loader.getController();
       recordQueryController.setMenuController(menucontroller);
        
        


        //Create the scenes
        Main = new Scene(MainRoot, 1336, 768);
        Record = new Scene(RecordRoot, 480, 223);
        Query = new Scene(RecordQueryRoot, 480, 126);
        _recordStage.initModality(Modality.APPLICATION_MODAL);
        primaryStage.setScene(Main);
        primaryStage.show();
        
    }

    //Methods which allow easy changing of scenes and stages
    public static void loadRecordStage() {
    	

        _recordStage.setTitle("Record Audio");
        _recordStage.setScene(Record);
        _recordStage.show();

    }

    public static void loadRecordQueryScene() {
        _recordStage.setScene(Query);
    }

    public static void loadRecordScene() {
        _recordStage.setScene(Record);
    }
    
    public static void closeRecordQueryScene() {
	_recordStage.close();
	
}
  

    public static void main(String[] args) {
        launch(args);
    }

}


