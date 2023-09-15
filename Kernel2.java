import java.lang.Thread;
import java.io.*;
import java.util.*;

public class Kernel2 extends Thread {
  private int runs;
  private int run_cycles;
  private int no_virtual_pages;
  private long block_page_size;
  private boolean do_file_log;
  private boolean do_stdout_log;
  private static byte address_radix = 10;
  private ControlPanel control_panel;
  private String output_file = "tracefile";
  private Vector<Page> pages_vector;
  private Vector<Instruction> instructions_vector;
  private static final String lineSeparator = System.getProperty("line.separator");

  Kernel2(String commands_path, String config_path) {
    this.no_virtual_pages = 63;
    this.block_page_size = 0x1000;
    this.do_file_log = false;
    this.do_stdout_log = false;
    this.runs = 0;
    long address_limit = no_virtual_pages * block_page_size - 1;

    if (config_path != null) {
      // Read config file
      try {
        File config_file = new File(config_path);
        Scanner config_scanner = new Scanner(config_file);
        while (config_scanner.hasNextLine()) {
          String[] line = config_scanner.nextLine().split(" ");
          switch (line[0].toLowerCase()) {
            case "enable_logging":
            this.do_file_log = Boolean.parseBoolean(line[1]);
            break;

            case "log_file":
            this.output_file = line[1].toLowerCase();
            break;

            case "pagesize":
            if (line[1].toLowerCase() == "power") {
              block_page_size = (long) Math.pow(2, Integer.parseInt(line[2]));
            } else {
              block_page_size = Long.parseLong(line[1]);
            }
            address_limit = no_virtual_pages * block_page_size - 1;
            break;

            case "addressradix":
            Kernel2.address_radix = Byte.parseByte(line[1]);
            break;

            case "numpages":
            no_virtual_pages = Integer.parseInt(line[1]);
            if (no_virtual_pages < 2 || no_virtual_pages > 63) {
              throw new Exception("MemoryManagement: numpages out of bounds.");
            }
            address_limit = no_virtual_pages * block_page_size - 1;
            break;

            default:
              break;
          }
        }
        config_scanner.close();

        for (int i = 0; i <= no_virtual_pages; i++) {
          long high = (i + 1) * block_page_size - 1;
          long low = i * block_page_size;
          pages_vector.add(new Page(i, -1, false, false, 0, 0, high, low));
        }

        config_scanner = new Scanner(config_file);

        while (config_scanner.hasNextLine()) {
          String[] line = config_scanner.nextLine().split(" ");
          if (line[0] != "memset") { continue; }
          int id = Integer.parseInt(line[1]);
          if (id < 0 || id > no_virtual_pages) {
            config_scanner.close();
            throw new Exception("MemoryManagement: memset id out of bounds.");
          }
          pages_vector.get(id).setNewValues(line, no_virtual_pages);
        }

        config_scanner.close();
      } catch (FileNotFoundException e) {
        System.out.println("Kernel2: error, file '" + config_path + "' does not exist.");
        System.exit(-1);
      } catch (Exception e) {
        System.out.println("Kernel2: error, " + e.getMessage());
        System.exit(-1);
      }
    }

    // Read commands file
    try {
      File commands_file = new File(commands_path);
      Scanner commands_scanner = new Scanner(commands_file);
      final int command = 0, type = 1, address_1 = 2, address_2 = 3;

      while (commands_scanner.hasNextLine()) {
        String[] line = commands_scanner.nextLine().split(" ");
        Instruction instruction;
        if (line[command] != "READ" && line[command] != "WRITE") { continue; }
        if (line[type].toLowerCase() == "random") {
          instruction = new Instruction(line[command], Common.randomLong(address_limit));
        } else {
          instruction = new Instruction(line[command], line[type], line[address_1], line[address_2]);
          instruction.isInRange(address_limit);
        }
        instructions_vector.add(instruction);

        commands_scanner.close();
      }
    } catch (FileNotFoundException e) {
      System.out.println("Kernel2: error, file '" + commands_path + "' does not exist.");
      System.exit(-1);
    }

    this.run_cycles = instructions_vector.size();
    if (run_cycles < 1) {
      System.out.println("Kernel2: error, no instructions to run.");
      System.exit(-1);
    }
    Vector<Integer> not_mapped = new Vector<Integer>();
    for(int i = 0; i < no_virtual_pages; i++){ not_mapped.add(i); }
    for (Page page_it : pages_vector){
      int physical_aux = page_it.getPhysicalAddress();
      if (physical_aux == -1) { continue; }
      if (!not_mapped.contains(physical_aux)) {
        System.out.println("Kernel2: error, physical address " + physical_aux + " is repeated.");
        System.exit(-1);
      }
      not_mapped.remove(physical_aux);
    }
    for (Page page_it : pages_vector){
      if (not_mapped.size() > (no_virtual_pages + 1) / 2 && page_it.getPhysicalAddress() == -1){
        page_it.setPhysicalAddress(not_mapped.remove(0));
      }
      if (page_it.getPhysicalAddress() == -1){
        control_panel.removePhysicalPage(page_it.getID());
      } else {
        control_panel.addPhysicalPage(page_it.getID(), page_it.getPhysicalAddress());
      }
    }
  }

  public void setControlPanel(ControlPanel control_panel) {
    this.control_panel = control_panel;
  }

  public void getPage(byte ID){
    control_panel.paintPage(pages_vector.elementAt(ID));
  }
  public void run(){
    step();
    while (this.runs())
  }
}