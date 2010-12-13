/**
 *
 *	LICENSE: see COPYING file
 *
**/

/*
 * Version.java
 * Copyright (C) 2005 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.core;

import it.eng.spagobi.engines.weka.configurators.FilterConfigurator;

import java.io.*;

import org.apache.log4j.Logger;

/**
 * This class contains the version number of the current WEKA release and some
 * methods for comparing another version string. The normal layout of a
 * version string is "MAJOR.MINOR.REVISION", but it can also handle partial
 * version strings, e.g. "3.4".<br>
 * Should be used e.g. in exports to XML for keeping track, with which version 
 * of WEKA the file was produced.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 1.4 $ 
 */
public class Version implements Comparable {
  
  /** the major version */
  public static int MAJOR = 3; 
  
  /** the minor version */
  public static int MINOR = 4; 
  
  /** the revision */
  public static int REVISION = 3;

  private static transient Logger logger = Logger.getLogger(Version.class);
  
  static {
    try {
      InputStream inR = Version.class.getClassLoader().getResourceAsStream("weka/core/version.txt");
      LineNumberReader lnr = new LineNumberReader(new InputStreamReader(inR));
      String line = lnr.readLine();
      int [] maj = new int[1];
      int [] min = new int[1];
      int [] rev = new int[1];
      parseVersion(line, maj, min, rev);
      MAJOR = maj[0];
      MINOR = min[0];
      REVISION = rev[0];
      lnr.close();
    } catch (Exception ex) {
    	logger.error("weka.core.Version: Unable to load version information!", ex);
    }
  }

  /** the complete version */
  public static String VERSION = MAJOR + "." + MINOR + "." + REVISION;


  private static void parseVersion(String version, int [] maj, int [] min,
                                   int [] rev) {
    int major = 0;
    int minor = 0;
    int revision = 0;

    try {
      String tmpStr = version;
      tmpStr = tmpStr.replace('-', '.');
      if (tmpStr.indexOf(".") > -1) {
        major  = Integer.parseInt(tmpStr.substring(0, tmpStr.indexOf(".")));
        tmpStr = tmpStr.substring(tmpStr.indexOf(".") + 1);
        if (tmpStr.indexOf(".") > -1) {
          minor  = Integer.parseInt(tmpStr.substring(0, tmpStr.indexOf(".")));
          tmpStr = tmpStr.substring(tmpStr.indexOf(".") + 1);
          if (!tmpStr.equals(""))
            revision = Integer.parseInt(tmpStr);
          else
            revision = 0;
        }
        else {
          if (!tmpStr.equals(""))
            minor = Integer.parseInt(tmpStr);
          else
            minor = 0;
        }
      }
      else {
        if (!tmpStr.equals(""))
          major = Integer.parseInt(tmpStr);
        else
          major = 0;
      }
    } catch (Exception e) {
      e.printStackTrace();
      major    = -1;
      minor    = -1;
      revision = -1;
    } finally {
      maj[0] = major;
      min[0] = minor;
      rev[0] = revision;
    }
  }

  /**
   * checks the version of this class against the given version-string.
   * 
   * @param o     the version-string to compare with
   * 
   * @return      -1 if this version is less, 0 if equal and +1 if greater
   * than the provided version
   */
  public int compareTo(Object o) {
    int       result;
    int       major;
    int       minor;
    int       revision;
    int       [] maj = new int [1];
    int       [] min = new int [1];
    int       [] rev = new int [1];
    String    tmpStr;
   
    
    // do we have a string?
    if (o instanceof String) {
      parseVersion((String)o, maj, min, rev);
      major = maj[0];
      minor = min[0];
      revision = rev[0];
    }
    else {
      major    = -1;
      minor    = -1;
      revision = -1;
    }

    if (MAJOR < major) {
      result = -1;
    }
    else if (MAJOR == major) {
      if (MINOR < minor) {
        result = -1;
      }
      else if (MINOR == minor) {
        if (REVISION < revision) {
          result = -1;
        }
        else if (REVISION == revision) {
          result = 0;
        }
        else {
          result = 1;
        }
      }
      else {
        result = 1;
      }
    }
    else {
      result = 1;
    }
    
    return result;
  }
  
  /**
   * whether the given version string is equal to this version.
   * 
   * @param o       the version-string to compare to
   * 
   * @return        TRUE if the version-string is equals to its own
   */
  public boolean equals(Object o) {
    return (compareTo(o) == 0);
  }
  
  /**
   * checks whether this version is older than the one from the given
   * version string.
   * 
   * @param o       the version-string to compare with
   * 
   * @return        TRUE if this version is older than the given one
   */
  public boolean isOlder(Object o) {
    return (compareTo(o) == -1);
  }
  
  /**
   * checks whether this version is newer than the one from the given
   * version string.
   * 
   * @param o       the version-string to compare with
   * 
   * @return        TRUE if this version is newer than the given one
   */
  public boolean isNewer(Object o) {
    return (compareTo(o) == 1);
  }
  
  /**
   * only for testing.
   * 
   * @param args the args
   */
  public static void main(String[] args) {
    Version       v;
    String        tmpStr;

    
    // test on different versions
    v = new Version();
    tmpStr = "5.0.1";
    tmpStr = VERSION;
    tmpStr = "3.4.0";
    tmpStr = "3.4";
    tmpStr = "5";
  }
}
