// The main MemoryManagement program, created by Alexander Reeder, 2000 Nov 19
// Refactorized by Felamar and miguehm, 2023 Sep 13

import java.applet.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class MemoryManagement {
  public static void main(String[] args) {
    MemoryView controlPanel;
    MemoryModel kernel;
    MemoryController memoryController;

    String commands_path = args[0];
    String config_path = args[1];

    // check if we have command file and prop file (prop are optional)
    // overload the memoryController method
    memoryController = new MemoryController(commands_path, config_path, "Memory Management");

    // include inside MemoryModel class instead
    try {
      if (args.length < 1 || args.length > 2) {
        throw new InsufficientArgumentsException();
      }

      File commandsFile = new File(args[0]);

      if (!(commandsFile.exists())) {
        throw new NonExistentFileException(commandsFile.getName());
      }

      if (!(commandsFile.canRead())) {
        throw new FileReadException(commandsFile.getName());
      }

      File configFile = new File(args[1]);

      // if config file pass as argument
      if (args.length == 2) {
        if (!(configFile.exists())) {
          throw new NonExistentFileException(configFile.getName());
        }
        if (!(configFile.canRead())) {
          throw new FileReadException(configFile.getName());
        }
      }
    } catch(InsufficientArgumentsException err){
      err.toString();
    } catch(NonExistentFileException err){
      err.toString();
    } catch(FileReadException err){
      err.toString();
    }

    // refactor in controller
    kernel = new Kernel2();
    controlPanel = new ControlPanel2("Memory Management");
    if (args.length == 1) {
      controlPanel.init(kernel, args[0], null);
    } else {
      controlPanel.init(kernel, args[0], args[1]);
    }
  }
}
