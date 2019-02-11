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

        //Test 1: Decode the article
        String decode_input_filename ="/Users/ami_zou/Desktop/COMP590/HW/comp590sp19-a1/data/compressed.dat";
        String decode_output_filename = "/Users/ami_zou/Desktop/COMP590/HW/comp590sp19-a1/data/uncompressed.txt";

        decoder.decode(decode_input_filename, decode_output_filename);
        
        //Test 2: Encode the article
        String encode_input_filename = decode_output_filename;
        String encode_output_filename = "/Users/ami_zou/Desktop/COMP590/HW/comp590sp19-a1/data/recompressed.txt"; //dat
        
        //NOT WORKING. Check out new HuffEncode and HuffEncoder for correct implementation!
        //encoder.encode(encode_input_filename, encode_output_filename);
        
        //Test 3: Decode the encoded article
        String redecode_input_filename = encode_output_filename;
        String redecode_output_filename = "/Users/ami_zou/Desktop/COMP590/HW/comp590sp19-a1/data/reuncompressed.txt";
        
        //NOT WORKING. Check out new HuffEncode and HuffEncoder for correct implementation!
        //Decoder d = new Decoder();
        //d.decode(redecode_input_filename, redecode_output_filename);
    }
}
