import sun.font.TrueTypeFont;

import java.io.*;
import java.util.List;

public class main {


    public static void main(String[] args) throws IOException {

        VirtualMemory vm = new VirtualMemory();
        String page_number = vm.GetPageNumber(30338);
        System.out.println("PAGE NUMBER: " + page_number);
        String frame_number = vm.GetFrameNumber(page_number);
        System.out.println("FRAME NUMBER: " + frame_number);

    }



}
