import jdk.internal.util.xml.impl.Input;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class MemoryManagement {

    public List<String> VirtualMemory;
    public List<String> TLB; // "Page Number,Frame Number" "E.g 11100101,01111011 11010011,01110111 ..."
    public List<String> PageTable; // "Address" "E.g 01101000 11011001 ..." Main Memory = Physical Memory = RAM

    public List<String> translationOutput;
    public List<String> diskData;


    public Integer InputCounter = 0;
    public Integer TranslatedAddresses = 0;
    public Integer  PageFaultCounter = 0;
    public Integer  TLBCounter = 0;

    public MemoryManagement() throws IOException {

        VirtualMemory = new ArrayList<String>();
        TLB = new ArrayList<String>();
        PageTable = new ArrayList<>();

        // Needed to output the translations into the file
        translationOutput = new ArrayList<String>();

        // Instantiating the disk
        diskData = new ArrayList<String>();
        LoadDisk();

        /*
        TLB.add("00100010,01011011");
        PageTable.add("01101000");
        PageTable.add("00101000");
        PageTable.add("01111011");
        */
    }


    public void StartSimulation() throws IOException {

        // ------------------------------SIMULATION------------------------------

        // Open the file
        FileInputStream file_stream = new FileInputStream("addresses.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(file_stream));

        String strLine;
        StringBuilder _translation = new StringBuilder();

        //Read File Line By Line
        while ((strLine = br.readLine()) != null)   {
            InputCounter++;
            // Print the content on the console
            String _pageNumber = GetPageNumber( Integer.parseInt(strLine));
            System.out.println("Input Page Number: " + _pageNumber);
            String _frameNumber = GetFrameNumber(_pageNumber);
            if (_frameNumber == null) {
                // TODO: Handle the page fault
                PageFaultCounter++;
                FromDiskToPageTable(_pageNumber);
                if (TLB.size() < 16) {
                    FromDiskToTLB(_pageNumber);
                }else {
                    TLB.remove(TLB.size() - 1);
                    FromDiskToTLB(_pageNumber);
                }
            }else {
                _translation = new StringBuilder();
                _translation.append("Virtual address: " + _pageNumber);
                _translation.append("          ");
                _translation.append("Physical address: " + _frameNumber);
                _translation.append("          ");
                _translation.append("Value: " + 107 + "\n");

                WriteToFile(_translation.toString());
            }
        }


        _translation = new StringBuilder();
        _translation.append("\n\n\nTranslated addresses: " + TranslatedAddresses );
        _translation.append("\nPage fault rate: " + ( Double.parseDouble(String.valueOf(PageFaultCounter)) * 100 ) / InputCounter );
        _translation.append("\nTLB hit rate: " + ( Double.parseDouble(String.valueOf(TLBCounter)) * 100 ) / InputCounter );
        translationOutput.add(_translation.toString());
        WriteToFile(_translation.toString());

        System.out.println(translationOutput);
        System.out.println("\n\n\nTranslated addresses: " + TranslatedAddresses );
        System.out.println("Page fault rate: " + ( Double.parseDouble(String.valueOf(PageFaultCounter)) * 100 ) / InputCounter );
        System.out.println("TLB hit rate: " + ( Double.parseDouble(String.valueOf(TLBCounter)) * 100 ) / InputCounter );

        //Close the input stream
        file_stream.close();

        // ------------------------------SIMULATION------------------------------

    }

    public void WriteToFile(String _text) throws FileNotFoundException, UnsupportedEncodingException {
        try {
            Files.write(Paths.get("logs.txt"), _text.toString().getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
        }
    }


    public String intTo16Binary(Integer _number) {
        return Integer.toBinaryString(0x10000 | _number).substring(1);
    }


    public Integer binaryTo16Int(String _binary) {
        return Integer.parseInt(_binary, 2);
    }

    public String GetPageNumber(int _address) {

        String page_number = intTo16Binary(_address).substring(0, 8);

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

    public Boolean PageIsInPageTable(String _pageBinary) {

        int _pageNumber = binaryTo16Int(_pageBinary);
        if (PageTable.size() > _pageNumber) {
            return true;
        }

        return false;
    }

    public String GetFrameNumber(String _pageNumber) {

        if (PageIsInTLB(_pageNumber)) {
            TranslatedAddresses++;
            TLBCounter++;
            System.out.println(_pageNumber + " is in the TLB");
            return GetFrameNumberFromTLB(_pageNumber);

        }else if (PageIsInPageTable(_pageNumber)) {
            TranslatedAddresses++;
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
                    System.out.println("Frame Number = " + found_tlb_items[1]);
                    return found_tlb_items[0];
                }
            }
        return "Page " + _pageBinary + " isn't in the TLB";
    }

    public String GetFrameNumberFromPageTable(String _pageBinary) {

            Integer _pageNumber = binaryTo16Int(_pageBinary);
            for (int i = 0; i < PageTable.size(); i++) {
                if (i == _pageNumber) {
                    System.out.println("Frame Number = " + PageTable.get(i).split(",")[0]);
                    return PageTable.get(i).split(",")[0];
                }
            }

        return "Page " + _pageNumber + " isn't in the TLB";
    }


    public void LoadDisk() throws IOException {

        String filePath = "disk_sim";
        //Instantiating the File class
        File file = new File(filePath);
        //instantiating the RandomAccessFile
        RandomAccessFile raFile = new RandomAccessFile(file, "rw");
        //Reading each line using the readLine() method
        while(raFile.getFilePointer() < raFile.length()) {
            int _val = raFile.readInt();
            diskData.add(intTo16Binary(_val));
        }
    }

    public void FromDiskToPageTable(String _pageNumber) {
        for (String item : diskData) {
            String item_page_number = GetPageNumber(binaryTo16Int(item));
            if (item_page_number.equals(_pageNumber)) {
                PageTable.add(item_page_number);
            }
        }
    }

    public  void FromDiskToTLB(String _pageNumber) {
        for (String item : diskData) {
            String item_page_number = GetPageNumber(binaryTo16Int(item));
            if (item_page_number.equals(_pageNumber)) {
                TLB.add(_pageNumber + "," + item.substring(0, 8));
            }
        }
    }

}
