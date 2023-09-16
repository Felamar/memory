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
  public static byte address_radix = 10;
  private ControlPanel2 control_panel;
  private String output_file = "tracefile";
  private String commands_path = "";
  private String config_path = "";
  private Vector<Page> pages_vector;
  private Vector<Instruction> instructions_vector;
  private static final String lineSeparator = System.getProperty("line.separator");

  public void init(String commands_path, String config_path) {
    this.no_virtual_pages = 63;
    this.block_page_size = 0x1000;
    this.do_file_log = false;
    this.do_stdout_log = false;
    this.runs = 0;
    this.run_cycles = 0;
    this.address_limit = no_virtual_pages * block_page_size - 1;
    this.pages_vector = new Vector<Page>();
    this.instructions_vector = new Vector<Instruction>();
    this.config_path = config_path;
    this.commands_path = commands_path;
    if (this.config_path != null) {
      // Read config file
    try {
      File config_file = new File(this.config_path);
      Scanner config_scanner = new Scanner(config_file);
      while (config_scanner.hasNextLine()) {
        String[] line = config_scanner.nextLine().split(" ");
        switch (line[0].toLowerCase()) {
          
          case "enable_logging":
          this.do_file_log = Boolean.parseBoolean(line[1]);
          break;

          case "log_file":
          this.output_file = line[1].toLowerCase();
          File out = new File(this.output_file);
          if (out.exists()) { out.delete(); }
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
          if (no_virtual_pages < 2 || no_virtual_pages > 64) {
            throw new Exception("MemoryManagement: numpages out of bounds.");
          }
          address_limit = no_virtual_pages * block_page_size - 1;
          break;

          default:
          break;
        }
      }
      config_scanner.close();

      for (int i = 0; i < no_virtual_pages; i++) {
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
      System.out.println("Kernel2: error, file '" + this.config_path + "' does not exist.");
      System.exit(-1);
    } catch (Exception e) {
      System.out.println("Kernel2: error, " + e.getMessage());
      System.exit(-1);
    }
    }

    // Read commands file
    try {
      File commands_file = new File(this.commands_path);
      Scanner commands_scanner = new Scanner(commands_file);
      final int command = 0, type = 1, address_1 = 2, address_2 = 3;

      while (commands_scanner.hasNextLine()) {
        String[] line = commands_scanner.nextLine().split(" ");
        Instruction instruction;
        if (!line[command].equals("READ") && !line[command].equals("WRITE")) { continue; }
        if (line[type].toLowerCase().equals("random")) {
          instruction = new Instruction(line[command], Common.randomLong(address_limit));
        } else if (line.length == 3) {
          instruction = new Instruction(line[command], line[type], line[address_1], null);
          instruction.isInRange(address_limit);
        }else{
          instruction = new Instruction(line[command], line[type], line[address_1], line[address_2]);
          instruction.isInRange(address_limit);
        }
        this.instructions_vector.add(instruction);

      }
      commands_scanner.close();
    } catch (FileNotFoundException e) {
      System.out.println("Kernel2: error, file '" + this.commands_path + "' does not exist.");
      System.exit(-1);
    } catch (IllegalStateException e){}

    this.run_cycles = this.instructions_vector.size();
    if (this.run_cycles < 1) {
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

  public void setControlPanel(ControlPanel2 control_panel) {
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

  public void getPage(int pageNum) {
    Page page = (Page) pages_vector.elementAt(pageNum);
    control_panel.paintPage(page);
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
  public int getRuns() { return this.runs; }
  public int getRunCycles() { return this.run_cycles; }

  public void step() {
    Instruction instruction = this.instructions_vector.elementAt(this.runs);

    control_panel.instruction_Label.setText(instruction.getInstruction());
    control_panel.address_Label.setText(
      Long.toString(instruction.getMinAddress(), address_radix)
      + " - " + 
      Long.toString(instruction.getMaxAddress(), address_radix)
    );
    long segment_step = block_page_size / 4;
    long page_num = instruction.getMinAddress() / block_page_size;
    long segment_start = (int) Math.floor((instruction.getMinAddress() % block_page_size) / segment_step);
    long segment_end = (int) Math.floor((instruction.getMaxAddress() % block_page_size) / segment_step);
    
    if (page_num != instruction.getMaxAddress() / block_page_size) {
      control_panel.segmentation_Label.setText("ERROR");
    } else {
      control_panel.segmentation_Label.setText("P(" + page_num + ") S(" + segment_start + " - " + segment_end + ")");
    }

    control_panel.paintPage(pages_vector.elementAt((int) page_num));

    Page page = pages_vector.elementAt((int) page_num);
    String message = "";

    if (page.getPhysicalAddress() == -1){
      message = instruction.getInstruction() + " " + Long.toString(instruction.getMinAddress(), address_radix) + "... page fault";
      PageFault.replacePage(pages_vector, no_virtual_pages, (int) page_num, control_panel);
      control_panel.page_fault_Label.setText("YES");
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
    control_panel.time_Label.setText(Integer.toString(runs * 10) + " (ns)");
  }

  public void reset() {
    this.pages_vector.clear();
    this.instructions_vector.clear();

    control_panel.status_Label.setText("STOP");
    control_panel.time_Label.setText("0");
    control_panel.instruction_Label.setText("NONE");
    control_panel.address_Label.setText("NULL");
    control_panel.segmentation_Label.setText("");
    control_panel.page_fault_Label.setText("NO");
    control_panel.virtual_page_Label.setText("x");
    control_panel.physical_page_Label.setText("0");
    control_panel.referenced_Label.setText("0");
    control_panel.modified_Label.setText("0");
    control_panel.in_mem_time_Label.setText("0");
    control_panel.last_touch_time_Label.setText("0");
    control_panel.low_limit_address_Label.setText("0");
    control_panel.high_limit_address_Label.setText("0");
    init(commands_path, config_path);;
  }
}