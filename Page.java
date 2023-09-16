public class Page {
    private int ID;
    private int physical_address;
    private boolean referenced;
    private boolean modified;
    private int t_in_mem;
    private int t_since_touched;
    private long up_mem_limit;
    private long low_mem_limit;

    Page(int ID, int physical_address, boolean referenced, boolean modified, int t_in_mem, int t_since_touched,
            long up_mem_limit, long low_mem_limit) {
        this.ID               = ID;
        this.physical_address = physical_address;
        this.referenced       = referenced;
        this.modified         = modified;
        this.t_in_mem         = t_in_mem;
        this.t_since_touched  = t_since_touched;
        this.up_mem_limit     = up_mem_limit;
        this.low_mem_limit    = low_mem_limit;
    }

    public int getID() {
        return ID;
    }

    public int getPhysicalAddress() {
        return physical_address;
    }

    public boolean getReferenced() {
        return referenced;
    }

    public boolean getModified() {
        return modified;
    }

    public int getTimeInMemory() {
        return t_in_mem;
    }

    public int getTimeSinceTouched() {
        return t_since_touched;
    }

    public long getUpperMemoryLimit() {
        return up_mem_limit;
    }

    public long getLowerMemoryLimit() {
        return low_mem_limit;
    }

    public void setPhysicalAddress(int physical_address) {
        this.physical_address = physical_address;
    }

    public void setReferenced(boolean referenced) {
        this.referenced = referenced;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public void setTimeInMemory(int t_in_mem) {
        this.t_in_mem = t_in_mem;
    }

    public void setTimeSinceTouched(int t_since_touched) {
        this.t_since_touched = t_since_touched;
    }

    public void setNewValues(String[] args, long no_virtual_pages){
        try{    
            if (args.length != 7) {
                throw new Exception("Page: error, invalid number of arguments.");
            }
            int     new_physical        = Integer.parseInt(args[2]);
            boolean new_referenced      = Integer.parseInt(args[3]) == 1;
            boolean new_modified        = Integer.parseInt(args[4]) == 1;
            int     new_t_in_mem        = Integer.parseInt(args[5]);
            int     new_t_since_touched = Integer.parseInt(args[6]);

            if (new_physical < -1 || physical_address > (no_virtual_pages - 1) / 2) {
                throw new Exception("Page: error, physical address '" + new_physical + "' is out of bounds.");
            }
            if (new_t_in_mem < 0) {
                throw new Exception("Page: error, time in memory '" + new_t_in_mem + "' is out of bounds.");
            }
            if (new_t_since_touched < 0) {
                throw new Exception("Page: error, time since touched '" + new_t_since_touched + "' is out of bounds.");
            }
            setPhysicalAddress(new_physical);
            setReferenced(new_referenced);
            setModified(new_modified);
            setTimeInMemory(new_t_in_mem);
            setTimeSinceTouched(new_t_since_touched);
        }
        catch(Exception e){
            System.out.println("Page: error, " + e.getMessage());
            System.exit(-1);
        }
    }
}
