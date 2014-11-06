import java.util.LinkedList;

/**
 * User: Yeap Hooi Tong
 * Date: 14/10/14
 * Time: 6:58 PM
 */
public class VirtualMemory {
    private static final int SEGMENT_FRAME = 0;
    private static VirtualMemory vm;

    private PhysicalMemory pm;
    private LinkedList<int[]> tlb;

    private VirtualMemory() {
        pm = PhysicalMemory.getInstance();
        tlb = new LinkedList<int[]>();
    }

    public static VirtualMemory getInstance() {
        if (vm == null) {
            vm = new VirtualMemory();
        }

        return vm;
    }

    public void setSegmentTable(int segment, int address) {
        assert pm != null;
        if (segment >= 512) {
            System.out.println("Exceed ST Range");
            return;
        }

        if (address >= pm.memory.length || (address < 512 && address > 0)) {
            System.out.println("Out of Memory Range");
            return;
        }

        if (address != -1) {
            /* check whether the frame and +1 is free */
            int neighbour = address + 512 - (address % 512);
            assert pm.isFree(address) && pm.isFree(neighbour) : "Conflict in initialization.";
            pm.occupyFrame(address);
            pm.occupyFrame(neighbour);
        }

        pm.setAddress(SEGMENT_FRAME, segment, address);
    }

    public void setPageTable(int page, int segment, int address) {
        assert pm != null;

        if (address != -1) {
            assert pm.isFree(address) : "Conflict in initialization.";
            pm.occupyFrame(address);
        }

        pm.setAddress(getPageTable(segment) + page, address);

    }

    private int getPageTable(int segment) {
        return pm.getAddress(SEGMENT_FRAME, segment);
    }

    public void read(int va) {
        String binaryString = Integer.toBinaryString(va);
        String padding = "00000000000000000000000000000000";
        String result = padding + binaryString;
        result = result.substring(result.length() - 32, result.length());
        int segment = Integer.parseInt(result.substring(4, 13), 2);
        int page = Integer.parseInt(result.substring(13, 23), 2);
        int offset = Integer.parseInt(result.substring(23, 32), 2);
        int segPA = getPageTable(segment);
        if (segPA == -1) {
            System.out.print("pf ");
        } else if (segPA == 0) {
            System.out.print("err ");
        } else {
            int pagePA = pm.getAddress(segPA + page);
            if (pagePA == -1) {
                System.out.print("pf ");
            } else if (pagePA == 0) {
                System.out.print("err ");
            } else {
                System.out.print((pagePA + offset) + " ");
            }
        }
    }

    public void write(int va) {
        String binaryString = Integer.toBinaryString(va);
        String padding = "00000000000000000000000000000000";
        String result = padding + binaryString;
        result = result.substring(result.length() - 32, result.length());
        int segment = Integer.parseInt(result.substring(4, 13), 2);
        int page = Integer.parseInt(result.substring(13, 23), 2);
        int offset = Integer.parseInt(result.substring(23, 32), 2);
        int segPA = getPageTable(segment);

        if (segPA == -1) {
            System.out.print("pf ");
        } else {
            if (segPA == 0) {
                int freeFrame = pm.allocatePageTable();
                pm.setAddress(0, segment, freeFrame * 512);
                segPA = freeFrame * 512;
            }

            int pagePA = pm.getAddress(segPA + page);

            if (pagePA == -1) {
                System.out.print("pf ");
            } else {
                if (pagePA == 0) {
                    int freeFrame = pm.allocatePage();
                    pm.setAddress(segPA + page, freeFrame * 512);
                    pagePA = pm.getAddress(segPA + page);
                }

                System.out.print((pagePA + offset) + " ");
            }
        }
    }

    public void readTLB(int va) {
        String binaryString = Integer.toBinaryString(va);
        String padding = "00000000000000000000000000000000";
        String result = padding + binaryString;
        result = result.substring(result.length() - 32, result.length());
        int sp = Integer.parseInt(result.substring(4, 23), 2);
        int offset = Integer.parseInt(result.substring(23, 32), 2);
        for (int[] m : tlb) {
            if (m[0] == sp) {
                /* hit */
                System.out.print("h ");
                System.out.print((m[1] + offset) + " ");
                tlb.remove(m);
                tlb.addFirst(m);
                return;
            }
        }

        /* miss */
        int segment = Integer.parseInt(result.substring(4, 13), 2);
        int page = Integer.parseInt(result.substring(13, 23), 2);
        int segPA = getPageTable(segment);

        if (segPA == -1) {
            System.out.print("pf ");
        } else if (segPA == 0) {
            System.out.print("err ");
        } else {
            int pagePA = pm.getAddress(segPA + page);
            if (pagePA == -1) {
                System.out.print("pf ");
            } else if (pagePA == 0) {
                System.out.print("err ");
            } else {
                System.out.print("m ");
                System.out.print((pagePA + offset) + " ");
                int[] k = new int[2];
                k[0] = sp;
                k[1] = pagePA;
                tlb.addFirst(k);
                if (tlb.size() > 4) {
                    tlb.removeLast();
                }
            }
        }
    }

    public void writeTLB(int va) {
        String binaryString = Integer.toBinaryString(va);
        String padding = "00000000000000000000000000000000";
        String result = padding + binaryString;
        result = result.substring(result.length() - 32, result.length());
        int sp = Integer.parseInt(result.substring(4, 23), 2);
        int offset = Integer.parseInt(result.substring(23, 32), 2);
        for (int[] m : tlb) {
            if (m[0] == sp) {
                /* hit */
                System.out.print("h ");
                System.out.print((m[1] + offset) + " ");
                tlb.remove(m);
                tlb.addFirst(m);
                return;
            }
        }

        /* miss */
        int segment = Integer.parseInt(result.substring(4, 13), 2);
        int page = Integer.parseInt(result.substring(13, 23), 2);
        int segPA = getPageTable(segment);

        if (segPA == -1) {
            System.out.print("pf ");
        } else {
            if (segPA == 0) {
                int freeFrame = pm.allocatePageTable();
                pm.setAddress(0, segment, freeFrame * 512);
                segPA = freeFrame * 512;
            }

            int pagePA = pm.getAddress(segPA + page);
            if (pagePA == -1) {
                System.out.print("pf ");
            } else {
                if (pagePA == 0) {
                    int freeFrame = pm.allocatePage();
                    pm.setAddress(segPA + page, freeFrame * 512);
                    pagePA = pm.getAddress(segPA + page);
                }
                System.out.print("m ");
                System.out.print((pagePA + offset) + " ");
                int[] k = new int[2];
                k[0] = sp;
                k[1] = pagePA;
                tlb.addFirst(k);
                if (tlb.size() > 4) {
                    tlb.removeLast();
                }
            }
        }
    }
}
