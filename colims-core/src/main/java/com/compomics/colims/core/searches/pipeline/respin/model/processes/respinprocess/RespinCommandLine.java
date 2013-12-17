package com.compomics.colims.core.searches.pipeline.respin.model.processes.respinprocess;

import com.compomics.colims.core.searches.pipeline.respin.control.configuration.RespinProperties;
import com.compomics.colims.core.searches.pipeline.respin.control.processrunner.ProcessEnum;
import com.compomics.colims.core.searches.pipeline.respin.control.processrunner.ProcessRunner;
import com.compomics.colims.core.searches.pipeline.respin.control.validation.SearchParamValidator;
import com.compomics.util.experiment.biology.EnzymeFactory;
import com.compomics.util.experiment.identification.SearchParameters;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Hello world!
 *
 */
public class RespinCommandLine {

    private static final Logger LOGGER = Logger.getLogger(RespinCommandLine.class);
    private final EnzymeFactory enzymeFactory = EnzymeFactory.getInstance();
    private RespinProperties respProps;
    private File fastaFile;
    private File searchguiJar;
    private File peptideshakerJar;
    private File searchParamFile;
    private String projectID = "default";
    private File outputDir;
    private File mgf;
    private int maxPepLength = 30;
    private int minPepLength = 5;
    private int misCleavages = 2;
    private ProcessRunner runner;
   

    public RespinCommandLine(File mgfFile, File searchParameterFile, String projectID) {
        try {
            File tempResultFolder = new File(System.getProperty("user.home") + "/.compomics/respin/temp_results/");
            FileUtils.deleteDirectory(tempResultFolder);
            tempResultFolder.mkdirs();
        } catch (Exception e) {
            //Does it matter? 
            LOGGER.error(e);
        }
        try {
            this.projectID = projectID;
            respProps = RespinProperties.getInstance();
            //purge temp dir
            File tempDir = new File(System.getProperty("user.home") + "/.compomics/respin/temp_results/");
            tempDir.delete();
            tempDir.mkdirs();
        } catch (IOException ex) {
            LOGGER.error(ex);
        } finally {
            this.mgf = mgfFile;
            this.searchParamFile = searchParameterFile;
        }
    }

    public RespinCommandLine(File mgfFile, File searchParameterFile, File fastaFile, String projectID) {
        this.fastaFile = fastaFile;
        try {
            File tempResultFolder = new File(System.getProperty("user.home") + "/.compomics/respin/temp_results/");
            FileUtils.deleteDirectory(tempResultFolder);
            tempResultFolder.mkdirs();
        } catch (IOException e) {
            //Does it matter? 
            LOGGER.error(e);
        }
        try {
            this.projectID = projectID;
            respProps = RespinProperties.getInstance();
            //purge temp dir
            File tempDir = new File(System.getProperty("user.home") + "/.compomics/respin/temp_results/");
            tempDir.delete();
            tempDir.mkdirs();
        } catch (IOException ex) {
            LOGGER.error(ex);
        } finally {
            this.mgf = mgfFile;
            this.searchParamFile = searchParameterFile;
        }
    }

    public RespinCommandLine setProjectID(String projectID) {
        if (projectID == null || projectID.isEmpty()) {
            return this;
        }
        this.projectID = projectID;
        return this;
    }

    public RespinCommandLine setMaxPepLen(int maxPepLen) {
        if (maxPepLen == 0) {
            return this;
        }
        this.maxPepLength = maxPepLen;
        return this;
    }

    public RespinCommandLine setMinPepLen(int minPepLen) {
        if (minPepLen == 0) {
            return this;
        }
        this.minPepLength = minPepLen;
        return this;
    }

    public RespinCommandLine setMisCleavages(int misCleavages) {
        if (misCleavages == 0) {
            return this;
        }
        this.misCleavages = misCleavages;
        return this;
    }

    public RespinCommandLine setSearchGUIFolder(File searchguiJar) {
        if (searchguiJar != null) {
            if (!searchguiJar.exists()) {
                return this;
            }
            this.searchguiJar = searchguiJar;
        }
        return this;
    }

    public RespinCommandLine setPeptideShakerFolder(File peptideshakerJar) {
        if (peptideshakerJar != null) {
            if (!peptideshakerJar.exists()) {
                return this;
            }
            this.peptideshakerJar = peptideshakerJar;
        }
        return this;
    }

    public RespinCommandLine setFasta(File fasta) {
        if (fasta != null) {
            if (!fasta.exists()) {
                LOGGER.error(fasta.getAbsolutePath() + " does not exist!");
                return this;
            }
            this.fastaFile = fasta;
        }
        return this;
    }

    public void init() throws RespinException {
        LOGGER.debug("Initializing Respin");
        try {
            File enzymeFile = respProps.getEnzymeFile();
            enzymeFactory.importEnzymes(enzymeFile);
        } catch (XmlPullParserException ex) {
            LOGGER.error(ex);
            throw new RespinException("Error initialising :");
        } catch (IOException ex) {
            LOGGER.error(ex);
            throw new RespinException("Error initialising :");
        }
    }

