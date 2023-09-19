public class FileReadException extends Exception {
    FileReadException(String filename){
        super("MemoryM: error, read of " + filename + " failed.");
        System.exit(-1);
    }
}