import java.io.*;

public class main {


    public static void main(String[] args) throws IOException {

        MemoryManagement vm = new MemoryManagement();
        vm.GetFrameNumber("11100101");
        /*
        String page_number = vm.GetPageNumber(57566);
        System.out.println("Page Number      : " +  page_number);
        System.out.println("Virtual Address  : " +  vm.intTo16Binary(57566));
        System.out.println("Physical Address : " +  vm.intTo16Binary(8926));
        */

        //vm.ReadDisk();

        /*
        String page_number = vm.GetPageNumber(30338);
        String frame_number = vm.GetFrameNumber(page_number);

        System.out.println("PAGE NUMBER BIN: " + page_number);
        System.out.println("PAGE NUMBER INT: " + vm.GetIntegerFromBinary(page_number));
        System.out.println("FRAME NUMBER: " + frame_number); */

    }



}
