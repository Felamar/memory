import java.io.*;
import java.util.*;

public class InsufficientArgumentsException extends Exception {
    public InsufficientArgumentsException(){
        super("Usage: 'java MemoryManagement <COMMAND FILE> <PROPERTIES FILE>'");
        System.exit(-1);
    }
}