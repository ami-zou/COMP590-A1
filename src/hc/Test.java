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
        Encoder encoder = new Encoder();

        String decode_input_filename ="/Users/ami_zou/Desktop/COMP590/HW/comp590sp19-a1/data/compressed.dat";
        String decode_output_filename = "/Users/ami_zou/Desktop/COMP590/HW/comp590sp19-a1/data/uncompressed.txt";

        decoder.decode(decode_input_filename, decode_output_filename);
        
        String encode_input_filename = "/Users/ami_zou/Desktop/COMP590/HW/comp590sp19-a1/data/uncompressed.txt";
        String encode_output_filename = "/Users/ami_zou/Desktop/COMP590/HW/comp590sp19-a1/data/recompressed.dat";
        
        encoder.encode(encode_input_filename, encode_output_filename);
    }
}
