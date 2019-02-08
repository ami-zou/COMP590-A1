package hc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import io.InputStreamBitSource;
import io.InsufficientBitsLeftException;

public class Test {

    public static void main(String[] args) throws InsufficientBitsLeftException, IOException {
        Decoder decoder = new Decoder();

        String input_file_name ="/Users/ami_zou/Desktop/COMP590/HW/comp590sp19-a1/data/compressed.dat";
        InputStream stream = new FileInputStream(input_file_name);
        
        String output_file_name = "/Users/ami_zou/Desktop/COMP590/HW/comp590sp19-a1/data/uncompressed.txt";

        decoder.decode(input_file_name, output_file_name);
    }
}
