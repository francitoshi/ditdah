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

import io.nut.base.io.ThrottledInputStream;
import io.nut.base.options.BooleanOption;
import io.nut.base.options.CommandOption;
import io.nut.base.options.MissingOptionParameterException;
import io.nut.base.options.OptionParser;
import io.nut.base.options.StringOption;
import io.nut.base.platform.Snap;
import io.nut.base.resources.ResourceBundles;
import io.nut.base.util.Java;
import io.nut.base.util.Utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class Main 
{
    private static final String DITDAH = "ditdah";
    private static final String DATE = "2026-01-24";
    private static final String REPORT_BUGS
            = "Report bugs to <francitoshi@gmail.com>\n";


    private static final String VER;
    private static final String LICENSE;
    private static final String HELP;
    
    static 
    {
        ResourceBundle bundle = ResourceBundles.getBundle(Main.class, Locale.getDefault());
        VER = Utils.firstNonNull(Main.class.getPackage().getImplementationVersion(),"[dev]");
        HELP = getResourceText(bundle, "help");
        LICENSE = getResourceText(bundle, "license");
    }
    private static final String VERSION
            = DITDAH + "  version " + VER + " ("+DATE+")\n"
            + "Copyright (C) 2026 by francitoshi@gmail.com\n";
    
    public static String getResourceText(ResourceBundle bundle, String key)
    {
        String fileName = bundle.getString(key);
        return ResourceBundles.getResourceAsString(Main.class, fileName).replace("{$VERSION}", VER).replace("{$DATE}", DATE);
    }
    
    public static void main(String... args) 
    {
        OptionParser options = new OptionParser();
        
        CommandOption encodeCmd = options.add(new CommandOption('e',"encode"));
        CommandOption decodeCmd = options.add(new CommandOption('d',"decode"));
        CommandOption chatCmd = options.add(new CommandOption('c',"chat"));

        StringOption inputOp = options.add(new StringOption('I', "input"));
        StringOption outputOp = options.add(new StringOption('O', "output"));
        
        BooleanOption helpOp = options.add(new BooleanOption('h', "help"));
        BooleanOption versionOp = options.add(new BooleanOption('v',"version"));
        BooleanOption licenseOp = options.add(new BooleanOption('L', "license"));
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
            boolean cmdUsed = CommandOption.isUsed(encodeCmd, decodeCmd, chatCmd);

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
            
            final File ditdahDir = altDir!=null ? altDir : new File(Java.USER_HOME, ".ditdah");
            final File configFile = new File(ditdahDir, "config.properties");

            ditdahDir.mkdirs();
        
            InputStream input = null;

            if (inputOp.isUsed())
            {
                File file = new File(inputOp.getValue());
                if (!file.exists())
                {
                    System.err.printf("can't find %s'\n", file);
                    System.exit(1);
                }
                input = new ThrottledInputStream(new FileInputStream(file), 66, 200, 60_000, TimeUnit.MILLISECONDS, false).setSingleLine(true);
            }

            OutputStream output = System.out;

            if (outputOp.isUsed())
            {
                output = new FileOutputStream(outputOp.getValue());
            }

            boolean mock = input!=null || System.console()==null;
                
            if(cmdUsed)
            {
                if(encodeCmd.isUsed())
                {
                    
                    for(int i=1;i<args.length;i++)
                    {
//                        lettera.send(args[i]);
                    }
                }
                else if(decodeCmd.isUsed())
                {
//                    lettera.listAccounts();
                }
                else if(chatCmd.isUsed())
                {
//                    lettera.listFriends();
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
}
