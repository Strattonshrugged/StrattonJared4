/**
 * Created by jared stratton on 5/18/17.
 * As per example run meters are rounded to two decimals and kilometers to three decimals
 * Caution: System is not robust, XML parser desired but outside scope of assignment
 */

import java.io.*;
import java.util.*;

public class StrattonJared4 {
    public static final int MAX_TRACKPOINTS = 10000;
    public static final double EARTH_RADIUS = 6372.795;


    public static void main(String args[]) throws FileNotFoundException {
        boolean gpxRunning = true;
        String fileLoaded = "";

        // initialize arrays
        double[] lons = null;
        double[] lats = null;
        double[] elevs = null;
        String[] times = null;

        while (gpxRunning == true)  {
            // method functional variables
            Scanner mainConsole = new Scanner(System.in);
            String selection;
            String dataIn;

            mainMenu(fileLoaded);
            selection = mainConsole.next().substring(0,1);

            if (selection.equals("1"))  {
                // validates gpx file, pulls contents into String, returns String
                dataIn = fileReader();
                // modify nothing if fileReader indicates file read failure with ""
                // if dataIn isn't "" it means a file was successfully read in
                if (!dataIn.equals(""))  {
                    // pull the name off the front of the Superstring
                    String[] stringParts;
                    stringParts = dataIn.split("SPLITMARK");
                    fileLoaded = stringParts[0];
                    dataIn = stringParts[1];
                    // zero the arrays
                    lons = new double[MAX_TRACKPOINTS];
                    lats = new double[MAX_TRACKPOINTS];
                    elevs = new double[MAX_TRACKPOINTS];
                    times = new String[MAX_TRACKPOINTS];

                    // remove node tags and all other extraneous characters from String
                    dataIn = bareBones(dataIn);
                    // sort dataIn String into the data arrays to be analyzed
                    lons = doubleSort(dataIn,"lons");
                    lats = doubleSort(dataIn,"lats");
                    elevs = doubleSort(dataIn,"elevs");
                    times = stringSort(dataIn);
                    // testing
                    // System.out.println("lons" + Arrays.toString(lons));
                    // System.out.println("lats" + Arrays.toString(lats));
                    // System.out.println("elevs" + Arrays.toString(elevs));
                    // System.out.println("times" + Arrays.toString(times));
                }
            }   else if (selection.equals("2") && (!fileLoaded.equals(""))){
                distanceSelection(lons,lats);
            }   else if (selection.equals("3") && (!fileLoaded.equals(""))){
                gainLossSelection(elevs);
            }   else if (selection.equals("4") && (!fileLoaded.equals(""))){
                lowHighSelection(elevs);
            }   else if (selection.equals("5") && (!fileLoaded.equals(""))){
                speedTableSelection(lons,lats,times);
            }   else if (selection.equals("0")) {
                gpxRunning = false;
            }   else    {
                System.out.printf("\tInvalid Selection\n");
            }
        }  // end of while loop
    } // end of main


    // print function, pops up as long as main-method while loop is running
    public static void mainMenu(String fileLoaded)   {
        System.out.printf("\n\tGPX Trackpoint Processor\n");
        if (!fileLoaded.equals("")) {
            System.out.printf("\tCurrent file loaded: " + fileLoaded +"\n");
        }
        System.out.printf("\t1) Load Data from GPX File\n");
        if (!fileLoaded.equals("")) {
            System.out.printf("\t2) Display Distance Traveled\n");
            System.out.printf("\t3) Display Elevation Gained & Lost\n");
            System.out.printf("\t4) Display Elevation Minimum & Maximum\n");
            System.out.printf("\t5) Display Speed Table\n");
        }
        System.out.printf("\t0) Exit Trackpoint Processor\n");
        System.out.printf("\tEnter your selection: ");
    }


    // take filename, validate filename, put file contents into string, return string
    public static String fileReader() {
        Scanner fileNameConsole = new Scanner(System.in);
        System.out.printf("\tPlease enter file name: ");
        String fileName = fileNameConsole.next();
        // if the file name doesn't end in .gpx it should fail, but maybe accidentally left off
        try {
            String subName = fileName.substring(fileName.length() - 4, fileName.length());
            // if it doesn't end with '.gpx', assume it's just a fileName w/o '.gpx'
            if (!subName.equals(".gpx")) {
                fileName = fileName + ".gpx";
            }
        }
        catch (StringIndexOutOfBoundsException notMissingSuffix)    {
            // If exception is thrown, proceed as if normal, test for name validity
        }
        // testing, hardcoded file path
        // fileName = "/home/jared/Projects/cs210/StrattonJared4/out/production/StrattonJared4/Track001.gpx";

        try {
            File file = new File(fileName);
            Scanner fileScanner = new Scanner(file);
            String fileContents = fileScanner.useDelimiter("\\A").next();
            if (fileContents.indexOf("<?xml version=\"1.0\"?><gpx version=\"1.1\"") > -1)   {
                fileContents = fileName + "SPLITMARK" + fileContents;
                return fileContents;
            }   else    {
                System.out.printf("\tError: File is not gpx format\n");
                return "";
            }
        }
        catch (FileNotFoundException e)   {
            System.out.printf("\tError: File Not Found\n");
            return "";
        }
    }   // end of fileReader method