    public void adjustParametersToUserSpecs() throws FileNotFoundException, IOException, ClassNotFoundException {
        try {
            File tempParameters = new File(System.getProperty("user.home") + "/.compomics/respin/temp_results/SearchGUI.parameters");
            SearchParameters parameters;
            if (searchParamFile != null) {
                if (!searchParamFile.equals(tempParameters)) {
                    FileUtils.copyFile(searchParamFile, tempParameters, true);
                }
            }
            parameters = SearchParameters.getIdentificationParameters(tempParameters);
            //   SearchParameters parameters = SearchParameters.getIdentificationParameters(searchParamFile);
            if (fastaFile != null) {
                LOGGER.info("Setting fasta file to " + fastaFile.getName());
                parameters.setFastaFile(fastaFile);
            }

            if (parameters.getEnzyme() == null) {
                LOGGER.info("Setting enzyme to Trypsin");
                parameters.setEnzyme(enzymeFactory.getEnzyme("Trypsin"));
            }
            LOGGER.info("Setting min pep length to " + this.minPepLength);
            LOGGER.info("Setting max pep length to " + this.maxPepLength);
            LOGGER.info("Setting missed cleavages to " + this.misCleavages);
            parameters.setMinPeptideLength(this.minPepLength);
            parameters.setMaxPeptideLength(this.maxPepLength);
            parameters.setnMissedCleavages(this.misCleavages);
            //verify arguments
            SearchParamValidator.validate(parameters);
            LOGGER.debug("SearchGUI parameters : ");
            System.out.println("PROCESS_PARAMS>>>PARAM>>>" + System.lineSeparator() + parameters);
            SearchParameters.saveIdentificationParameters(parameters, new File(System.getProperty("user.home") + "/.compomics/respin/temp_results/SearchGUI.parameters"));
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e);
        }

    }

    public void runSearchGUI() throws ConfigurationException, IOException, InterruptedException {
        LOGGER.debug("Running SearchGUI");
        runner = new ProcessRunner(mgf, searchParamFile);
        runner.setProcess(ProcessEnum.SEARCHGUI).setOutputFolder(respProps.getTempResultDirectory())
                .startProcess();
    }

    public void runPeptideShaker() throws ConfigurationException, IOException, InterruptedException {
        LOGGER.debug("Finished SearchGUI");
        LOGGER.debug("Running PeptideShaker");
        runner.setProjectID(this.projectID)
                .setOutputFolder(respProps.getTempResultDirectory())
                .setProcess(ProcessEnum.PEPTIDESHAKER)
                .startProcess();
    }

    public void storeResults() throws IOException {
        LOGGER.debug("Storing results");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        //needed : MGF,SearchParameters, CPS and Text
        File[] resultFiles = new File(System.getProperty("user.home") + "/.compomics/respin/temp_results/").listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                LOGGER.debug(pathname.getAbsolutePath().toLowerCase());
                if (pathname.getAbsolutePath().toLowerCase().endsWith(".cps")
                        || pathname.getAbsolutePath().toLowerCase().endsWith(".mgf")) {
                    return true;
                } else {
                    pathname.delete();
                    return false;
                }
            }
        });

        for (File aResultingFile : resultFiles) {
            File finalResultFile = new File(outputDir.getAbsolutePath() + "/" + aResultingFile.getName());
            LOGGER.debug("Storing " + finalResultFile.getAbsolutePath());
            FileUtils.copyFile(aResultingFile, finalResultFile);
            aResultingFile.delete();
        }
    }

    public void cleanUp() {

        LOGGER.debug("Cleaning up...");
        try {
            File tempFile = new File(System.getProperty("user.home") + "/.compomics/respin/temp_results/");
            File[] wasteFiles = tempFile.listFiles();
            for (File aFile : wasteFiles) {
                try {
                    aFile.delete();
                } catch (Exception e) {
                    LOGGER.error(e);
                }
            }
        } catch (Exception e) {
            //do this to avoid fake "failures" due to busy mgf file
        }
        LOGGER.debug("Emptying peptideshaker matches folder");
        try {
            File peptideShakerMainFolder = new File(RespinProperties.getInstance().getPeptideShakerJarPath()).getParentFile();
            File peptideShakerMatchesFolder = new File(peptideShakerMainFolder, "resources/matches");
            System.out.println(peptideShakerMatchesFolder.getAbsolutePath());
            File[] wasteFiles = peptideShakerMatchesFolder.listFiles();
            for (File aFile : wasteFiles) {
                if (aFile.isDirectory()) {
                    try {
                        FileUtils.deleteDirectory(aFile);
                    } catch (Exception e) {
                        LOGGER.error(e);
                    }
                }
            }
        } catch (IOException ex) {
            LOGGER.error(ex);
        }

    }

    public RespinCommandLine setOutputDir(File outputDir) {
        this.outputDir = outputDir;
        if (!outputDir.exists()) {
            outputDir.mkdir();
        }
        return this;
    }

    public File getSearchParametersFile() {
        return searchParamFile;
    }

    public File getMGF() {
        return mgf;
    }
}
