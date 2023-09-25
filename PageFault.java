/* It is in this file, specifically the replacePage function that will
  be called by MemoryManagement when there is a page fault.  The 
  users of this program should rewrite PageFault to implement the 
  page replacement algorithm.
*/

// This PageFault file is an example of the FIFO Page Replacement 
// Algorithm as described in the Memory Management section.

import java.util.*;
// import Page;

public class PageFault {

  /**
   * The page replacement algorithm for the memory management sumulator.
   * This method gets called whenever a page needs to be replaced.
   * <p>
   * The page replacement algorithm included with the simulator is
   * FIFO (first-in first-out). A while or for loop should be used
   * to search through the current memory contents for a canidate
   * replacement page. In the case of FIFO the while loop is used
   * to find the proper page while making sure that virtPageNum is
   * not exceeded.
   * 
   * <pre>
   *   Page page = ( Page ) mem.elementAt( oldestPage )
   * </pre>
   * 
   * This line brings the contents of the Page at oldestPage (a
   * specified integer) from the mem vector into the page object.
   * Next recall the contents of the target page, replacePageNum.
   * Set the physical memory address of the page to be added equal
   * to the page to be removed.
   * 
   * <pre>
   * controlPanel.removePhysicalPage(oldestPage)
   * </pre>
   * 
   * Once a page is removed from memory it must also be reflected
   * graphically. This line does so by removing the physical page
   * at the oldestPage value. The page which will be added into
   * memory must also be displayed through the addPhysicalPage
   * function call. One must also remember to reset the values of
   * the page which has just been removed from memory.
   *
   * @param mem            is the vector which contains the contents of the pages
   *                       in memory being simulated. mem should be searched to
   *                       find the
   *                       proper page to remove, and modified to reflect any
   *                       changes.
   * @param virtPageNum    is the number of virtual pages in the
   *                       simulator (set in Kernel.java).
   * @param replacePageNum is the requested page which caused the
   *                       page fault.
   * @param controlPanel   represents the graphical element of the
   *                       simulator, and allows one to modify the current
   *                       display.
   */
  public static void replacePage(Vector<Page> mem, int virtPageNum, int replacePageNum, ControlPanel controlPanel) {
    int count = 0;
    int oldestPage = -1;
    int oldestTime = 0;
    int firstPage = -1;
    boolean mapped = false;

    while (!(mapped) || count != virtPageNum) {
      Page page = (Page) mem.elementAt(count);
      if (page.getPhysicalAddress() != -1) {
        if (firstPage == -1) {
          firstPage = count;
        }
        if (page.getTimeInMemory() > oldestTime) {
          oldestTime = page.getTimeInMemory();
          oldestPage = count;
          mapped = true;
        }
      }
      count++;
      if (count == virtPageNum) {
        mapped = true;
      }
    }
    if (oldestPage == -1) {
      oldestPage = firstPage;
    }
    Page page = (Page) mem.elementAt(oldestPage);
    Page nextpage = (Page) mem.elementAt(replacePageNum);
    controlPanel.removePhysicalPage(oldestPage);
    nextpage.setPhysicalAddress(page.getPhysicalAddress());
    controlPanel.addPhysicalPage(nextpage.getPhysicalAddress(), replacePageNum);
    page.setTimeInMemory(0);
    page.setTimeSinceTouched(0);
    page.setReferenced(false);
    page.setModified(false);
    page.setPhysicalAddress(-1);
  }

  public static int replace_by_WSClock(Vector<Page> pages_vector, int no_virtual_pages, int page_ID, 
    ControlPanel control_panel, int current_time, int clock_hand){
      
    int i = clock_hand;
    int frame_size = pages_vector.size();
    final int TAU = 10;
    boolean referenced;
    boolean modified;

    while (true){
      i = (i + 1) % frame_size;
      Page page_to_replace = pages_vector.get(i);
      referenced = page_to_replace.getReferenced();
      modified = page_to_replace.getModified();
      page_to_replace.setReferenced(false);
      System.out.println("i:" + i);
      System.out.println("referenced: " + referenced + " modified: " + modified);
      System.out.println("time in memory: " + page_to_replace.getTimeInMemory()+ " time_since_touched: " + page_to_replace.getTimeSinceTouched());
  
      if (referenced){
        page_to_replace.setReferenced(false);
        page_to_replace.setTimeSinceTouched(current_time);
        continue;
      } 
      if (page_to_replace.getTimeInMemory() - page_to_replace.getTimeSinceTouched() > TAU && modified){
        page_to_replace.setModified(false);
        continue;
      }
      if (page_to_replace.getTimeInMemory() - page_to_replace.getTimeSinceTouched() > TAU && !modified){
        int tr_aux = page_to_replace.getID();
        control_panel.removePhysicalPage(tr_aux);
        Page new_page = pages_vector.get(page_ID);
        new_page.setPhysicalAddress(tr_aux);
        control_panel.addPhysicalPage(tr_aux, page_ID);
        page_to_replace.setPhysicalAddress(-1);
        return i;
      }
    }
  }
}
