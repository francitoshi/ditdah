/*
 *  MainTest.java
 *
 *  Copyright (c) 2026 francitoshi@gmail.com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  Report bugs or new features to: francitoshi@gmail.com
 */
package io.francitoshi.ditdah;

import io.nut.base.io.IO;
import java.io.File;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class MainTest 
{
    @Test
    public void testMain1()
    {
        Main.main("-L");
    }
    
    @Test
    public void testMain2()
    {
        Main.main("-v");
    }

    @Test
    public void testMain3() throws IOException
    {
        Main.main("encode", "Hello World");
    }
    
    @Test
    public void testEncodeDecode1() throws IOException
    {
        //ditdah encode -i message.txt -o morse.txt //Encode ASCII text file to Morse code text file        
        File messageTxt = createTmp("message", ".txt", HELLO_WORLD);
        File morseTxt = createTmp("morse", ".txt", null);

        Main.main("encode", "-i", messageTxt.getAbsolutePath(), "-o", morseTxt.getAbsolutePath());
        messageTxt.delete();

        //  ditdah decode -i morse.txt -o message.txt //Decode Morse code text file to ASCII text file
        Main.main("decode", "-i", morseTxt.getAbsolutePath(), "-o", messageTxt.getAbsolutePath());

        assertTrue(txtContains(messageTxt, HELLO_WORLD));
        
        messageTxt.delete();
        messageTxt.delete();
    }

    @Test
    public void testPlayListen1() throws IOException
    {
        //  ditdah play -i message.txt -o morse.wav // Generate audio file from ASCII text

        File messageTxt = createTmp("message",".txt", HELLO_WORLD);
        File morseWav = createTmp("morse",".wav", null);

        Main.main("play", "-i", messageTxt.getAbsolutePath(), "-o", morseWav.getAbsolutePath());
        messageTxt.delete();

        //  ditdah listen -i recording.wav -o message.txt // Decode audio file to ASCII text
        Main.main("listen", "-i", morseWav.getAbsolutePath(), "-o", messageTxt.getAbsolutePath());

        assertTrue(txtContains(messageTxt, HELLO_WORLD));
        
        messageTxt.delete();
        messageTxt.delete();
    }

    public static File createTmp(String prefix, String sufix, String text) throws IOException
    {
        File file = File.createTempFile(prefix, sufix);
        if(file.exists())
        {
            file.delete();
        }
        if(text!=null)
        {
            IO.writeToFile(file, text);
        }
        return file;
    }

    public static boolean txtContains(File file, String text) throws IOException
    {
        byte[] bytes = IO.readAllBytes(file);
        String s = new String(bytes).toUpperCase();
        return s.contains(text.toUpperCase());
    }

    public static final String HELLO_WORLD = "hello world";

}

//  ditdah encode < input.txt > output.txt
//      Encode from stdin to stdout
//
//
//
//  ditdah play -i message.txt
//      Play ASCII text as Morse code through speakers
//
//
//  ditdah listen -o message.txt
//      Listen to microphone and decode to text file
//
//  echo "<SOS>" | ditdah encode | ditdah decode
//      Pipeline example: encode and decode
