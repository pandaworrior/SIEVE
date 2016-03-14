package replicationlayer.core.util;

// $Id: Debug.java 1550 2011-10-19 12:10:42Z dcfp $
 
public class Debug {

    public static boolean debug = false;
    public static boolean profile = false;


    public static void log(boolean module, boolean level, String format, Object... args) {
        if (module && level) {
            System.err.printf(format, args);
        }
    }

    public static void println(Object obj) {
        if (debug) {
            System.err.println(obj);
        }
    }

    public static void println(String str) {
        if (debug) {
            System.err.println(str);
        }
    }
    
    public static void printf(String format, Object... args){
    	if (debug){
    		System.err.printf(format, args);
    	}
    }

    public static void println(boolean cond, Object st) {
        if (debug && cond) {
            System.err.println(st);
        }
    }

    public static void println() {
        if (debug) {
            //System.err.println();
        }
    }

    public static void print(Object obj) {
        if (debug) {
            System.err.print(obj);
        }
    }

    static public void kill(Exception e) {
        e.printStackTrace();
        System.exit(0);
    }

    public static void kill(String st) {
        kill(new RuntimeException(st));
    }
    static protected long baseline = 0;//System.currentTimeMillis() - 1000000;

    public static void profileStart(String s) {
        if (!profile) {
            return;
        }
        String tmp = Thread.currentThread() + " " + s + " START " + (System.currentTimeMillis() - baseline);
        System.err.println(tmp);
    }

    public static void profileFinis(String s) {
        if (!profile) {
            return;
        }
        String tmp = Thread.currentThread() + " " + s + " FINIS " + (System.currentTimeMillis() - baseline);
        System.err.println(tmp);
    }

}
