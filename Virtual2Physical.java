public class Virtual2Physical {
  public static int pageNum(long memory_address, int no_pages, long block_size) {
    long n_page = memory_address / block_size;
    return n_page >= 0 && n_page < no_pages ? (int) n_page : -1;
  }
}