import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VirtualMemoryTest {
    private static PhysicalMemory pm = PhysicalMemory.getInstance();
    private static VirtualMemory vm = VirtualMemory.getInstance();

    @BeforeClass
    public static void setUp() {
        vm.setSegmentTable(0, 2048);
        vm.setSegmentTable(50, 768);
        vm.setSegmentTable(5, -1);
        vm.setSegmentTable(7, 12840);
        vm.setPageTable(5, 0, 3300);
        vm.setPageTable(6, 50, 9500);
        vm.setPageTable(6, 7, -1);
    }

    @Test
    public void testSetSegmentTable() throws Exception {
        assertEquals(2048, pm.getAddress(0));
        assertEquals(768, pm.getAddress(50));
        assertEquals(false, pm.isFree(0));
        assertEquals(false, pm.isFree(50));
    }

    @Test
    public void testSetPageTable() throws Exception {
        assertEquals(3300, pm.getAddress(2053));
        assertEquals(9500, pm.getAddress(774));
    }

    @Test
    public void testRead() throws Exception {
        String segment = "000000000";
        String page = "0000000101";
        String offset = "000000111";
        String va = segment + page + offset;
        vm.read(Integer.parseInt(va, 2)); //3307

        segment = "000110010";
        page = "0000000110";
        offset = "000000111";
        va = segment + page + offset;
        vm.read(Integer.parseInt(va, 2)); //9507

        segment = "000110011";
        page = "0000000110";
        offset = "000000111";
        va = segment + page + offset;
        vm.read(Integer.parseInt(va, 2)); //error

        segment = "000000111";
        page = "0000000110";
        offset = "000000111";
        va = segment + page + offset;
        vm.read(Integer.parseInt(va, 2)); //pf
    }

    @Test
    public void testWrite() throws Exception {
        String segment = "000000000";
        String page = "0000000100";
        String offset = "000000111";
        String va = segment + page + offset;
        vm.write(Integer.parseInt(va, 2));

        segment = "000110010";
        page = "0000000110";
        offset = "000000111";
        va = segment + page + offset;
        vm.write(Integer.parseInt(va, 2)); //9507

        segment = "000000111";
        page = "0000000110";
        offset = "000000111";
        va = segment + page + offset;
        vm.write(Integer.parseInt(va, 2));
    }
}
