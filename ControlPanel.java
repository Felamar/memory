import java.awt.*;
import java.awt.event.ActionListener;

public class ControlPanel extends Frame {
  public Kernel kernel;
  public Button run_button;
  public Button step_button;
  public Button reset_button;
  public Button exit_button;
  public Label status_Label;
  public Label time_Label;
  public Label instruction_Label;
  public Label address_Label;
  public Label segmentation_Label;
  public Label page_fault_Label;
  public Label virtual_page_Label;
  public Label physical_page_Label;
  public Label referenced_Label;
  public Label modified_Label;
  public Label in_mem_time_Label;
  public Label last_touch_time_Label;
  public Label low_limit_address_Label;
  public Label high_limit_address_Label;

  public int no_virtual_pages = 64;
  public Button[] button_pages = new Button[no_virtual_pages];
  public Label[] label_pages = new Label[no_virtual_pages];

  ControlPanel(String title){
    super(title);
    for(int i = 0; i < no_virtual_pages; i++){
      this.button_pages[i] = new Button("Page " + i);
      this.label_pages[i] = new Label(null, Label.CENTER);
    }
    this.run_button               = new Button("Run");
    this.step_button              = new Button("Step");
    this.reset_button             = new Button("Reset");
    this.exit_button              = new Button("Exit");
    this.status_Label             = new Label("STOP" , Label.LEFT) ;
    this.time_Label               = new Label("0" , Label.LEFT) ;
    this.instruction_Label        = new Label("NONE" , Label.LEFT) ;
    this.address_Label            = new Label("NULL" , Label.LEFT) ;
    this.segmentation_Label       = new Label("NULL" , Label.LEFT) ;
    this.page_fault_Label         = new Label("NO" , Label.LEFT) ;
    this.virtual_page_Label       = new Label("x" , Label.LEFT) ;
    this.physical_page_Label      = new Label("0" , Label.LEFT) ;
    this.referenced_Label         = new Label("0" , Label.LEFT) ;
    this.modified_Label           = new Label("0" , Label.LEFT) ;
    this.in_mem_time_Label        = new Label("0" , Label.LEFT) ;
    this.last_touch_time_Label    = new Label("0" , Label.LEFT) ;
    this.low_limit_address_Label  = new Label("0" , Label.LEFT) ;
    this.high_limit_address_Label = new Label("0" , Label.LEFT) ;
  }

