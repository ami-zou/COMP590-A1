package io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Test {

    public static void main(String[] args) throws InsufficientBitsLeftException, IOException {
        Decoder decoder = new Decoder();

        String path ="/Users/ami_zou/Desktop/COMP590/HW/comp590sp19-a1/data/compressed.dat";
        InputStream stream = new FileInputStream(path);
        InputStreamBitSource input = new InputStreamBitSource(stream);

        System.out.print("Hello World");

        //decoder.readBits(stream);
        decoder.decode(stream);
    }
}
