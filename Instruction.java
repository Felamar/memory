public class Instruction 
{
  public String inst;
  public long addr_min;
  public long addr_max;
  public long range;

  public Instruction( String inst, long addr_min, long addr_max) 
  {
    this.inst = inst;
    this.addr_min = Math.min(addr_min, addr_max);
    this.addr_max = Math.max(addr_min, addr_max);
    this.range = Math.abs(addr_max - addr_min);
  }
}
