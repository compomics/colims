/**
 * Created by IntelliJ IDEA. User: Lennart Date: 20-jan-2004 Time: 10:07:23
 */
package com.compomics.colims.core.io.model;

import com.compomics.mslims.util.mascot.MascotIdentifiedSpectrum;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import org.apache.log4j.Logger;

/*
 * CVS information:
 *
 * $Revision: 1.3 $
 * $Date: 2007/03/08 10:14:19 $
 */
/**
 * This class maps a Mascot Generic File to memory. It allows for search ad
 * retrieval as well as comparing functionality.
 *
 * @author Lennart
 * @version $Id: MascotGenericFile.java,v 1.3 2007/03/08 10:14:19 kenny Exp $
 */
public class MascotGenericFile extends SpectrumFileAncestor {
    // Class specific log4j logger for MascotGenericFile instances.

    private static Logger logger = Logger.getLogger(MascotGenericFile.class);
    /**
     * This variable holds the comments for this MascotGenericFile.
     */
    private String comments = null;
    /**
     * The title of the MascotGenericFile.
     */
    private String title = null;
    /**
     * This HashMap will hold the charges for those ions for which a charge is
     * known.
     */
    private HashMap charges = new HashMap();
    /**
     * This constant defines the key in the spectrum header for the title.
     */
    private static final String TITLE = "TITLE";
    /**
     * This constant defines the key in the spectrum header for the precursor
     * M/Z and intensity.
     */
    private static final String PEPMASS = "PEPMASS";
    /**
     * This constant defines the key in the spectrum header for the precursor
     * charge. Note that this field can be omitted from a MascotGenericFile.
     */
    private static final String CHARGE = "CHARGE";
    /**
     * This constant defines the start of a comment line.
     */
    private static final String COMMENT_START = "###";
    /**
     * This constant defines the start tag for the ions.
     */
    private static final String IONS_START = "BEGIN IONS";
    /**
     * This constant defines the ernd tag for the ions.
     */
    private static final String IONS_END = "END IONS";
    /**
     * This constant defines the retention time tag for the precursor ion.
     */
    private static final String RETENTION = "RTINSECONDS";
    /**
     * This constant defines the scan number tags for the Mascot generic file.
     */
    private static final String SCAN_NUMBERS = "SCANS";
    /**
     * This Properties instance contains all the Embedded properties that are
     * listed in a Mascot Generic File.
     */
    private Properties extraEmbeddedParameters;

    /**
     * This constructor takes the MGF File as a String as read from file or DB.
     * The filename is specified separately here.
     *
     * @param filename String with the filename for the MGF File.
     * @param contents String with the contents of the MGF File.
     */
    public MascotGenericFile(String filename, String contents) {
        this.parseFromString(contents);
        this.filename = filename;
    }

    /**
     * This empty constructor enables the creation of a new MascotGenericFile
     * via its setters.
     */
    public MascotGenericFile() {
        // Empty constructor to create new file from scratch.
    }

