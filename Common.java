public class Common {

    static public long s2l(String s) {
        long i = 0;

        try {
            i = Long.parseLong(s.trim());
        } catch (NumberFormatException nfe) {
            System.out.println("NumberFormatException: " + nfe.getMessage());
        }

        return i;
    }

    static public int s2i(String s) {
        int i = 0;

        try {
            i = Integer.parseInt(s.trim());
        } catch (NumberFormatException nfe) {
            System.out.println("NumberFormatException: " + nfe.getMessage());
        }

        return i;
    }

    static public byte s2b(String s) {
        int i = 0;
        byte b = 0;

        try {
            i = Integer.parseInt(s.trim());
        } catch (NumberFormatException nfe) {
            System.out.println("NumberFormatException: " + nfe.getMessage());
        }
        b = (byte) i;
        return b;
    }

    public static long randomLong(long MAX) {
        return (long) (Math.random() * MAX);
    }
}
