package taskB;

import java.util.ArrayList;

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
    private static int[][] alloc = { /* memory size is 1024 */
	    // Example 1
	    /*
	     * {1, 1, 30}, //Process 1 is allocating 30 bytes {2, 1, 40}, //Process 2 is
	     * allocating 40 bytes {3, 1, 50}, {4, 2, 1}
	     */

	    // Example 2
	    /*
	     * {1, 1, 300}, {2, 1, 150}, {3, 1, 200}, {4, 2, 1}
	     */

	    // Example 3: more processes

	    { 1, 1, 150 }, { 2, 1, 20 }, { 3, 1, 150 }, { 4, 1, 20 }, { 5, 1, 150 }, { 6, 1, 20 }, { 7, 1, 150 },
	    { 8, 1, 20 }, { 9, 1, 150 }, { 10, 1, 20 }, { 11, 1, 150 }, { 12, 1, 20 }, { 13, 2, 2 }, { 14, 2, 4 },
	    { 15, 2, 6 }, { 16, 2, 8 }, { 17, 2, 10 }, { 18, 2, 12 }, { 19, 1, 20 }, { 20, 1, 21 }, { 21, 1, 125 }

    };

    // Keep track of all processes created
    private static ArrayList<Process> listof_processes;

    public static void createProcesses() {
	Process proc;

	listof_processes = new ArrayList<>();
	for (int i = 0; i < alloc.length; i++) {
	    proc = new Process(alloc[i][0], alloc[i][1], alloc[i][2]);
	    listof_processes.add(proc);
	}
    }

    /**
     * This method runs the First Fit Memory Allocation simulation using a linked
     * list. Loops through the Processes in the Process list and allocates
     * appropriately. If it cannot allocate, it will fail and print why accordingly.
     * If it succeeds it will print 'Success'.
     */
    private static void firstFit() {

	MainMemory manager = new MainMemory();
	manager.insertAtStart(new Block());

	for (Process proc : listof_processes) {

	    if (proc.isAllocating()) {
		boolean placed = manager.firstFitInsert(proc);
		// externalFragmentation has not been implemented
		/*
		 * if(!placed){ System.out.println("Request " + proc.getReference_number() +
		 * " failed at allocating " + proc.getArgument() + " bytes." );
		 * System.out.println("External Fragmentation is " +
		 * manager.externalFragmentation() + " bytes."); //memory print
		 * manager.printBlocks(); return; }
		 */
	    } else if (proc.isDeallocating()) {
		manager.deallocateBlock(proc.getArgument());
	    }
	}

	System.out.println("Success");
	// memory print
	manager.printBlocks();
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
		// you should calculate the total bytes of the external fragmentation
		if (!placed) {
		    System.out.println("Request " + proc.getReference_number() + " failed at allocating "
			    + proc.getArgument() + " bytes.");
		    System.out.println("External Fragmentation is " + manager.externalFragmentation() + " bytes.");
		    // memory print
		    manager.printBlocks();
		    return;
		}
	    } else if (proc.isDeallocating()) {
		manager.deallocateBlock(proc.getArgument());
	    }
	}
	System.out.println("Success");
	// memory print
	manager.printBlocks();
    }

    public static void main(String[] args) {

	createProcesses();

	System.out.println("----------First Fit - for demo only, you can remove this part ---------");
	firstFit();

	System.out.println("----------Best Fit - to be implemented ---------");
	bestFit();

    }

}
