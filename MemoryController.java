import java.io.*;
import java.util.*;

public class MemoryController {
    MemoryModel model;
    MemoryView view; 

    MemoryController(String commands_path, String config_path, String title){
        model = new MemoryModel(commands_path, config_path);
        view = new MemoryView(title);
    }

    public void initSimulator(){
        
    }

    public void initBottonsEvents(){

    }
}