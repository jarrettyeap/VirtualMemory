import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PhysicalMemoryTest {
    private static PhysicalMemory pm = PhysicalMemory.getInstance();
    private static VirtualMemory vm = VirtualMemory.getInstance();

    @BeforeClass
    public static void setUp() throws Exception {
        vm.setSegmentTable(2, 1024);
        vm.setSegmentTable(5, 2048);
        vm.setSegmentTable(6, 4096);
    }

    @Test
    public void testGetAddress() throws Exception {
        assertEquals(1024, pm.getAddress(0, 2));
        assertEquals(2048, pm.getAddress(0, 5));
        assertEquals(4096, pm.getAddress(0, 6));
    }

    @Test
    public void testGetAddress1() throws Exception {
        assertEquals(1024, pm.getAddress(2));
        assertEquals(2048, pm.getAddress(5));
        assertEquals(4096, pm.getAddress(6));
    }

    @Test
    public void testSetAddress() throws Exception {
        pm.setAddress(4, 5, 2014);
        assertEquals(2014, pm.getAddress(4, 5));
    }

    @Test
    public void testSetAddress1() throws Exception {
        pm.setAddress(2024, 1024);
        assertEquals(1024, pm.getAddress(2024));
    }

    @Test
    public void testAllocatePageTable() throws Exception {
        assertEquals(6, pm.allocatePageTable());
        assertEquals(10, pm.allocatePageTable());
    }

    @Test
    public void testAllocatePage() throws Exception {
        assertEquals(1, pm.allocatePage());
        assertEquals(12, pm.allocatePage());
        assertEquals(13, pm.allocatePage());
        assertEquals(14, pm.allocatePage());
        assertEquals(15, pm.allocatePage());
    }

    @Test
    public void testIsFree() throws Exception {
        assertEquals(false, pm.isFree(0, 0));
        assertEquals(false, pm.isFree(0, 15));
        assertEquals(true, pm.isFree(0, 16));

    }

    @Test
    public void testIsFree1() throws Exception {
        assertEquals(false, pm.isFree(0));
        assertEquals(false, pm.isFree(1500));
        assertEquals(true, pm.isFree(200000));

    }

    @Test
    public void testOccupyFrame() throws Exception {
        assertEquals(true, pm.isFree(1, 2));
        pm.occupyFrame(1, 2);
        assertEquals(false, pm.isFree(1, 2));
    }

    @Test
    public void testOccupyFrame1() throws Exception {
        assertEquals(true, pm.isFree(400000));
        pm.occupyFrame(400000);
        assertEquals(false, pm.isFree(400000));
    }

}
