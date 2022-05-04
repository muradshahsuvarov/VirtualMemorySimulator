import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VirtualMemory {

    public List<String> TLB; // "Page Number,Frame Number" "E.g 0,23 35,23 ..."
    public List<String> PageTable; // "Page Number,Frame Number" "E.g 0,23 35,23 ..."
    public List<String> PhysicalMemoryRAM; // "Address"

    public VirtualMemory() {

        TLB = new ArrayList<String>();
        PageTable = new ArrayList<String>();
        PhysicalMemoryRAM = new ArrayList<String>();

        TLB.add("10000010,24");

    }



    private String intToBi16Binary(int _logicalAddress) {
        return Integer.toBinaryString(0x10000 | _logicalAddress).substring(1);
    }

    public String GetPageNumber(int _virtualAddress) {

        String page_number = intToBi16Binary(_virtualAddress).substring(8, 16);

        return page_number;
    }

    public Boolean PageIsInTLB(String _pageNumber) {
        for (String tlb_item : TLB) {
            if (tlb_item.contains(_pageNumber)) {
                return true;
            }
        }
        return false;
    }

    public Boolean PageIsInPageTableRAM(String _pageNumber) {
        for (String page_table_ram_item : TLB) {
            if (page_table_ram_item.contains(_pageNumber)) {
                return true;
            }
        }
        return false;
    }

    public String GetFrameNumber(String _pageNumber) {

        if (PageIsInTLB(_pageNumber)) {

            System.out.println(_pageNumber + " is in TLB");
            return GetFrameNumberFromTLB(_pageNumber);

        }else if (PageIsInPageTableRAM(_pageNumber)) {

            System.out.println(_pageNumber + " is in Page Table RAM");
            return GetFrameNumberFromPageTable(_pageNumber);

        }else {
            System.out.println("Page fault occurred!");
            return null;
        }
    }

    public String GetFrameNumberFromTLB(String _pageNumber) {
            for (String tlb_item : TLB ) {
                if (tlb_item.contains(_pageNumber)) {
                    String[] found_tlb_items = tlb_item.split(",");
                    String found_tlb_page = found_tlb_items[1];
                    return found_tlb_page;
                }
            }
        return "Page " + _pageNumber + " isn't in the TLB";
    }

    public String GetFrameNumberFromPageTable(String _pageNumber) {
            for (String page_item : PageTable ) {
                if (page_item.contains(_pageNumber)) {
                    String[] found_ram_items = page_item.split(",");
                    String found_ram_item = found_ram_items[1];
                    return found_ram_item;
                }
            }
        return "Page " + _pageNumber + " isn't in the TLB";
    }

    public  void ReadDisk() throws IOException {
        RandomAccessFile in = new RandomAccessFile("disk_sim", "r");
        int version = in.readInt();
        byte type = in.readByte();
        int beginOfData = in.readInt();
        byte[] tempId = new byte[16];
        in.read(tempId, 0, 16);
        String id = new String(tempId);
        try{
            while(true){
                System.out.println("Disk Address: " + in.readShort() + ", Disk Byte Value: " + in.readByte());
            }
        }catch (Exception e) {
            System.out.println("End of file occurred!");
        }
    }

}
