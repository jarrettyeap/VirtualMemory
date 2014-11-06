import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * User: Yeap Hooi Tong
 * Date: 14/10/14
 * Time: 6:58 PM
 */
public class Driver {

    private final static Charset ENCODING = StandardCharsets.UTF_8;
    private static Path initFilePath, transFilePath;
    private static boolean isTLB;
    private static VirtualMemory vm;

    public static void main(String[] args) {
        /* Check for 3 Arguments */
        /* 1 for TLB mode  1 for initialisation, 1 for translation */
        if (args.length != 3) {
            System.out.println("Invalid Start. Please specify two files and mode.");
            System.exit(0);
        }

        vm = VirtualMemory.getInstance();
        isTLB = args[0].equals("1");
        initFilePath = Paths.get(args[1]);
        transFilePath = Paths.get(args[2]);
        try {
            readInit(); /* Read Initialization File */
            readTrans();  /* Read Translation File */
        } catch (IOException e) {
            System.out.println("There is a problem opening the files.");
        }
    }

    private static void readInit() throws IOException {
    /* Read Initialisation File */
        try (Scanner scanner = new Scanner(initFilePath, ENCODING.name())) {
            String[] ptAddress = scanner.nextLine().split("\\s+");
            for (int i = 0; i < ptAddress.length; i += 2) {
                vm.setSegmentTable(Integer.parseInt(ptAddress[i]),
                    Integer.parseInt(ptAddress[i + 1]));
            }

            String[] pageAddress = scanner.nextLine().split("\\s+");
            for (int i = 0; i < pageAddress.length; i += 3) {
                vm.setPageTable(Integer.parseInt(pageAddress[i]),
                    Integer.parseInt(pageAddress[i + 1]), Integer.parseInt(pageAddress[i + 2]));
            }
        }
    }

    private static void readTrans() throws IOException {
    /* Read Initialisation File */
        try (Scanner scanner = new Scanner(transFilePath, ENCODING.name())) {
            String[] ops = scanner.nextLine().split("\\s+");
            for (int i = 0; i < ops.length; i += 2) {
                boolean RW = Integer.parseInt(ops[i]) != 0;
                if (isTLB) {
                    if (!RW) {
                        vm.readTLB(Integer.parseInt(ops[i + 1]));
                    } else {
                        vm.writeTLB(Integer.parseInt(ops[i + 1]));
                    }
                } else {
                    if (!RW) {
                        vm.read(Integer.parseInt(ops[i + 1]));
                    } else {
                        vm.write(Integer.parseInt(ops[i + 1]));
                    }
                }

            }
        }
    }

}
