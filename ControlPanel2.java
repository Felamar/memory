import java.applet.*;
import java.awt.*;

public class ControlPanel2 extends Frame {
  public Kernel2 kernel;
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

  ControlPanel2(String title){
    super(title);
    for(int i = 0; i < no_virtual_pages; i++){
      button_pages[i] = new Button("Page " + i);
      label_pages[i] = new Label(null, Label.CENTER);
    }
    run_button               = new Button("Run");
    step_button              = new Button("Step");
    reset_button             = new Button("Reset");
    exit_button              = new Button("Exit");
    status_Label             = new Label("STOP" , Label.LEFT) ;
    time_Label               = new Label("0" , Label.LEFT) ;
    instruction_Label        = new Label("NONE" , Label.LEFT) ;
    address_Label            = new Label("NULL" , Label.LEFT) ;
    segmentation_Label       = new Label("NULL" , Label.LEFT) ;
    page_fault_Label         = new Label("NO" , Label.LEFT) ;
    virtual_page_Label       = new Label("x" , Label.LEFT) ;
    physical_page_Label      = new Label("0" , Label.LEFT) ;
    referenced_Label         = new Label("0" , Label.LEFT) ;
    modified_Label           = new Label("0" , Label.LEFT) ;
    in_mem_time_Label        = new Label("0" , Label.LEFT) ;
    last_touch_time_Label    = new Label("0" , Label.LEFT) ;
    low_limit_address_Label  = new Label("0" , Label.LEFT) ;
    high_limit_address_Label = new Label("0" , Label.LEFT) ;
  }

  public void init(Kernel2 p_kernel, String commands, String config){
    kernel = p_kernel;
    kernel.setControlPanel(this);
    setLayout(null);
    setBackground(Color.white);
    setForeground(Color.black);
    setSize(635, 545);
    setFont(new Font("Helvetica", Font.PLAIN, 12));

    run_button.setForeground(Color.decode("#2b2b2b"));
    run_button.setBackground(Color.decode("#94b3b5"));
    run_button.setBounds(0, 25, 70, 15);
    add(run_button);

    step_button.setForeground(Color.decode("#2b2b2b"));
    step_button.setBackground(Color.decode("#94b3b5"));
    step_button.setBounds(70, 25, 70, 15);
    add(step_button);

    reset_button.setForeground(Color.decode("#2b2b2b"));
    reset_button.setBackground(Color.decode("#94b3b5"));
    reset_button.setBounds(140, 25, 70, 15);
    add(reset_button);

    exit_button.setForeground(Color.decode("#2b2b2b"));
    exit_button.setBackground(Color.decode("#94b3b5"));
    exit_button.setBounds(210, 25, 70, 15);
    add(exit_button);

    for(int i = 0; i < no_virtual_pages; i++){
      int x_offset = 140 * (i % (no_virtual_pages / 2));

      button_pages[i].setForeground(Color.decode("#2b2b2b"));
      button_pages[i].setBackground(Color.decode("#94b3b5"));
      button_pages[i].setBounds(x_offset, 55 + i * 15, 70, 15);
      add(button_pages[i]);
      
      label_pages[i].setBounds(x_offset + 70, 55 + i * 15, 70, 15);
      label_pages[i].setForeground(Color.red);
      label_pages[i].setFont(new Font("Helvetica", Font.PLAIN, 10));
      add(label_pages[i]);
    }

    status_Label.setBounds(345, 25, 100, 15);
    add(address_Label);

    time_Label.setBounds(345, 40 , 100, 15);
    add(time_Label);

    instruction_Label.setBounds(345, 70 , 100, 15);
    add(instruction_Label);

    address_Label.setBounds(345, 85 , 100, 15);
    add(address_Label);

    segmentation_Label.setBounds(345, 100 , 100, 15);
    add(segmentation_Label);

    page_fault_Label.setBounds(345, 115 , 100, 15);
    add(page_fault_Label);

    virtual_page_Label.setBounds(345, 145 , 100, 15);
    add(virtual_page_Label);

    physical_page_Label.setBounds(345, 160 , 100, 15);
    add(physical_page_Label);

    referenced_Label.setBounds(345, 175 , 100, 15);
    add(referenced_Label);

    modified_Label.setBounds(345, 190 , 100, 15);
    add(modified_Label);

    in_mem_time_Label.setBounds(345, 205 , 100, 15);
    add(in_mem_time_Label);

    last_touch_time_Label.setBounds(345, 220 , 100, 15);
    add(last_touch_time_Label);

    low_limit_address_Label.setBounds(345, 250 , 100, 15);
    add(low_limit_address_Label);

    high_limit_address_Label.setBounds(345, 265 , 100, 15);
    add(high_limit_address_Label);

    Label virtual_one_Label = new Label("Virtual", Label.CENTER);
    virtual_one_Label.setBounds(0, 35 , 70, 15);
    add(virtual_one_Label);

    Label virtual_two_Label = new Label("Virtual", Label.CENTER);
    virtual_two_Label.setBounds(140, 35 , 70, 15);
    add(virtual_one_Label);

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

    kernel.init(commands, config);

    setVisible(true);
  }

  public void paintPage(Page page){
    virtual_page_Label.setText(Integer.toString(page.getID()));
    physical_page_Label.setText(Integer.toString(page.getPhysicalAddress()));
    referenced_Label.setText(Boolean.toString(page.getReferenced()));
    modified_Label.setText(Boolean.toString(page.getModified()));
    in_mem_time_Label.setText(Integer.toString(page.getTimeInMemory()));
    last_touch_time_Label.setText(Integer.toString(page.getTimeSinceTouched()));
    low_limit_address_Label.setText(Long.toString(page.getLowerMemoryLimit(), Kernel2.address_radix));
    high_limit_address_Label.setText(Long.toString(page.getUpperMemoryLimit(), Kernel2.address_radix));
  }

  public void setStatus(String status){
    status_Label.setText(status);
  }

  public void addPhysicalPage(int page_num, int physical_address){
    label_pages[physical_address].setText(Integer.toString(page_num));
  }

  public void removePhysicalPage(int physical_address){
    label_pages[physical_address].setText(null);
  }

  public void action(AWTEvent e, Object arg){
    if(e.getSource() == run_button){
      setStatus("RUN");
      run_button.setEnabled(false);
      step_button.setEnabled(false);
      reset_button.setEnabled(false);
      kernel.run();
      setStatus("STOP");
      reset_button.setEnabled(true);
      return;
    }
    if(e.getSource() == step_button){
      setStatus("STEP");
      if(kernel.getRuns() != kernel.getRunCycles()){ 
        kernel.step(); 
      } else { 
        step_button.setEnabled(false);
        run_button.setEnabled(false);
      }
      setStatus("STOP");
      return;
    }
    if(e.getSource() == reset_button){
      setStatus("RESET");
      run_button.setEnabled(true);
      step_button.setEnabled(true);
      reset_button.setEnabled(false);
      kernel.reset();
      reset_button.setEnabled(true);
      setStatus("STOP");
      return;
    }
    if(e.getSource() == exit_button){
      System.exit(0);
    }
    if(e.getSource() instanceof Button){
      Button button = (Button) e.getSource();
      for(int i = 0; i < no_virtual_pages; i++){
        if(button == button_pages[i]){
          kernel.getPage(i);
          return;
        }
      }
    }
    return;
  }

}