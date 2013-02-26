/**
 * Created by IntelliJ IDEA. User: Lennart Date: 20-jan-2004 Time: 10:10:00
 */
package com.compomics.colims.core.io.model;

import com.compomics.util.interfaces.SpectrumFile;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

/*
 * CVS information:
 *
 * $Revision: 1.4 $
 * $Date: 2009/11/06 11:47:15 $
 */
/**
 * This class presents an abstract ancestor for all standard SpectrumFile
 * implementations.
 *
 * @author Lennart Martens
 * @version $Id: SpectrumFileAncestor.java,v 1.4 2009/11/06 11:47:15 kenny Exp $
 */
public abstract class SpectrumFileAncestor implements SpectrumFile {

    /**
     * This variable holds the filename for the spectrum file.
     */
    protected String filename = null;
    /**
     * This HashMap holds all the peaks in the spectrum file.
     */
    protected HashMap peaks = new HashMap();
    /**
     * This variable holds the precursor M/Z
     */
    protected double precursorMz = -1.0;
    /**
     * This variable holds the charge state.
     */
    protected int charge = 0;
    /**
     * The precursor intensity.
     */
    protected double intensity = -1.0;

    /**
     * This method reports on the charge of the precursor ion. Note that when
     * the charge could not be determined, this method will return '0'.
     *
     * @return int with the charge of the precursor, or '0' if no charge state
     * is known.
     */
    @Override
    public int getCharge() {
        return charge;
    }

    /**
     * This method reports on the filename for the file.
     *
     * @return String with the filename for the file.
     */
    @Override
    public String getFilename() {
        return filename;
    }

    /**
     * This method reports on the intensity of the precursor ion.
     *
     * @return double with the intensity of the precursor ion.
     */
    @Override
    public double getIntensity() {
        return intensity;
    }

    /**
     * This method reports on the peaks in the spectrum, with the Doubles for
     * the masses as keys in the HashMap, and the intensities for each peak as
     * Double value for that mass key.
     *
     * @return HashMap with Doubles as keys (the masses) and Doubles as values
     * (the intensities).
     */
    @Override
    public HashMap getPeaks() {
        return peaks;
    }

    /**
     * This method reports on the precursor M/Z
     *
     * @return double with the precursor M/Z
     */
    @Override
    public double getPrecursorMZ() {
        return precursorMz;
    }

    /**
     * This method sets the charge of the precursor ion. When the charge is not
     * known, it should be set to '0'.
     *
     * @param charge int with the charge of the precursor ion.
     */
    @Override
    public void setCharge(int charge) {
        this.charge = charge;
    }

    /**
     * This method sets the filename for the file.
     *
     * @param filename String with the filename for the file.
     */
    @Override
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * This method sets the intensity of the precursor ion.
     *
     * @param intensity double with the intensity of the precursor ion.
     */
    @Override
    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    /**
     * This method sets the peaks on the spectrum. Doubles for the masses as
     * keys in the HashMap, and the intensities for each peak as Double value
     * for that mass key.
     *
     * @param peaks HashMap with Doubles as keys (the masses) and Doubles as
     * values (the intensities).
     */
    @Override
    public void setPeaks(HashMap peaks) {
        this.peaks = peaks;
    }

    /**
     * This method sets the precursor M/Z on the file.
     *
     * @param precursorMZ double with the precursor M/Z
     */
    @Override
    public void setPrecursorMZ(double precursorMZ) {
        this.precursorMz = precursorMZ;
    }

    @Override
    public double getTotalIntensity() {
        Iterator iter = this.peaks.values().iterator();
        double totalIntensity = 0.0;
        while (iter.hasNext()) {
            totalIntensity += (Double) iter.next();
        }
        return round(totalIntensity);
    }

    @Override
    public double getHighestIntensity() {
        Iterator iter = this.peaks.values().iterator();
        double highestIntensity = -1.0;
        while (iter.hasNext()) {
            double temp = (Double) iter.next();
            if (temp > highestIntensity) {
                highestIntensity = temp;
            }
        }
        return round(highestIntensity);
    }

    private double round(final double totalIntensity) {
        BigDecimal bd = new BigDecimal(totalIntensity).setScale(2, RoundingMode.UP);
        return bd.doubleValue();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + Objects.hashCode(this.filename);
        hash = 17 * hash + Objects.hashCode(this.peaks);
        hash = 17 * hash + (int) (Double.doubleToLongBits(this.precursorMz) ^ (Double.doubleToLongBits(this.precursorMz) >>> 32));
        hash = 17 * hash + this.charge;
        hash = 17 * hash + (int) (Double.doubleToLongBits(this.intensity) ^ (Double.doubleToLongBits(this.intensity) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SpectrumFileAncestor other = (SpectrumFileAncestor) obj;
        if (!Objects.equals(this.filename, other.filename)) {
            return false;
        }
        if (!Objects.equals(this.peaks, other.peaks)) {
            return false;
        }
        if (Double.doubleToLongBits(this.precursorMz) != Double.doubleToLongBits(other.precursorMz)) {
            return false;
        }
        if (this.charge != other.charge) {
            return false;
        }
        if (Double.doubleToLongBits(this.intensity) != Double.doubleToLongBits(other.intensity)) {
            return false;
        }
        return true;
    }
        
}
