public class Instruction {
    private String instruction;
    private long min_address;
    private long max_address;
    
    Instruction(String instruction, String type, String min_address, String max_address) {
        this.instruction = instruction;
        byte radix = 10;
        switch (type) {
            case "bin":
            radix = 2;
            break;

            case "oct":
            radix = 8;
            break;

            case "hex":
            radix = 16;
            break;

            default:
            break;
        }
        this.min_address = Long.parseLong(min_address, radix);
        this.max_address = max_address != null ? Long.parseLong(max_address, radix) : this.min_address;
        this.min_address = Math.min(this.min_address, this.max_address);
        this.max_address = Math.max(this.min_address, this.max_address);
    }

    Instruction(String instruction, long address) {
        this.instruction = instruction;
        if (address < 0) {
            System.out.println("Instruction: error, address '" + address + "' is out of range.");
            System.exit(-1);
        }
        this.min_address = address;
        this.max_address = address;
    }

    public String getInstruction() {
        return this.instruction;
    }

    public long getMinAddress() {
        return this.min_address;
    }

    public long getMaxAddress() {
        return this.max_address;
    }

    public void isInRange(long MAX) {

        if (min_address < 0 || min_address > MAX) {
            System.out.println("Instruction: error, address '" + min_address + "' is out of range.");
            System.exit(-1);
        }
        if (max_address < 0 || max_address > MAX) {
            System.out.println("Instruction: error, address '" + max_address + "' is out of range.");
            System.exit(-1);
        }
    }

}
