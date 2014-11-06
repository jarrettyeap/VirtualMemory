/**
 * User: Yeap Hooi Tong
 * Date: 14/10/14
 * Time: 6:58 PM
 */
public class PhysicalMemory {
    private static PhysicalMemory pm;
    public int[] bitmap;
    public int[] memory;
    private int[] mask;
    private int[] invertMask;

    private PhysicalMemory() {
        this.bitmap = new int[32];
        bitmap[0] = 1 << 31;
        this.memory = new int[524288];
        initMask();
    }

    public static PhysicalMemory getInstance() {
        if (pm == null) {
            pm = new PhysicalMemory();
        }

        return pm;
    }

    public int getAddress(int frame, int offset) {
        int i = (frame * 512) + offset;
        return memory[i];
    }

    public int getAddress(int pa) {
        return memory[pa];
    }

    public void setAddress(int frame, int offset, int word) {
        int i = (frame * 512) + offset;
        memory[i] = word;
    }

    public void setAddress(int pa, int word) {
        memory[pa] = word;
    }

    /* Search for two consecutive free frames */
    public int allocatePageTable() {
        for (int i = 0; i < bitmap.length; i++) {
            for (int j = 0; j < 32; j++) {
                /* ignore first frame */
                if (i == 0 && j == 0) {
                    continue;
                }
                int frame = bitmap[i] & mask[j];
                if (frame == 0) {
                    /* check for neighbour frame */
                    int n = i;
                    int m = j + 1;
                    int neighbour;
                    if (m == 32) {
                        m = 0;
                        n++;
                        neighbour = bitmap[n] & mask[m];
                    } else {
                        neighbour = bitmap[n] & mask[m];
                    }

                    if (neighbour == 0) {
                        /* this pair of frames can be allocated */
                        occupyFrame(i, j);
                        occupyFrame(n, m);
                        return (i * 32) + j;
                    }
                }
            }
        }

        assert false : "Should not be full.";
        return -1;
    }

    /* Search for one free frame to allocate */
    public int allocatePage() {
        for (int i = 0; i < bitmap.length; i++) {
            for (int j = 0; j < 32; j++) {
                /* ignore first frame */
                if (i == 0 && j == 0) {
                    continue;
                }
                int frame = bitmap[i] & mask[j];
                if (frame == 0) {
                    occupyFrame(i, j);
                    return (i * 32) + j;
                }
            }
        }

        assert false : "Should not be full.";
        return -1;
    }

    public boolean isFree(int i, int j) {
        return (bitmap[i] & mask[j]) == 0;
    }

    public boolean isFree(int pa) {
        if (pa == -1) {
            return true;
        }
        int frame = pa / 512;
        int i = frame / 32;
        int j = frame % 32;
        return isFree(i, j);
    }

    public void occupyFrame(int pa) {
        int frame = pa / 512;
        int i = frame / 32;
        int j = frame % 32;
        occupyFrame(i, j);
    }

    public void occupyFrame(int i, int j) {
        bitmap[i] = bitmap[i] | mask[j];
    }

    public void freeFrame(int i, int j) {
        bitmap[i] = bitmap[i] & invertMask[j];
    }

    private void initMask() {
        mask = new int[32];
        mask[31] = 1;
        for (int i = 30; i >= 0; i--) {
            mask[i] = mask[i + 1] << 1;
        }

        invertMask = new int[32];
        for (int i = 0; i < invertMask.length; i++) {
            invertMask[i] = ~mask[i];
        }
    }
}