    // takes String, removes everything but the values I need
    public static String bareBones(String data)  {
        int firstTrackpointIndex = data.indexOf("trkpt");
        data = data.substring(firstTrackpointIndex,data.length());
        data = data.replace("lon"," ");
        data = data.replace("lat"," ");
        data = data.replace("ele"," ");
        data = data.replace("time"," ");
        data = data.replace("trkpt"," ");
        data = data.replace("trkseg"," ");
        data = data.replace("trk"," ");
        data = data.replace("gpx"," ");
        data = data.replace("/"," ");
        data = data.replace("\""," ");
        data = data.replace("<"," ");
        data = data.replace(">"," ");
        data = data.replace("="," ");
        return data;
    }   // end of bareBones

    public static double[] doubleSort(String data, String indicator)  {
        Scanner dataPlucker = new Scanner(data);
        double[] temp = new double[MAX_TRACKPOINTS];
        int key;

        if (indicator.equals("lons"))   {
            key = 0;
        }   else if (indicator.equals("lats"))  {
            key = 1;
        }   else if (indicator.equals("elevs"))    {
            key = 2;
        }   else    {
            System.out.printf("\tError occurred sorting data");
            return temp;
        }
        int i = 0;
        int position = 0;
        String inHand;
        while (dataPlucker.hasNext())  {
            inHand = dataPlucker.next();
            if (position == key)    {
                temp[i] = Double.parseDouble(inHand);
            }
            position = position + 1;
            if (position == 4)  {
                position = 0;
                i = i + 1;
            }
        }
        return temp;
    }   // end of doubleSort


    public static String[] stringSort(String data)  {
        Scanner dataPlucker = new Scanner(data);
        String[] temp = new String[MAX_TRACKPOINTS];
        int key = 3;
        int i = 0;
        int position = 0;
        String inHand;
        while (dataPlucker.hasNext())  {
            inHand = dataPlucker.next();
            if (position == key)    {
                temp[i] = inHand;
            }
            position = position + 1;
            if (position == 4)  {
                position = 0;
                i = i + 1;
            }
        }
        return temp;
    }   // end of stringSort


    public static void distanceSelection(double[] lons, double[] lats)  {
        double totalDistance = 0.0;
        int i = 0;
        while ((lons[i + 1] != 0.0 && lats[i + 1] != 0.0) && (i < (lons.length - 1)))   {
            double intervalDistance = 0;
            intervalDistance = coordinateDistance(lons[i],lats[i],lons[i+1],lats[i+1]);
            // testing
            // System.out.printf("point at i: \t\t\tlongitude\t" + lons[i] + "\tlatitude\t" + lats[i] + "\n");
            // System.out.printf("point at i + 1: \t\tlongitude\t" + lons[i] + "\tlatitude\t" + lats[i] + "\n");
            // System.out.printf("Interval distance:\t\t\t\t" + intervalDistance + "\n");
            totalDistance = totalDistance + intervalDistance;
            i = i + 1;
        }
        System.out.printf("\tTotal distance travelled is " + kmRound(totalDistance) + " kilometers.\n");
    }


    public static void gainLossSelection(double[] elevs)  {
        double gained = 0.0;
        double lost = 0.0;
        for (int i = 0; (i < (elevs.length - 1)) && (elevs[i + 1] != 0.0); i ++)    {
            // uphill
            if (elevs[i + 1] > elevs[i])    {
                gained = gained + (elevs[i + 1] - elevs[i]);
            // downhill
            }   else if (elevs[i + 1] < elevs[i])   {
                lost = lost + (elevs[i] - elevs[i + 1]);
            }
        }
        System.out.printf("\tTotal elevation gained is " + mRound(gained)+ " meters.\n");
        System.out.printf("\tTotal elevation lost is " + mRound(lost) + " meters.\n");
    }   // end of gainLossSelection


    public static void lowHighSelection(double[] elevs)   {
        double max = elevs[0];
        double min = elevs[0];
        int index = 1;
        while ((index < 10000) && (elevs[index] != 0.0))     {
            if (elevs[index] > max) {
                max = elevs[index];
            }
            if (elevs[index] < min) {
                min = elevs[index];
            }
            index = index + 1;
        }
        System.out.printf("\tMaximum elevation reached is " + mRound(max) + " meters.\n");
        System.out.printf("\tMinimum elevation reached is " + mRound(min) + " meters.\n");
    }   // end of lowHighSelection


