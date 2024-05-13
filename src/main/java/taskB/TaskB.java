package taskB;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * Main function for memory management.
 */
public class TaskB {

    // size of memory
    public static final int TOTAL_BYTES = 1024;

    /*
     * The first number is the reference id of job. The second number is a request
     * to allocate or deallocate. (1 - Allocate, 2 - Deallocate) The third number if
     * allocate will try and allocate those amount of bytes into a memory stack of
     * 1024 bytes. If it is deallocate, the third argument will be the reference id
     * to deallocate from memory.
     *
     * NOTE: you should read the process input from taskB.txt instead of putting it
     * in the Java file
     */
    private static List<int[]> alloc = new ArrayList<>();

    // Keep track of all processes created
    private static ArrayList<Process> listof_processes;

    public static void createProcesses() {
        Process proc;

        listof_processes = new ArrayList<>();
        for (int[] parts : alloc) {
            proc = new Process(parts[0], parts[1], parts[2]);
            listof_processes.add(proc);
        }
    }

    /**
     * This method runs the Best Fit Memory Allocation simulation using a linked
     * list. Loops through the Processes in the Process list and allocates
     * appropriately. If it cannot allocate, it will fail and print why accordingly.
     * If it succeeds it will print 'Success'.
     */
    private static void bestFit() {
        MainMemory manager = new MainMemory();
        manager.insertAtStart(new Block());

        for (Process proc : listof_processes) {
            if (proc.isAllocating()) {
                boolean placed = manager.bestFitInsert(proc);
                if (!placed) {
                    System.out.println("Request " + proc.getReference_number()
                            + " failed at allocating " + proc.getArgument() + " bytes.");
                    System.out.println("External Fragmentation is "
                            + manager.externalFragmentation() + " bytes.");
                    manager.printBlocks();
                    manager.compactMemory();
                    System.out.println("-------After Compaction ------");
                    manager.printBlocks();

                    boolean retry = manager.bestFitInsert(proc);
                    if (!retry) {
                        System.out.println("Request " + proc.getReference_number()
                                + " failed at allocating " + proc.getArgument() + " bytes.");
                        System.out.println("External Fragmentation is "
                                + manager.externalFragmentation() + " bytes.");
                        return;
                    }
                }
            } else if (proc.isDeallocating()) {
                manager.deallocateBlock(proc.getArgument());
            }
        }
        System.out.println("Success");
        manager.printBlocks();
    }

    /**
     * Main function for memory management.
     * @param args command line arguments
     */

    public static void main(String[] args) {
        try {
            File file = new File("taskB.csv");
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] chunks = line.split(",");

                int reference_number = Integer.parseInt(chunks[0]);
                int operation = Integer.parseInt(chunks[1]);
                int argument = Integer.parseInt(chunks[2]);

                alloc.add(new int[]{reference_number, operation, argument});
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
        }
        createProcesses();

        System.out.println("----------Best Fit----------");
        bestFit();
    }
}
