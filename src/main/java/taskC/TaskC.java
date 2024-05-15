package taskC;

import java.io.*;
import java.util.*;

public class TaskC {

    /**
     * Size of TLB entries.
     */
    private static final int tlb_size = 4;

    /**
     * Size of pages.
     */
    private static final int page_size = 4 * 1024;

    /**
     * Initially given virtual addresses.
     */
    private static final List<Integer> given_virtual_addresses = new ArrayList<>();

    /**
     * TLB entries list to store TLB entries.
     */
    private static List<TLBEntry> TLB = new ArrayList<>();

    /**
     * Page table
     */
    private static Map<Integer, PageTableEntry> page_table = new HashMap<>();

    /**
     * Next physical page number to be used for page table.
     */
    private static int next_physical_page = 13;


    /**
     * Main function for memory management.
     * @param args command line arguments
     * @throws IOException if an I/O error occurs
     */
    public static void main(String[] args) {
        List<String> file_data = new ArrayList<>();
        try {
            File file = new File("taskC.txt");
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                file_data.add(reader.nextLine());
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("taskC.txt not found.");
        }

        // Find the indexes of the given data in the file

        int virtual_addresses = file_data.indexOf("#Address");
        int tlb_start = file_data.indexOf("#Initial TLB");
        int page_table_start = file_data.indexOf("#Initial Page table");

        List<String> tlb = new ArrayList<>();
        List<String> page_table = new ArrayList<>();
        for (int i = virtual_addresses + 1; i < tlb_start; i++) {
            String hex = file_data.get(i).split("x")[1];
            given_virtual_addresses.add(Integer.parseInt(hex, 16));
        }

        for (int i = tlb_start + 2; i < page_table_start; i++) {
            tlb.add(file_data.get(i));
        }

        for (int i = page_table_start + 2; i < file_data.size(); i++) {
            page_table.add(file_data.get(i));
        }

        initializeTLB(tlb);
        initializePageTable(page_table);

        try {
            processAddresses();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Initialize the TLB.
     */
    private static void initializeTLB(List<String> tlb_entries) {
        for (String tlb_entry : tlb_entries) {
            String[] parts = tlb_entry.split(",");
            TLB.add(new TLBEntry(
                    Integer.parseInt(parts[0]),
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2]),
                    Integer.parseInt(parts[3])
            ));
        }
    }


    /**
     * Initialize page table.
     */
    private static void initializePageTable(List<String> page_table_entries) {
        for (String page_table_entry : page_table_entries) {
            String[] parts = page_table_entry.split(",");
            page_table.put(
                    Integer.parseInt(parts[0]),
                    new PageTableEntry(
                            Integer.parseInt(parts[1]),
                            parts[2].trim()
                    ));
        }
    }


    /**
     * Process all given virtual addresses.
     * @throws IOException if an I/O error occurs
     */
    private static void processAddresses() throws IOException {
        List<String> results = new ArrayList<>();
        for (int address : given_virtual_addresses) {
            String result = processAddress(address);

            // Add results to the list

            results.add(String.format("# After the memory access 0x%04X", address));
            results.add("#Address, Result (Hit, Miss, PageFault)");
            results.add(String.format("0x%04X,", address) + result);
            results.add("#updated TLB\n" + "#Valid, Tag, Physical Page #, LRU");
            for (TLBEntry entry : TLB) {
                results.add(entry.toString());
            }
            results.add("#updated Page table\n" + "#Index,Valid,Physical Page or On Disk");
            for (Map.Entry<Integer, PageTableEntry> pageTableEntry :
                    page_table.entrySet()) {
                results.add(pageTableEntry.getKey() + ","
                        + pageTableEntry.getValue().toString());
            }
        }

        writeResults(results);
    }


    /**
     * Process a given virtual address.
     * @param virtual_addresses virtual address to process
     * @return result of the processing
     */
    private static String processAddress(int virtual_addresses) {
        int page_number = virtual_addresses / page_size;

        for (TLBEntry entry : TLB) {
            if (entry.valid == 1 && entry.tag == page_number) {
                updateLRU(entry);
                return "Hit";
            }
        }

        // If the page is not in the TLB, check the page table

        if (page_number >= page_table.size() || page_table.get(page_number).valid == 0) {
            page_table.put(page_number, new PageTableEntry
                    (1, String.valueOf(next_physical_page++)));
            updateTLB(page_number, next_physical_page - 1);
            return "Page fault";
        } else {
            updateTLB(page_number, Integer.parseInt(page_table.get(page_number)
                    .physical_page_or_disk));
            return "Miss";
        }
    }


    /**
     * Update TLB with the given tag and physical page number.
     * @param tag tag to update
     * @param physical_page physical page number to update
     */
    private static void updateTLB(int tag, int physical_page) {
        TLBEntry lruEntry = TLB.stream().filter(entry -> entry.valid == 0)
                .findFirst().orElseGet(() -> Collections.min
                        (TLB, Comparator.comparingInt(e -> e.lru)));

        lruEntry.valid = 1;
        lruEntry.tag = tag;
        lruEntry.physical_page = physical_page;
        updateLRU(lruEntry);
    }


    /**
     * Update LRU values of TLB entries.
     * @param used_entry used entry to update LRU values
     */
    private static void updateLRU(TLBEntry used_entry) {
        int usedLRU = used_entry.lru;
        used_entry.lru = 4;
        for (TLBEntry entry : TLB) {
            if (entry != used_entry && entry.lru > usedLRU) {
                entry.lru--;
            }
        }
    }


    /**
     * Write results to a file.
     * @param results results to write
     * @throws IOException if an I/O error occurs
     */
    private static void writeResults(List<String> results) throws IOException {
        try (BufferedWriter writer = new BufferedWriter
                (new FileWriter("taskc-sampleoutput.txt"))) {
            for (String result : results) {
                writer.write(result);
                writer.newLine();
            }
        }
    }


    /**
     * Page table entry class.
     * Contains valid bit and physical page or disk information.
     */
    static class PageTableEntry {
        int valid;
        String physical_page_or_disk;

        PageTableEntry(int valid, String physical_page_or_disk) {
            this.valid = valid;
            this.physical_page_or_disk = physical_page_or_disk;
        }

        /**
         * Returns a string representation of the object.
         * @return string representation of the object
         */
        @Override
        public String toString() {
            return valid + "," + physical_page_or_disk;
        }
    }


    /**
     * TLB entry class.
     * Contains valid bit, tag, physical page and LRU information.
     */
    static class TLBEntry {
        int valid;
        int tag;
        int physical_page;
        int lru;

        TLBEntry(int valid, int tag, int physical_page, int lru) {
            this.valid = valid;
            this.tag = tag;
            this.physical_page = physical_page;
            this.lru = lru;
        }

        /**
         * Returns a string representation of the object.
         * @return string representation of the object
         */
        @Override
        public String toString() {
            return valid + "," + tag + "," + physical_page + "," + lru;
        }
    }
}