    public static void speedTableSelection(double[] lons,double[] lats,String[] times)    {
        System.out.printf("\t\tSPEED TABLE\n");
        System.out.printf("\t\t(km / h)\tTime\n");
        System.out.printf("\t~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
        int i = 0;

        while (i < times.length - 3) {
            double i0lon = lons[i];
            double i0lat = lats[i];
            String i0time = times[i];
            double i1lon = lons[i + 1];
            double i1lat = lats[i + 1];
            String i1time = times[i + 1];
            double i2lon = lons[i + 2];
            double i2lat = lats[i + 2];
            String i2time = times[i + 2];
            double i3lon = lons[i + 3];
            double i3lat = lats[i + 3];
            String i3time = times[i + 3];
            double distance0to1 = coordinateDistance(i0lon,i0lat,i1lon,i1lat);
            double distance1to2 = coordinateDistance(i1lon,i1lat,i2lon,i2lat);
            double distance2to3 = coordinateDistance(i2lon,i2lat,i3lon,i3lat);

            // trim Strings to just the HH:MM:SS format
            i0time = i0time.substring(11, i0time.length() - 1);
            i1time = i1time.substring(11, i1time.length() - 1);
            i2time = i2time.substring(11, i2time.length() - 1);
            i3time = i3time.substring(11, i3time.length() - 1);

            int i0seconds = secondsCalculated(i0time);
            int i1seconds = secondsCalculated(i1time);
            int i2seconds = secondsCalculated(i2time);
            int i3seconds = secondsCalculated(i3time);

            // implemented Mat.max after two trackpoints had zero seconds between them
            // less accurate but I can't divide by zero
            int time0to1 = Math.max(1, i1seconds - i0seconds);
            int time1to2 = Math.max(1, i2seconds - i1seconds);
            int time2to3 = Math.max(1, i3seconds - i2seconds);

            double speed0to1 = distance0to1 * (3600 / time0to1);
            double speed1to2 = distance1to2 * (3600 / time1to2);
            double speed2to3 = distance2to3 * (3600 / time2to3);

            double averageSpeed = (speed0to1 + speed1to2 + speed2to3) / 3;

            System.out.printf("\t\t" + mRound(averageSpeed) + "\t\t" + Cher(i3time) + "\n");

            // iterate
            i = i + 4;
            // preempt null pointer exception error (early exit checker)
            try {
                String nullTest = times[i + 3];
                if (nullTest.equals(null)) {
                    return;
                }
            } catch (NullPointerException badNullTest) {
                return;
            }
        }   // end of while loop
    }   // end of SpeedTableSelection


    public static double mRound(double In)  {
        In = In * 100;
        In = Math.round(In);
        In = In / 100;
        return In;
    }


    public static double kmRound(double In)  {
        In = In * 1000;
        In = Math.round(In);
        In = In / 1000;
        return In;
    }

    public static double coordinateDistance(double Lon1,double Lat1,double Lon2,double Lat2) {
        double LatDiff = Math.toRadians(Lat1 - Lat2);
        double LongDiff = Math.toRadians(Lon1 - Lon2);
        double theA =
                Math.sin(LatDiff / 2) *
                Math.sin(LatDiff / 2) +
                Math.cos(Math.toRadians(Lat1)) *
                Math.cos(Math.toRadians(Lat2)) *
                Math.sin(LongDiff / 2) *
                Math.sin(LongDiff / 2);
        double theC = 2 * Math.atan2(Math.sqrt(theA), Math.sqrt(1 - theA));
        // theC must be equal to DeltaSigma in the book's description
        double Answer = EARTH_RADIUS * theC;

        return Answer;
    }   // end of coordinateDistance


    public static int secondsCalculated(String timeIn)  {
        int totalSeconds = 0;

        String[] timeChunks = new String[3];
        timeChunks = timeIn.split(":");
        totalSeconds = (Integer.parseInt(timeChunks[0]) * 60 * 60)
                        + (Integer.parseInt(timeChunks[1]) * 60)
                            + (Integer.parseInt(timeChunks[2]));
        return totalSeconds;
    }


    public static String Cher(String In)    {
        if (In.length() == 8)   {
            // HH:MM:SS
            int firstTwo = Integer.parseInt(In.substring(0,2));
            firstTwo = firstTwo - 7;
            In = Integer.toString(firstTwo) + In.substring(2,8);
            return In;
        }   else    {
            // H:MM:SS
            int justOne = Integer.parseInt(In.substring(0,1));
            justOne = justOne - 7;
            if (justOne < 0)    {
                justOne = 24 - Math.abs(justOne);
            }
            In = Integer.toString(justOne) + In.substring(1,7);
            return In;
        }
    }   // end of Cher

} // end of class
