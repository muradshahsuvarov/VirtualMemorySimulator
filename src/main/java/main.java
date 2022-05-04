import sun.font.TrueTypeFont;

import java.io.*;
import java.util.List;

public class main {


    public static void main(String[] args) throws IOException {

        VirtualMemory vm = new VirtualMemory();
        String page_number = vm.GetPageNumber(30338);
        System.out.println(page_number);
        vm.GetFrameNumber(page_number);

    }



}
