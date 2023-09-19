public class NonExistentFileException extends Exception{
    NonExistentFileException(String filename){
        super("MemoryM: error, file '" + filename + "' does not exist.");
        System.exit(-1);
    }
}