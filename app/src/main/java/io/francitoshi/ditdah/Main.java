/*
 *  Main.java
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

import io.nut.base.audio.Audio;
import static io.nut.base.audio.Audio.ADJUST_START;
import static io.nut.base.audio.Audio.DCOFFSET;
import static io.nut.base.audio.Audio.HANNWINDOW;
import static io.nut.base.audio.Audio.OVERLAP;
import io.nut.base.audio.AudioSynthesizer;
import io.nut.base.audio.AudioMorse;
import io.nut.base.audio.Wave;
import io.nut.base.jar.ManifestReader;
import io.nut.base.signal.Morse;
import io.nut.base.options.BooleanOption;
import io.nut.base.options.CommandOption;
import io.nut.base.options.MissingOptionParameterException;
import io.nut.base.options.NumberOption;
import io.nut.base.options.OptionParser;
import io.nut.base.options.StringOption;
import io.nut.base.platform.Snap;
import io.nut.base.resources.ResourceBundles;
import io.nut.base.time.JavaTime;
import io.nut.base.util.Utils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineUnavailableException;

public class Main 
{    
    private static final String DITDAH = "ditdah";
    private static final String DATE;
    private static final String VER;
    private static final String LICENSE;
    private static final String HELP;
    private static final String VERSION;
    
    static 
    {
        ResourceBundle bundle = ResourceBundles.getBundle(Main.class, Locale.getDefault());
        ManifestReader reader = new ManifestReader(Main.class);

        VER = Utils.firstNonNull(Main.class.getPackage().getImplementationVersion(),"[dev]");
        DATE = Utils.firstNonNull(reader.getMainAttribute(ManifestReader.BUILD_DATE), LocalDate.now().format(JavaTime.YYYY_MM_DD));
        HELP = getResourceText(bundle, "help");
        LICENSE = getResourceText(bundle, "license");
        VERSION = getResourceText(bundle, "version");
    }
    
    public static String getResourceText(ResourceBundle bundle, String key)
    {
        String fileName = bundle.getString(key);
        return ResourceBundles.getResourceAsString(Main.class, fileName).replace("{$VERSION}", VER).replace("{$DATE}", DATE);
    }
    
    static final int WPM = 12;
    static final int VOLUME = 70;
    static final int FREQ = 800;

    public static void main(String... args) 
    {
        OptionParser options = new OptionParser();
        
        CommandOption encodeCmd = options.add(new CommandOption('e',"encode"));
        CommandOption decodeCmd = options.add(new CommandOption('d',"decode"));
        CommandOption playCmd = options.add(new CommandOption('p',"play"));
        CommandOption listenCmd = options.add(new CommandOption('t',"listen"));
        CommandOption tutorCmd = options.add(new CommandOption('T',"tutor"));

        StringOption inputOp = options.add(new StringOption('i', "input"));
        StringOption outputOp = options.add(new StringOption('o', "output"));
        
        NumberOption wpmOp = options.add(new NumberOption('w', "wpm"));
        NumberOption ewpmOp = options.add(new NumberOption('e', "ewpm"));
        
        NumberOption freqOp = options.add(new NumberOption('f', "freq"));
        NumberOption volumeOp = options.add(new NumberOption('v', "volume"));
        
        StringOption waveOp = options.add(new StringOption('W', "wave"));
        NumberOption sampleRateOp = options.add(new NumberOption('r', "sample-rate"));
        
        BooleanOption helpOp = options.add(new BooleanOption('h', "help"));
        BooleanOption licenseOp = options.add(new BooleanOption('L', "license"));
        BooleanOption versionOp = options.add(new BooleanOption('v',"version"));

        BooleanOption debugOp = options.add(new BooleanOption('D', "debug"));
        BooleanOption noSnapOp = options.add(new BooleanOption('S', "no-snap"));

        try
        {
            args = options.parse(args);

            if (helpOp.isUsed())
            {
                System.out.println(HELP);
                return;
            }
            if (versionOp.isUsed())
            {
                System.out.println(VERSION);
                return;
            }
            if (licenseOp.isUsed())
            {
                System.out.println(LICENSE);
                return;
            }
            
            boolean cmdUsed = CommandOption.isUsed(encodeCmd, decodeCmd, playCmd, listenCmd, tutorCmd);

            if(!cmdUsed)
            {
                System.out.println(HELP);
                return;
            }

            File altDir = null;
            if(Snap.isSnap() && !noSnapOp.isUsed())
            {
                altDir = Snap.fixTmpDir();
            }
        
            if(cmdUsed)
            {
                int wpm = wpmOp.intValue(WPM);
                int ewpm = ewpmOp.intValue(WPM);
                int volume = volumeOp.intValue(VOLUME);
                int hz = freqOp.intValue(FREQ);
                Wave wave = Wave.SINE;
                Morse morse = new Morse(wpm, ewpm, 0, 0);
                                
                if(encodeCmd.isUsed())
                {
                    BufferedReader reader = new BufferedReader(getInputText(inputOp, args));
                    PrintStream out = getOutputText(outputOp, args);

                    for(String line=reader.readLine();line!=null;line=reader.readLine())
                    {
                        String s = morse.join(morse.encode(line.trim()));
                        out.println(s);
                    }
                    return;
                }
                else if(decodeCmd.isUsed())
                {
                    BufferedReader reader = new BufferedReader(getInputText(inputOp, args));
                    PrintStream out = getOutputText(outputOp, args);

                    for(String line=reader.readLine();line!=null;line=reader.readLine())
                    {
                        String s = morse.decode(line);
                        out.println(s);
                    }
                    return;
                }
                else if(playCmd.isUsed())
                {
                    BufferedReader reader = new BufferedReader(getInputText(inputOp, args));
                    AudioSynthesizer audioSynthesizer = getAudioSynthesizer(outputOp, wave);                    
                    
                    for(String line=reader.readLine();line!=null;line=reader.readLine())
                    {
                        int[] pattern = morse.encodePattern(line.trim());
                        audioSynthesizer.play(hz, pattern, volume/100.0);
                        morse.updateStartGap(2);
                    }
                    audioSynthesizer.drain();
                }
                else if(listenCmd.isUsed())
                {
                    AudioInputStream ais = Audio.getAudioInputStream(Audio.getLineIn(Audio.PCM_CD_MONO, 441000));
                    ais = Audio.getMarkable(ais);
                    AudioMorse instance = new AudioMorse(ais, hz, HANNWINDOW|OVERLAP|DCOFFSET|ADJUST_START, 5, 0);
                    
                    for(String s : instance)
                    {
                        System.out.print(s);
                        System.out.flush();
                    }
                }
                else if(tutorCmd.isUsed())
                {
                    new Tutor().main();
                }
            }
            else
            {
                System.out.println(HELP);
                return;
            }
        }
        catch (MissingOptionParameterException | IOException  ex)
        {
            System.getLogger(Main.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        catch (Exception ex)
        {
            System.getLogger(Main.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    public static BufferedReader getInputText(StringOption inputOp, String[] args) throws IOException, MissingOptionParameterException
    {
        if(inputOp.isUsed())
        {
            File file = new File(inputOp.getValue());
            if(!file.exists())
            {
                System.out.printf("File '%s' does not exists", file);
                System.exit(1);
            }
            
            return new BufferedReader(new FileReader(file));
        }
        else if(args.length>0)
        {
            return new BufferedReader(new StringReader(String.join(" ", args)));
        }
        else
        {
            return new BufferedReader(new InputStreamReader(System.in));
        }
    }
    
    public static PrintStream getOutputText(StringOption outputOp, String[] args) throws IOException, MissingOptionParameterException
    {
        if(outputOp.isUsed())
        {
            File file = new File(outputOp.getValue());
            if(file.exists())
            {
                if(!askOverwrite(file))
                {
                    System.exit(1);
                }
            }
            return new PrintStream(file);
        }
        else
        {
            return System.out;
        }
    }
    
    public static AudioSynthesizer getAudioSynthesizer(StringOption outputOp, Wave wave) throws LineUnavailableException, IOException, MissingOptionParameterException
    {
        if(outputOp.isUsed())
        {
            File file = new File(outputOp.getValue());
            if(!askOverwrite(file))
            {
                System.exit(1);
            }
            return new AudioSynthesizer(file, Audio.PCM_CD_MONO, 16, wave);
        }
        return new AudioSynthesizer(Audio.getLineOut(Audio.PCM_CD_MONO), 16, wave);
    }
    
    static final String OVERWRITE = "File '%s' already exists. Overwrite? [y/N]\n";
    
    public static boolean askOverwrite(File file) throws MissingOptionParameterException
    {
        Scanner sc = new Scanner(System.in);
        System.out.printf(OVERWRITE, file);
        System.out.flush();
        
        String line = sc.nextLine();
        return line.equalsIgnoreCase("Y");
    }
}
