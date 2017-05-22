/**
 * Created by jared stratton on 5/18/17.
 * As per example run meters are rounded to two decimals and kilometers to three decimals
 */

import java.io.*;
import java.util.*;

public class StrattonJared4 {
    public static final int MAX_TRACKPOINTS = 10000;

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
                    System.out.println("lons" + Arrays.toString(lons));
                    System.out.println("lats" + Arrays.toString(lats));
                    System.out.println("elevs" + Arrays.toString(elevs));
                    System.out.println("times" + Arrays.toString(times));
                }
            }   else if (selection.equals("2") && (!fileLoaded.equals(""))){
                distanceSelection();
            }   else if (selection.equals("3") && (!fileLoaded.equals(""))){
                gainLossSelection(elevs);
            }   else if (selection.equals("4") && (!fileLoaded.equals(""))){
                lowHighSelection(elevs);
            }   else if (selection.equals("5") && (!fileLoaded.equals(""))){
                speedTableSelection();
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
        // TODO FIX PATH -- DELETE THIS CODE, LET FILENAME CONTINUE FROM ABOVE
        fileName = "/home/jared/Projects/cs210/StrattonJared4/out/production/StrattonJared4/Track002.gpx";
        try {
            File file = new File(fileName);
            Scanner fileScanner = new Scanner(file);
            String fileContents = fileScanner.useDelimiter("\\A").next();
            if (fileContents.indexOf("<?xml version=\"1.0\"?><gpx version=\"1.1\"") > -1)   {
                fileContents = fileName + "SPLITMARK" + fileContents;
                //System.out.println("string with splitmark test" + fileContents);
                return fileContents;
            }   else    {
                System.out.println("Error: file is not gpx format");
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


    public static void distanceSelection()  {
        System.out.printf("\tBehold My Distance Selection!\n");
    }


    public static void gainLossSelection(double[] elevs)  {
        double elevation = elevs[0];
        double gained = 0.0;
        double lost = 0.0;
        double difference;
        int index = 1;
        while ((index < 10000) && (elevs[index] != 0.0))     {
            if (elevs[index] > elevation)   {
                difference = elevs[index] - elevation;
                gained = gained + difference;
            }   else if (elevs[index] < elevation)  {
                difference = elevation - elevs[index];
                lost = lost + difference;
            }
            index = index + 1;
            elevation = elevs[index];
        }
        System.out.printf("\tTotal elevation gained is " + meterRound(gained) + " meters.\n");
        System.out.printf("\tTotal elevation lost is " + meterRound(lost) + " meters.\n");

    }   // end of gainLossSelection


    public static void lowHighSelection(double[] elevs)   {
        System.out.printf("\tBehold My Elevation High And Low Selection!\n");
        double elevation = elevs[0];
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
        System.out.printf("\tMaximum elevation reached is " + meterRound(max) + " meters.\n");
        System.out.printf("\tMinimum elevation reached is " + meterRound(min) + " meters.\n");
    }   // end of lowHighSelection


    public static void speedTableSelection()    {
        System.out.printf("\tBehold My Speed Table!\n");
        int fourthMaxTrackpoints = MAX_TRACKPOINTS / 4;
        double[] speedTableSpeeds = new double[fourthMaxTrackpoints];
        String[] speedTableTimes = new String[fourthMaxTrackpoints];
    }


    public static double meterRound(double In)  {
        In = In * 100;
        In = Math.round(In);
        In = In / 100;
        return In;
    }


} // end of class
