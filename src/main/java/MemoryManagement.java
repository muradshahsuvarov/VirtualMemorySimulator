import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class MemoryManagement {

    public List<String> VirtualMemory;
    public String[] TLB; // "Page Number,Frame Number" "E.g 11100101,01111011 11010011,01110111 ..."
    public List<String> PageTable; // "Address" "E.g 01101000 11011001 ..." Main Memory = Physical Memory = RAM

    public List<String> translationOutput;

    public MemoryManagement() {

        VirtualMemory = new ArrayList<>();
        TLB = new String[16];
        PageTable = new ArrayList<>();

        // Needed to output the translations into the file
        translationOutput = new ArrayList<String>();

    }



    public String intTo16Binary(Integer _number) {
        return Integer.toBinaryString(0x10000 | _number).substring(1);
    }


    public Integer binaryTo16Int(String _binary) {
        return Integer.parseInt(_binary, 2);
    }

    public String GetPageNumber(int _virtualAddress) {

        String page_number = intTo16Binary(_virtualAddress).substring(0, 8);

        return page_number;

    }

    public Boolean PageIsInTLB(String _pageNumber) {
        if (TLB.length > 0) {
            for (String tlb_item : TLB) {
                if (tlb_item != null) {
                    if (tlb_item.contains(_pageNumber)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Boolean PageIsInPageTable(String _pageNumber) {
        if (PageTable.size() > 0) {
            for (String pageTable_item : PageTable) {
                if (pageTable_item != null) {
                    if (pageTable_item.contains(_pageNumber)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Integer GetIntegerFromBinary(String _binaryNumber) {
        int _number = Integer.parseInt(_binaryNumber, 2);
        return _number;
    }

    public String GetFrameNumber(String _pageNumber) {

        if (PageIsInTLB(_pageNumber)) {

            System.out.println(_pageNumber + " is in the TLB");
            return GetFrameNumberFromTLB(_pageNumber);

        }else if (PageIsInPageTable(_pageNumber)) {

            System.out.println(_pageNumber + " is in the Page Table");
            return GetFrameNumberFromPageTable(_pageNumber);

        }else {
            System.out.println("Page fault occurred!");
            return null;
        }
    }

    public String GetFrameNumberFromTLB(String _pageBinary) {
            for (String tlb_item : TLB ) {
                if (tlb_item.contains(_pageBinary)) {
                    String[] found_tlb_items = tlb_item.split(",");
                    return found_tlb_items[1];
                }
            }
        return "Page " + _pageBinary + " isn't in the TLB";
    }

    public String GetFrameNumberFromPageTable(String _pageBinary) {

            Integer _pageNumber = binaryTo16Int(_pageBinary);
            for (int i = 0; i < PageTable.size(); i++) {
                if (i == _pageNumber) {
                    return PageTable.get(i).split(",")[0];
                }
            }

        return "Page " + _pageNumber + " isn't in the TLB";
    }

    public  void ReadDisk() throws IOException {
        String filePath = "disk_sim";
        //Instantiating the File class
        File file = new File(filePath);
        //Instantiating the StringBuffer
        StringBuffer buffer = new StringBuffer();
        //instantiating the RandomAccessFile
        RandomAccessFile raFile = new RandomAccessFile(file, "rw");
        //Reading each line using the readLine() method
        while(raFile.getFilePointer() < raFile.length()) {
            int _val = raFile.read();
            buffer.append(_val + " = " + intTo16Binary(_val)+System.lineSeparator());
        }
        String contents = buffer.toString();

        System.out.println("Contents: \n" + contents);


    }

}
