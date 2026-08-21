package io.francitoshi.ditdah;

import static io.francitoshi.ditdah.Tutor.LESSONS;
import io.nut.base.util.Utils;
import java.io.IOException;
import java.io.PrintStream;
import static java.lang.System.out;
import java.util.ArrayList;
import javax.sound.sampled.LineUnavailableException;
import org.junit.jupiter.api.Test;

/**
 *
 * @author franci
 */
public class TutorTest
{
    
    public TutorTest()
    {
    }
    
    /**
     * Test of main method, of class Tutor.
     */
    @Test
    public void testMain() throws Exception
    {
        Pipes.BytePipe pipes = Pipes.bytePipe();
        PrintStream ps = new PrintStream(pipes.output);
            
        Tutor tutor = new Tutor(pipes.input, out, true);
        Thread th = Utils.execute(()-> {execute(tutor);}, "tutor", true);
        
        char[] chars = Tutor.LESSONS.replaceAll(" ", "").toCharArray();
        ArrayList<String> s = new ArrayList<>();
        
        for(int lesson=0;lesson<chars.length;lesson++)
        {
            String c = Character.toString(chars[lesson]);
            s.add(c);

            println(ps, c, 5);

            println(ps, s.toArray(new String[0]));
        }
        Utils.sleep(60000);

        th.join();
    }

    private void println(PrintStream ps, String... s)
    {
        for(String item : s)
        {
            ps.println(s);
        }
    }

    private void println(PrintStream ps, String s, int times)
    {
        for(int i=0;i<times;i++)
        {
            ps.println(s);
        }
    }

    public void execute(Tutor tutor)
    {
        try
        {
            tutor.main();
        }
        catch (LineUnavailableException ex)
        {
            System.getLogger(TutorTest.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        catch (IOException ex)
        {
            System.getLogger(TutorTest.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }


}
