import java.lang.Thread;
import java.io.*;
import java.util.*;

public class Kernel2 extends Thread {
  private int runs;
  private int run_cycles;
  private int no_virtual_pages;
  private long block_page_size;
  private long address_limit;
  private boolean do_file_log;
  private boolean do_stdout_log;
  private static byte address_radix = 10;
  private ControlPanel control_panel;
  private String output_file = "tracefile";
  private String commands_path;
  private String config_path;
  private Vector<Page> pages_vector;
  private Vector<Instruction> instructions_vector;
  private static final String lineSeparator = System.getProperty("line.separator");

  Kernel2(String commands_path, String config_path) {
    this.no_virtual_pages = 63;
    this.block_page_size = 0x1000;
    this.do_file_log = false;
    this.do_stdout_log = false;
    this.runs = 0;
    this.config_path = config_path;
    this.commands_path = commands_path;
    this.address_limit = no_virtual_pages * block_page_size - 1;
    this.pages_vector = new Vector<Page>();
    this.instructions_vector = new Vector<Instruction>();
    init();
  }

  public void init() {
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
    for (int i = 0; i < no_virtual_pages; i++) {
      not_mapped.add(i);
    }
    for (Page page_it : pages_vector) {
      int physical_aux = page_it.getPhysicalAddress();
      if (physical_aux == -1) {
        continue;
      }
      if (!not_mapped.contains(physical_aux)) {
        System.out.println("Kernel2: error, physical address " + physical_aux + " is repeated.");
        System.exit(-1);
      }
      not_mapped.remove(physical_aux);
    }
    for (Page page_it : pages_vector) {
      if (not_mapped.size() > (no_virtual_pages + 1) / 2 && page_it.getPhysicalAddress() == -1) {
        page_it.setPhysicalAddress(not_mapped.remove(0));
      }
      if (page_it.getPhysicalAddress() == -1) {
        control_panel.removePhysicalPage(page_it.getID());
      } else {
        control_panel.addPhysicalPage(page_it.getID(), page_it.getPhysicalAddress());
      }
    }
  }

  public void setControlPanel(ControlPanel control_panel) {
    this.control_panel = control_panel;
  }

  private void printLogFile(String message){
    try {
      FileWriter file_writer = new FileWriter(output_file, true);
      file_writer.write(message + lineSeparator);
      file_writer.close();
    } catch (IOException e) {
      System.out.println("Kernel2: error, " + e.getMessage());
      System.exit(-1);
    }
  }

  public void run() {
    step();
    while (this.runs < this.run_cycles) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        System.out.println("Kernel2: error, " + e.getMessage());
        System.exit(-1);
      }
      step();
    }
  }

  public void step() {
    Instruction instruction = instructions_vector.elementAt(this.runs);

    control_panel.instructionValueLabel.setText(instruction.toString());
    control_panel.addressValueLabel.setText(
      Long.toString(instruction.getMinAddress(), address_radix)
      + " - " + 
      Long.toString(instruction.getMaxAddress(), address_radix)
    );
    long segment_step = block_page_size / 4;
    long page_num = instruction.getMinAddress() / block_page_size;
    long segment_start = (int) Math.floor((instruction.getMinAddress() % block_page_size) / segment_step);
    long segment_end = (int) Math.floor((instruction.getMaxAddress() % block_page_size) / segment_step);
    
    if (page_num != instruction.getMaxAddress() / block_page_size) {
      control_panel.address_segmentation_label.setText("ERROR");
    } else {
      control_panel.address_segmentation_label.setText("P(" + page_num + ") S(" + segment_start + " - " + segment_end + ")");
    }

    control_panel.paintPage(pages_vector.elementAt((int) page_num));

    Page page = pages_vector.elementAt((int) page_num);
    String message = "";

    if (page.getPhysicalAddress() == -1){
      message = instruction.getInstruction() + " " + Long.toString(instruction.getMinAddress(), address_radix) + "... page fault";
      PageFault.replacePage(pages_vector, no_virtual_pages, (int) page_num, control_panel);
      control_panel.pageFaultValueLabel.setText("YES");
    } else {
      page.setTimeSinceTouched(0);

      boolean boolean_aux;

      boolean_aux = instruction.getInstruction() == "READ" ? true : page.getReferenced();
      page.setReferenced(boolean_aux);
      
      boolean_aux = instruction.getInstruction() == "WRITE" ? true : page.getModified();
      page.setModified(boolean_aux);
      
      message = instruction.getInstruction() + " " + Long.toString(instruction.getMinAddress(), address_radix) + "... okay";
    }

    if (do_stdout_log) { System.out.println(message); }
    if (do_file_log ) { printLogFile(message); }
    
    for (Page page_it : pages_vector) {
      if (page_it.getPhysicalAddress() == -1) { continue; }
      page_it.setTimeSinceTouched(page_it.getTimeSinceTouched() + 10);
      page_it.setTimeInMemory(page_it.getTimeInMemory() + 10);
    }

    runs++;
    control_panel.timeValueLabel.setText(Integer.toString(runs * 10) + " (ns)");
  }

  public void reset() {
    pages_vector.clear();
    instructions_vector.clear();

    control_panel.statusValueLabel.setText("STOP");
    control_panel.timeValueLabel.setText("0");
    control_panel.instructionValueLabel.setText("NONE");
    control_panel.addressValueLabel.setText("NULL");
    control_panel.address_segmentation_label.setText("");
    control_panel.pageFaultValueLabel.setText("NO");
    control_panel.virtualPageValueLabel.setText("x");
    control_panel.physicalPageValueLabel.setText("0");
    control_panel.RValueLabel.setText("0");
    control_panel.MValueLabel.setText("0");
    control_panel.inMemTimeValueLabel.setText("0");
    control_panel.lastTouchTimeValueLabel.setText("0");
    control_panel.lowValueLabel.setText("0");
    control_panel.highValueLabel.setText("0");
    init();
  }
}