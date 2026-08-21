package io.francitoshi.ditdah;

import io.nut.base.audio.Audio;
import io.nut.base.audio.AudioSynthesizer;
import io.nut.base.audio.Wave;
import io.nut.base.signal.Morse;
import static io.nut.base.signal.Morse.DEFAULT_WMP;
import static io.nut.base.signal.Morse.FLAG_BOLD;
import io.nut.base.util.Joins;
import io.nut.base.util.Shuffles;
import io.nut.base.util.Utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import javax.sound.sampled.LineUnavailableException;

/**
 *
 * @author franci
 */
public class Tutor
{
    //https://www.youtube.com/watch?v=OB1RUBwAvbE&list=PLE29501CA11B567E8
    //https://www.youtube.com/watch?v=kRvzE9NyW70

    static final String LESSONS = "ETI5 MAN0 SOR1 KDW2 VUG8 HBF9 PCQ3 YJL4 XZ7 6,/? @ <AR> <SK> <DE>";
    
    private final InputStream in;
    private final PrintStream out;
    private final boolean demo;
    private final boolean echo;
    private final double volume;
    private final int waitMillis;
    private final Morse morse;

    public Tutor(InputStream in, PrintStream out, boolean demo)
    {
        this.in = in;
        this.out = out;
        this.demo = demo;
        this.waitMillis = demo ? 1000 : 444;
        this.volume = demo ? 0.33 : 1;
        this.echo = demo;
        this.morse = new Morse(DEFAULT_WMP, DEFAULT_WMP, FLAG_BOLD, demo ? 1 : 5);
    }

    public Tutor()
    {
        this(System.in, System.out, false);
    }

    
    public void main() throws LineUnavailableException, IOException
    {
        out.println("------------------------------");
        out.println("----- ditdah Morse Tutor -----");
        out.println("------------------------------");
        try (AudioSynthesizer audioSynthesizer = new AudioSynthesizer(Audio.getLineOut(Audio.PCM_CD_MONO), 16, Wave.SINE))
        {
            char[] chars = LESSONS.replaceAll(" ", "").toCharArray();
            StringBuilder s = new StringBuilder();
            for(int lesson=0;lesson<chars.length;lesson++)
            {
                String c = Character.toString(chars[lesson]);
                s.append(c);

                learnOneLetter(audioSynthesizer, lesson, c);

                practice(audioSynthesizer, lesson, s.toString());
                
            }
        }
        
//        AudioInputStream ais = Audio.getAudioInputStream(Audio.getLineIn(Audio.PCM_CD_MONO, 441000));
//        ais = Audio.getMarkable(ais);
//        AudioMorse instance = new AudioMorse(ais, hz, HANNWINDOW|OVERLAP|DCOFFSET|ADJUST_START, 5, 0);

//        for(String s : instance)
//        {
//            out.print(s);
//            out.flush();
//        }
        
//        AudioInputStream ais = Audio.getAudioInputStream(Audio.getLineIn(Audio.PCM_CD_MONO, 441000));
//        ais = Audio.getMarkable(ais);
//        AudioMorse instance = new AudioMorse(ais, hz, HANNWINDOW|OVERLAP|DCOFFSET|ADJUST_START, 5, 0);

//        for(String s : instance)
//        {
//            out.print(s);
//            out.flush();
//        }
    }
    static final int READER_BUFFER_SIZE = 2;
    public void learnOneLetter(AudioSynthesizer audioSynthesizer, int lesson, String letter) throws IOException
    {
        String code = morse.join(morse.encode(letter));
        int[] pattern = morse.join(morse.encodePattern(letter));

        out.println("------------------------------");
        out.printf("     Lesson %d - Letter %s %s\n", lesson, letter, code);
        out.println("------------------------------");

        out.flush();
        for(int i=0;i<5;i++)
        {
            Utils.sleep(waitMillis);
            out.printf("%s %s\n", letter, code);
            audioSynthesizer.play(800, pattern, 1);
        }
        for(int i=0;i<5;i++)
        {
            Utils.sleep(waitMillis);
            out.printf("%s\n", letter);
            audioSynthesizer.play(800, pattern, 1);
        }
        out.printf("type the letter %s and [return]\n", letter);
        BufferedReader  sc = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8), READER_BUFFER_SIZE);
        for(int i=0;i<5;)
        {
            Utils.sleep(waitMillis);
            audioSynthesizer.play(800, pattern, 1);
            String line = sc.readLine().trim();
            if(echo) System.err.println(line);
            if(line.equalsIgnoreCase(letter))
            {
                i++;
            }
            else
            {
                System.err.printf("Wrong letter was %s\n", letter);
            }
        }
        
    }
    public boolean practice(AudioSynthesizer audioSynthesizer, int lesson, String letters) throws IOException
    {
        char[] chars = letters.toCharArray();
        
        out.println("------------------------------");
        out.printf("     Lesson %d - Letter %s \n", lesson, letters);
        out.println("------------------------------");
        for(char c : chars)
        {
            String letter = ""+c;
            String code =morse.join(morse.encode(letter));
            int[] pattern = morse.join(morse.encodePattern(letter));
            out.printf("%s %s\n", letter, code);
            audioSynthesizer.play(800, pattern, volume);
            Utils.sleep(400);
        }
        out.println("------------------------------");
        
        out.printf("type the letters and [return]\n");
        BufferedReader  sc = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8), READER_BUFFER_SIZE);
        char[] test = Joins.join(chars, chars, chars, chars, chars);

        while(true)
        {
            if(!demo) Shuffles.shuffle(test);
            Utils.sleep(waitMillis);
            String s = new String(test);
            int[] pattern = morse.join(morse.encodePattern(s));
            audioSynthesizer.play(800, pattern, volume);
            String line = sc.readLine().trim();
            if(line.equalsIgnoreCase(s))
            {
                return true;
            }
            else
            {
                System.err.printf("Wrong letters were %s\n", s);
            }
        }
    } 
}