  public void init(Kernel p_kernel, String commands, String config){
    this.kernel = p_kernel;
    this.kernel.setControlPanel(this);
    setLayout(null);
    setBackground(Color.white);
    setForeground(Color.black);
    setSize(635, 545);
    setFont(new Font("Courier", Font.PLAIN, 12));

    this.run_button.setForeground(Color.decode("#2b2b2b"));
    this.run_button.setBackground(Color.decode("#94b3b5"));
    this.run_button.setBounds(0, 25, 70, 15);
    this.run_button.addActionListener(new ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
      setStatus("RUN");
      run_button.setEnabled(false);
      step_button.setEnabled(false);
      reset_button.setEnabled(false);
      kernel.run();
      setStatus("STOP");
      reset_button.setEnabled(true);
      }
    });
    add(this.run_button);

    this.step_button.setForeground(Color.decode("#2b2b2b"));
    this.step_button.setBackground(Color.decode("#94b3b5"));
    this.step_button.setBounds(70, 25, 70, 15);
    this.step_button.addActionListener(new ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
      setStatus("STEP");
      if(kernel.getRuns() != kernel.getRunCycles()){ 
        kernel.step(); 
      } else { 
        step_button.setEnabled(false);
        run_button.setEnabled(false);
      }
      setStatus("STOP");
      }
    });
    add(this.step_button);

    this.reset_button.setForeground(Color.decode("#2b2b2b"));
    this.reset_button.setBackground(Color.decode("#94b3b5"));
    this.reset_button.setBounds(140, 25, 70, 15);
    this.reset_button.addActionListener(new ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
      setStatus("RESET");
      run_button.setEnabled(true);
      step_button.setEnabled(true);
      reset_button.setEnabled(false);
      kernel.reset();
      reset_button.setEnabled(true);
      setStatus("STOP");
      }
    });
    add(this.reset_button);

    this.exit_button.setForeground(Color.decode("#2b2b2b"));
    this.exit_button.setBackground(Color.decode("#94b3b5"));
    this.exit_button.setBounds(210, 25, 70, 15);
    this.exit_button.addActionListener(new ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
      System.exit(0);
      }
    });
    add(this.exit_button);

    for(int i = 0; i < no_virtual_pages; i++){
      int x_offset = 140 * (int)(i / (no_virtual_pages / 2));
      
      int aux = i;
      this.button_pages[i].setForeground(Color.decode("#2b2b2b"));
      this.button_pages[i].setBackground(Color.decode("#94b3b5"));
      this.button_pages[i].setBounds(x_offset, 55 + (i % (no_virtual_pages / 2)) * 15, 70, 15);
      this.button_pages[i].addActionListener(new ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          kernel.getPage(aux);
        }
      });
      add(button_pages[i]);
      
      this.label_pages[i].setBounds(x_offset + 70, 55 + i % (no_virtual_pages / 2) * 15, 70, 15);
      this.label_pages[i].setForeground(Color.red);
      this.label_pages[i].setFont(new Font("Courier", Font.PLAIN, 10));
      add(this.label_pages[i]);
    }

    this.status_Label.setBounds(400, 25, 100, 15);
    add(this.address_Label);

    this.time_Label.setBounds(400, 40 , 100, 15);
    add(this.time_Label);

    this.instruction_Label.setBounds(400, 70 , 100, 15);
    add(this.instruction_Label);

    this.address_Label.setBounds(400, 85 , 100, 15);
    add(this.address_Label);

    this.segmentation_Label.setBounds(400, 100 , 100, 15);
    add(this.segmentation_Label);

    this.page_fault_Label.setBounds(400, 115 , 100, 15);
    add(this.page_fault_Label);

    this.virtual_page_Label.setBounds(400, 145 , 100, 15);
    add(this.virtual_page_Label);

    this.physical_page_Label.setBounds(400, 160 , 100, 15);
    add(this.physical_page_Label);

    this.referenced_Label.setBounds(400, 175 , 100, 15);
    add(this.referenced_Label);

    this.modified_Label.setBounds(400, 190 , 100, 15);
    add(this.modified_Label);

    this.in_mem_time_Label.setBounds(400, 205 , 100, 15);
    add(this.in_mem_time_Label);

    this.last_touch_time_Label.setBounds(400, 220 , 100, 15);
    add(this.last_touch_time_Label);

    this.low_limit_address_Label.setBounds(400, 250 , 100, 15);
    add(this.low_limit_address_Label);

    this.high_limit_address_Label.setBounds(400, 265 , 100, 15);
    add(this.high_limit_address_Label);

    Label virtual_one_Label = new Label("Virtual", Label.CENTER);
    virtual_one_Label.setBounds(0, 35 , 70, 15);
    add(virtual_one_Label);

    Label virtual_two_Label = new Label("Virtual", Label.CENTER);
    virtual_two_Label.setBounds(140, 35 , 70, 15);
    add(virtual_two_Label);

    Label physical_one_Label = new Label("Physical", Label.CENTER);
    physical_one_Label.setBounds(70, 35 , 70, 15);
    add(physical_one_Label);

    Label physical_two_Label = new Label("Physical", Label.CENTER);
    physical_two_Label.setBounds(210, 35 , 70, 15);
    add(physical_two_Label);

    Label status_Label = new Label("status: ", Label.LEFT);
    status_Label.setBounds(285, 25 , 65, 15);
    add(status_Label);

    Label time_Label = new Label("time: ", Label.LEFT);
    time_Label.setBounds(285, 40 , 65, 15);
    add(time_Label);

    Label instruction_Label = new Label("instruction: ", Label.LEFT);
    instruction_Label.setBounds(285, 70 , 100, 15);
    add(instruction_Label);

    Label address_Label = new Label("address: ", Label.LEFT);
    address_Label.setBounds(285, 85 , 100, 15);
    add(address_Label);

    Label segmentation_Label = new Label("segmentation: ", Label.LEFT);
    segmentation_Label.setBounds(285, 100 , 100, 15);
    add(segmentation_Label);

    Label page_fault_Label = new Label("page fault: ", Label.LEFT);
    page_fault_Label.setBounds(285, 115 , 100, 15);
    add(page_fault_Label);

    Label virtual_page_Label = new Label("virtual page: ", Label.LEFT);
    virtual_page_Label.setBounds(285, 145 , 100, 15);
    add(virtual_page_Label);

    Label physical_page_Label = new Label("physical page: ", Label.LEFT);
    physical_page_Label.setBounds(285, 160 , 100, 15);
    add(physical_page_Label);

    Label referenced_Label = new Label("referenced: ", Label.LEFT);
    referenced_Label.setBounds(285, 175 , 100, 15);
    add(referenced_Label);

    Label modified_Label = new Label("modified: ", Label.LEFT);
    modified_Label.setBounds(285, 190 , 100, 15);
    add(modified_Label);

    Label in_mem_time_Label = new Label("in mem time: ", Label.LEFT);
    in_mem_time_Label.setBounds(285, 205 , 100, 15);
    add(in_mem_time_Label);

    Label last_touch_time_Label = new Label("last touch time: ", Label.LEFT);
    last_touch_time_Label.setBounds(285, 220 , 100, 15);
    add(last_touch_time_Label);

    Label low_limit_address_Label = new Label("low limit address: ", Label.LEFT);
    low_limit_address_Label.setBounds(285, 250 , 100, 15);
    add(low_limit_address_Label);

    Label high_limit_address_Label = new Label("high limit address: ", Label.LEFT);
    high_limit_address_Label.setBounds(285, 265 , 100, 15);
    add(high_limit_address_Label);

    this.kernel.init(commands, config);

    setVisible(true);
  }

  public void paintPage(Page page){
    this.virtual_page_Label.setText(Integer.toString(page.getID()));
    this.physical_page_Label.setText(Integer.toString(page.getPhysicalAddress()));
    this.referenced_Label.setText(Boolean.toString(page.getReferenced()));
    this.modified_Label.setText(Boolean.toString(page.getModified()));
    this.in_mem_time_Label.setText(Integer.toString(page.getTimeInMemory()));
    this.last_touch_time_Label.setText(Integer.toString(page.getTimeSinceTouched()));
    this.low_limit_address_Label.setText(Long.toString(page.getLowerMemoryLimit(), Kernel.address_radix));
    this.high_limit_address_Label.setText(Long.toString(page.getUpperMemoryLimit(), Kernel.address_radix));
  }

  public void setStatus(String status){
    this.status_Label.setText(status);
  }

  public void addPhysicalPage(int page_num, int physical_address){
    this.label_pages[physical_address].setText(Integer.toString(page_num));
  }

  public void removePhysicalPage(int physical_address){
    this.label_pages[physical_address].setText(null);
  }

}