    /**
     * This constructor takes the filename of the MGF File as argument and loads
     * it form the hard drive.
     *
     * @param file File with the pointer to the MGF File.
     * @throws IOException when the file could not be read.
     */
    public MascotGenericFile(File file) throws IOException {
        if (!file.exists()) {
            throw new IOException("MGF File '" + file.getCanonicalPath() + "' was not found!");
        } else {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                StringBuilder lsb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    lsb.append(line).append("\n");
                }

                this.parseFromString(lsb.toString());
                this.filename = file.getName();
            }
        }
    }

    /**
     * This method checks whether the MascotIdentifiedSpectrum corresponds to
     * this spectrum. The precise method for comparison is up to the individual
     * implementations.
     *
     * @param mascotIdentifiedSpectrum MascotIdentifiedSpectrum to compare to.
     * @return boolean which indicates whether these objects correspond.
     */
    public boolean corresponds(MascotIdentifiedSpectrum mascotIdentifiedSpectrum) {
        boolean corresponds = false;
        // The search title can yield the information we need.
        // Typically, the title is maintained, or, if it is not, we simply need to
        // check against the filename (in the latter case, we have substituted the
        // title with the filename ourselves).
        String searchTitle = mascotIdentifiedSpectrum.getSearchTitle();
        if (this.title.equals(searchTitle) || this.filename.equals(searchTitle)) {
            corresponds = true;
        }

        // Return the result.
        return corresponds;
    }

    /**
     * This method allows to write the spectrum file to the specified
     * OutputStream.
     *
     * @param outputStream OutputStream to write the file to. This Stream will
     * <b>NOT</b> be closed by this method.
     * @throws IOException when the write operation fails.
     */
    @Override
    public void writeToStream(OutputStream outputStream) throws IOException {
        this.writeToStream(outputStream, false);
    }

    /**
     * This method allows to write the MascotGenericFile to the specified
     * OutputStream.
     *
     * @param aOut OutputStream to write the file to. This Stream will
     * <b>NOT</b> be closed by this method.
     * @param aSubstituteFilename if this boolean is true, the filename is set
     * to be the title in the output header. If it is false, the title is set as
     * the title.
     * @throws IOException when the write operation fails.
     */
    public void writeToStream(OutputStream aOut, boolean aSubstituteFilename) throws IOException {
        this.writeToWriter(new OutputStreamWriter(aOut), aSubstituteFilename);
    }

    /**
     * This method allows the caller to write the spectrum file to the specified
     * folder using its current filename.
     *
     * @param parentDir File with the parent directory to put the file in.
     * @throws IOException whenever the write process failed.
     */
    @Override
    public void writeToFile(File parentDir) throws IOException {
        if (!parentDir.exists() && !parentDir.isDirectory()) {
            throw new IOException("Parent '" + parentDir.getCanonicalPath() + "' does not exist or is not a directory!");
        }
        File output = new File(parentDir, this.filename);
        
        //write to stream
        try (FileOutputStream fos = new FileOutputStream(output)) {
            this.writeToStream(fos);
            fos.flush();
        }
    }

    /**
     * This methods returns the comments for this MascotGenericFile.
     *
     * @return String with the comments for this MascotGenericFile.
     */
    public String getComments() {
        return comments;
    }

    /**
     * This method sets the comments for this MascotGenericFile.
     *
     * @param aComments String with the comments for this MAscotGenericFile.
     */
    public void setComments(String aComments) {
        comments = aComments;
    }

    /**
     * This method reports on the title of the MascotGenericFile.
     *
     * @return String with the title for the MascotGenericFile.
     */
    public String getTitle() {
        return title;
    }

    /**
     * This method allows the setting of the title for the MascotGenericFile.
     *
     * @param aTitle String with the title for the MascotGenericFile.
     */
    public void setTitle(String aTitle) {
        title = aTitle;
    }

    /**
     * This method returns the Value of the corresponding embedded parameter
     * Key.
     *
     * @param aKey String with the Key of the embedded parameter.
     * @return String Value of the embedded parameter Key.
     */
    public String getExtraEmbeddedProperty(String aKey) {
        String lReturn = "NoSuchKey";
        if (extraEmbeddedParameters != null) {
            if (extraEmbeddedParameters.containsKey(aKey)) {
                lReturn = (String) extraEmbeddedParameters.get(aKey);
            }
        }
        return lReturn;
    }

    /**
     * This private method can be called during the parsing of the aFileContents
     * String to save embedded parameters in the Properties instance.
     *
     * @param aKey Embedded Property Key.
     * @param aValue Embedded Property Value.
     */
    private void addExtraEmbeddedParameter(String aKey, String aValue) {
        if (extraEmbeddedParameters == null) {
            extraEmbeddedParameters = new Properties();
        }
        extraEmbeddedParameters.put(aKey, aValue);
    }

    /**
     * This method compares two MascotGenericFiles and allows them to be sorted
     * relative to each other. Sorting is done on the basis of precursor M/Z (we
     * cannot always calculate mass due to the possible absence of charge
     * information).
     *
     * @param anObject MascotGenericFile to compare this instance to.
     * @return int with the code for sorting (negative, positive or 0).
     */
    @Override
    public int compareTo(Object anObject) {
        int result = 0;

        // Comparison is done based on precursor M/Z. We cannot reliably calculate
        // the mass, since (many) MGF spectra do not include charge information.
        MascotGenericFile other = (MascotGenericFile) anObject;

        double intermediate_result = (this.getPrecursorMZ() - other.getPrecursorMZ());

        if (intermediate_result > 0) {
            result = 1;
        } else if (intermediate_result < 0) {
            result = -1;
        } else {
            result = 0;
        }

        return result;
    }

    /**
     * This method checks for equality between this object and the specified
     * object.
     *
     * @param anObject Object to test equality with.
     * @return boolean indicating whether the presented objects are equal
     * ('true') or not ('false').
     */
    @Override
    public boolean equals(Object anObject) {
        boolean result = false;

        if (anObject != null && anObject instanceof MascotGenericFile) {
            MascotGenericFile other = (MascotGenericFile) anObject;
            if (this.filename.equals(other.filename) && this.charge == other.charge
                    && this.title.equals(other.title) && this.peaks.equals(other.peaks)
                    && this.charges.equals(other.charges)) {
                result = true;
            }
        }

        return result;
    }

    /**
     * This method returns a String representation of this MGF file.
     *
     * @return String with the String representation of this object.
     */
    @Override
    public String toString() {
        String result = null;
        StringWriter sw = new StringWriter();
        try {
            this.writeToWriter(sw, false);
            result = sw.toString();
            sw.close();
        } catch (IOException ioe) {
            // No exceptions are expected here.
            logger.error(ioe.getMessage(), ioe);
        }
        return result;
    }

    /**
     * This method returns a String representation of this MGF file.
     *
     * @param aSubstituteFilename if this boolean is true, the filename is set
     * to be the title in the output header. If it is false, the title is set as
     * the title.
     * @return String with the String representation of this object.
     */
    public String toString(boolean aSubstituteFilename) {
        String result = null;
        StringWriter sw = new StringWriter();
        try {
            this.writeToWriter(sw, aSubstituteFilename);
            result = sw.toString();
            sw.close();
        } catch (IOException ioe) {
            // No exceptions are expected here.
            logger.error(ioe.getMessage(), ioe);
        }
        return result;
    }

    /**
     * This method formats an integer to a charge String as used in a
     * MascotGenericFile (eg., 1 to 1+).
     *
     * @param aCharge int with the charge to format.
     * @return String with the formatted charge (eg., 1+).
     */
    private String processCharge(int aCharge) {
        // The charge notation is '1+', or conversely, '1-'.
        // Therefore we do some extra processing.
        String affix = "+";
        if (aCharge < 0) {
            affix = "-";
        }
        return Math.abs(aCharge) + affix;
    }

    /**
     * This method extracts an integer from Mascot Generic File charge notation,
     * eg., 1+. Remark that the charge can also be annotated as "+2,+3", in
     * those rather cases the charge is also "not known." So we save a zero
     * value.
     *
     * @param aCharge String with the Mascot Generic File charge notation (eg.,
     * 1+).
     * @return int with the corresponding integer.
     */
    private int extractCharge(String aCharge) {
        int charge = 0;

        // Trim the charge String.
        String trimmedCharge = aCharge.trim();

        boolean negate = false;
        boolean multiCharge = false;

        // See if there is a '-' in the charge String.
        if (trimmedCharge.indexOf("-") >= 0) {
            negate = true;
        }

        // See if there are multiple charges assigned to this spectrum.
        if (trimmedCharge.indexOf(",") >= 0) {
            multiCharge = true;
        }

        if (!multiCharge) {
            // Charge is now: trimmedCharge without the sign character,
            if (trimmedCharge.length() == 1) {
                charge = Integer.parseInt(trimmedCharge);
            } else {
                // Charge is now: trimmedCharge including the sign character.
                // negated if necessary.
                charge = Integer.parseInt(trimmedCharge.substring(0, 1));
                if (negate) {
                    charge = -charge;
                }
            }

        }

        return charge;
    }

    /**
     * This method will parse the input String and read all the information
     * present into a MascotGenericFile object.
     *
     * @param aFileContent String with the contents of the file.
     */
    private void parseFromString(String aFileContent) {
        try {
            BufferedReader br = new BufferedReader(new StringReader(aFileContent));
            String line = null;
            // Cycle the file.
            int lineCount = 0;
            boolean inSpectrum = false;
            StringBuilder comments = new StringBuilder();
            while ((line = br.readLine()) != null) {
                // Advance line count.
                lineCount++;
                // Delete leading/trailing spaces.
                line = line.trim();
                // Skip empty lines.
                if (line.equals("")) {
                    continue;
                }
                // First line can be 'CHARGE'.
                if (lineCount == 1 && line.startsWith(CHARGE)) {
                    continue;
                }
                // Read all starting comments.
                if (line.startsWith("#")) {
                    comments.append(line).append("\n");
                } // BEGIN IONS marks the start of the real file.
                else if (line.equals(IONS_START)) {
                    inSpectrum = true;
                } // END IONS marks the end.
                else if (line.equals(IONS_END)) {
                    inSpectrum = false;
                } // Read embedded parameters. The most important parameters (such as TITLE, PEPMASS and optional CHARGE fields )
                // will be saved as instance variables as well as in the iEmbeddedParameter Properties instance.
                else if (inSpectrum && (line.indexOf("=") >= 0)) {
                    // Find the starting location of the value (which is one beyond the location
                    // of the '=').
                    int equalSignIndex = line.indexOf("=");

                    // See which header line is encountered.
                    if (line.startsWith(TITLE)) {
                        // TITLE line found.
                        this.setTitle(line.substring(equalSignIndex + 1));
                    } else if (line.startsWith(PEPMASS)) {
                        // PEPMASS line found.
                        String value = line.substring(equalSignIndex + 1).trim();
                        StringTokenizer st = new StringTokenizer(value, " \t");
                        this.setPrecursorMZ(Double.parseDouble(st.nextToken().trim()));
                        // It is possible that parent intensity is not mentioned. We then set it to '0'.
                        if (st.hasMoreTokens()) {
                            this.setIntensity(Double.parseDouble(st.nextToken().trim()));
                        } else {
                            this.setIntensity(0.0);
                        }
                    } else if (line.startsWith(CHARGE)) {
                        // CHARGE line found.
                        // Note the extra parsing to read a Mascot Generic File charge (eg., 1+).
                        this.setCharge(this.extractCharge(line.substring(equalSignIndex + 1)));
                    } else {
                        // This is an extra embedded parameter!
                        String aKey = line.substring(0, equalSignIndex);
                        String aValue = line.substring(equalSignIndex + 1);
                        // Save the extra embedded parameter in iEmbeddedParameter
                        addExtraEmbeddedParameter(aKey, aValue);
                    }
                } // Read peaks, minding the possibility of charge present!
                else if (inSpectrum) {
                    // We're inside the spectrum, with no '=' in the line, so it should be
                    // a peak line.
                    // A peak line should be either of the following two:
                    // 234.56 789
                    // 234.56 789   1+
                    StringTokenizer st = new StringTokenizer(line, " \t");
                    int count = st.countTokens();
                    if (count == 2 || count == 3) {
                        String temp = st.nextToken().trim();
                        Double mass = new Double(temp);
                        temp = st.nextToken().trim();
                        Double intensity = new Double(temp);
                        this.peaks.put(mass, intensity);
                        if (st.hasMoreTokens()) {
                            int charge = this.extractCharge(st.nextToken());
                            charges.put(mass, Integer.valueOf(charge));
                        }
                    } else {
                        logger.error("\n\nUnrecognized line at line number " + lineCount + ": '" + line + "'!\n");
                    }
                }
            }
            // Last but not least: add the comments.
            this.comments = comments.toString();
            // That's it.
            br.close();
        } catch (IOException ioe) {
            // We do not expect IOException when using a StringReader.
            logger.error(ioe.getMessage(), ioe);
        }
    }

    /**
     * This method extracts the retention time from the RTINSECONDS key, or
     * looks for 'min' in the TITLE.
     *
     * @return The retention time as a double value. If this MGF file is a sum
     * of scans, the double[] consists of multiple retention times for each scan
     * number. And if neither the RTINSECONDS value, or the 'min' value in the
     * title was found, the method returns NULL.
     */
    public double[] getRetentionInSeconds() {
        double[] lResult = null;
        // First, try to find if RTINSECONDS is within embedded parameters.
        if (extraEmbeddedParameters != null && extraEmbeddedParameters.containsKey(RETENTION)) {
            String lValue = String.valueOf(extraEmbeddedParameters.get(RETENTION));

            String[] lValues = lValue.split("[-,]");

            lResult = new double[lValues.length];
            for (int i = 0; i < lValues.length; i++) {
                String s = lValues[i];
                lResult[i] = Double.parseDouble(s);
            }

            // Second, try to find 'min' in the title section - for Esquire data!
        } else if (getTitle().toLowerCase().endsWith(" min")) {
            String lTitle = getTitle().toLowerCase();
            int lIndexStop = lTitle.indexOf(" min");
            int lIndexStart = lTitle.lastIndexOf(") ");

            if (lIndexStart == -1) {
                lIndexStart = lTitle.lastIndexOf("), ");
            }

            lResult = new double[]{0};

            if (lIndexStop != -1 && (lIndexStop - lIndexStart < 10)) {
                // We need an index to stop, and the length cannot be more then  5chars!! (max '999.9' min)
                String s = lTitle.substring(lIndexStart + 2, lIndexStop);
                double d = Double.parseDouble(s); // From minutes ...
                lResult[0] = d * 60; // .. to seconds!!

            }
        }
        return lResult;
    }

    /**
     * This method extracts the retention time from the RTINSECONDS key, or
     * looks for 'min' in the TITLE.
     *
     * @return The retention time as a double value. If this MGF file is a sum
     * of scans, the double[] consists of multiple retention times for each scan
     * number. And if neither the RTINSECONDS value, or the 'min' value in the
     * title was found, the method returns NULL.
     */
    public int[] getScanNumbers() {
        int[] lResult = null;
        // Try to find if SCAN_NUMBERS is within embedded parameters.
        if (extraEmbeddedParameters != null && extraEmbeddedParameters.containsKey(SCAN_NUMBERS)) {
            String lValue = String.valueOf(extraEmbeddedParameters.get(SCAN_NUMBERS));
            String[] lValues = lValue.split("[-,]");
            lResult = new int[lValues.length];
            for (int i = 0; i < lValues.length; i++) {
                String s = lValues[i];
                lResult[i] = Integer.parseInt(s);
            }
        }
        return lResult;
    }

    /**
     * This method writes the MGF object to the specified Writer.
     *
     * @param aWriter Writer to write a String representation of this class to.
     * @param aSubstituteFilename if this boolean is true, the filename is set
     * to be the title in the output header. If it is false, the title is set as
     * the title.
     * @throws IOException when the writing failed.
     */
    private void writeToWriter(Writer aWriter, boolean aSubstituteFilename) throws IOException {
        BufferedWriter bw = new BufferedWriter(aWriter);

        // Comments go first.
        bw.write(this.getComments());
        // Next the ion start tag.
        bw.write(IONS_START + "\n");
        // Now the title, or the filename if the substition flag is 'true'.
        if (aSubstituteFilename) {
            // Substituting the title with the filename.
            bw.write(TITLE + "=" + this.getFilename() + "\n");
        } else {
            // Keeping the title.
            bw.write(TITLE + "=" + this.getTitle() + "\n");
        }
        // Precursor M/Z and intensity (separated by a space).
        bw.write(PEPMASS + "=" + this.getPrecursorMZ() + " " + this.getIntensity() + "\n");
        // For charge: see if it is present first (charge != 0).
        // If it is not present, omit this line altogether.
        if (this.getCharge() != 0) {
            bw.write(CHARGE + "=" + this.processCharge(this.getCharge()) + "\n");
        }
        // Note that the retention time is also within the embedded parameters!!
        // If there are any extra embedded parameters in the mascot generic file,
        // also write them in this header section.
        if (this.extraEmbeddedParameters != null) {
            if (!extraEmbeddedParameters.isEmpty()) {
                Iterator iter = extraEmbeddedParameters.keySet().iterator();
                while (iter.hasNext()) {
                    String aKey = (String) iter.next();
                    String aValue = (String) extraEmbeddedParameters.get(aKey);
                    bw.write(aKey + "=" + aValue + "\n");
                }
            }
        }
        // After the header, it is customary to leave an empty line.
        bw.write("\n");
        // Next up the ions themselves.
        SortedSet ss = new TreeSet(this.getPeaks().keySet());
        Iterator it = ss.iterator();
        while (it.hasNext()) {
            Double tempKey = (Double) it.next();
            BigDecimal lDouble = new BigDecimal(tempKey.doubleValue()).setScale(4, BigDecimal.ROUND_HALF_UP);
            // We need to check whether a charge is known for this peak.
            String charge = "";
            if (charges.containsKey(tempKey)) {
                int chargeState = ((Integer) charges.get(tempKey)).intValue();
                charge = "\t" + this.processCharge(chargeState);
            }
            bw.write(lDouble.toString() + " " + new BigDecimal(((Double) this.peaks.get(tempKey)).doubleValue()).setScale(4, BigDecimal.ROUND_HALF_UP).toString() + charge + "\n");
        }

        bw.write(IONS_END);

        bw.flush();
    }

    /**
     * Returns whether this mgf file is created as a sum of different scans.
     *
     * @return boolean
     */
    public boolean isSumOfScans() {
        boolean b = false;
        String lEmbeddedProperty = getExtraEmbeddedProperty("SCANS");
        // Multiple scan numbers are separated by a dash sign.
        if (lEmbeddedProperty.split("-").length > 1) {
            b = true;
        }
        return b;
    }
}
