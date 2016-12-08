/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.util;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.CharBuffer;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 *
 * @author demet
 */
public class AccessionConverter {

    /**
     * properties configuration to read properties file.
     */
    private static PropertiesConfiguration config;

    /**
     * This method takes any accession from given database and converts to
     * UniProt.
     *
     * @param accession
     * @param databaseName
     * @return list of UniProt accessions.
     * @throws IOException
     */
    public static List<String> convertToUniProt(String accession, String databaseName) throws IOException {
        List<String> accessions = new ArrayList<>();
        StringBuilder url = new StringBuilder();
        url.append("http://www.ebi.ac.uk/Tools/picr/rest/getUPIForAccession?accession=");
        // if database is defined, search using given database
        if (!databaseName.equals("CONTAMINANTS")) {
            url.append(accession).append("&database=").append(databaseName);
            String xmlPage = getPage(url.toString());
            int startIndex = 0;
            while (xmlPage.indexOf("<databaseName>" + databaseName + "</databaseName>", startIndex) != -1) {
                startIndex = xmlPage.indexOf("<databaseName>" + databaseName + "</databaseName>", startIndex) + 1;
                if (xmlPage.indexOf("<identicalCrossReferences>", startIndex - 300) != -1) {
                    String feature = xmlPage.substring(xmlPage.indexOf("<identicalCrossReferences>", startIndex - 300), xmlPage.indexOf("</identicalCrossReferences>", xmlPage.indexOf("<identicalCrossReferences>", startIndex - 300)));
                    String description = feature.substring(feature.indexOf("<databaseDescription>") + 21, feature.indexOf("</databaseDescription>"));
                    if (description.contains("UniProtKB/")) {
                        String sub = feature.substring(feature.indexOf("<accession>") + 11, feature.indexOf("</accession>"));
                        accessions.add(sub);
                    }
                }
            }
        // if protein is from contaminants fasta file, search using all database in the properties file
        } else {
            if (accession.contains("CON")) {
                accession = org.apache.commons.lang3.StringUtils.substringAfter(accession, "CON__");
            }
            url.append(accession);
            // read the database properties file
            try {
                config = PropertiesUtil.parsePropertiesFile("config/embl-ebi-database.properties");
            } catch (ConfigurationException ex) {
                Logger.getLogger(AccessionConverter.class.getName()).log(Level.SEVERE, null, ex);
            }
            Iterator<String> keys = config.getKeys();
            while (keys.hasNext()) {
                String key = keys.next();
                if(config.getString(key).equals("1")){
                    url.append("&database=").append(key);
                }
            }
            String xmlPage = getPage(url.toString());
            keys = config.getKeys();
            while (keys.hasNext()) {
                String database = keys.next();
                int startIndex = 0;
                while (xmlPage.indexOf("<databaseName>" + database + "</databaseName>", startIndex) != -1) {
                    startIndex = xmlPage.indexOf("<databaseName>" + database + "</databaseName>", startIndex) + 1;
                    if (xmlPage.indexOf("<identicalCrossReferences>", startIndex - 300) != -1) {
                        String feature = xmlPage.substring(xmlPage.indexOf("<identicalCrossReferences>", startIndex - 300), xmlPage.indexOf("</identicalCrossReferences>", xmlPage.indexOf("<identicalCrossReferences>", startIndex - 300)));
                        String description = feature.substring(feature.indexOf("<databaseDescription>") + 21, feature.indexOf("</databaseDescription>"));
                        if (description.contains("UniProtKB/")) {
                            String sub = feature.substring(feature.indexOf("<accession>") + 11, feature.indexOf("</accession>"));
                            accessions.add(sub);
                        }
                    }
                }
            }
        }
        return accessions;

    }

    /**
     * fetches the html page at the passed url as a string
     *
     * @param aUrl the url to fetch the html page from
     * @return the page at the url in textual form
     * @throws IOException
     */
    public static String getPage(String aUrl) throws IOException {
        StringBuilder input = new StringBuilder();
        String htmlPage;
        try (Reader r = openReader(aUrl)) {
            CharBuffer buffer = CharBuffer.allocate(256);
            while ((r.read(buffer)) != -1) {
                input.append(buffer.flip());
                buffer.clear();
            }
        }catch(IOException e){
            throw new IOException("EMBL-EBI server is not available at the moment, please try again later.");
        }
        htmlPage = input.toString();
        return htmlPage;
    }

    /**
     * convenience method for opening a buffered reader to an url, standard
     * timeout is set to 500 milliseconds
     *
     * @param aUrl the url to open a reader to
     * @return the buffered reader for reading the url
     * @throws IOException if the url could not be read
     */
    public static BufferedReader openReader(String aUrl) throws IOException {
        URL myURL = new URL(aUrl);
        HttpURLConnection c = (HttpURLConnection) myURL.openConnection();
        c.setConnectTimeout(500);
        return new BufferedReader(new InputStreamReader(new BufferedInputStream(c.getInputStream()), "UTF-8"));
    }

    public static BufferedInputStream openStream(String aURL) throws IOException {
        URL myURL = new URL(aURL);
        HttpURLConnection c = (HttpURLConnection) myURL.openConnection();
        c.setConnectTimeout(500);
        return new BufferedInputStream(c.getInputStream());

    }

}
