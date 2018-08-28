package name_sayer;


//Singleton class to pass the creation name from one controller to another
public class DataSingleton {

    private static DataSingleton single_instance = null;
    private String _creationName;

    //private constructor
    private DataSingleton() {

    }

    public static DataSingleton getInstance() {
        if (single_instance == null) {
            single_instance = new DataSingleton();
            return single_instance;
        } else {
            return single_instance;
        }
    }


    public void setCreationName(String creationName) {
        this._creationName = creationName;
    }
    public String getCreationName() {
        return _creationName;
    }


}

