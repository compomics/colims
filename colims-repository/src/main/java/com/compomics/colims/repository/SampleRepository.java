package com.compomics.colims.repository;

import com.compomics.colims.model.Material;
import com.compomics.colims.model.Protocol;
import com.compomics.colims.model.Sample;
import com.compomics.colims.model.SampleBinaryFile;

import java.util.List;

/**
 * This interface provides repository methods for the Sample class.
 *
 * @author Niels Hulstaert
 */
public interface SampleRepository extends GenericRepository<Sample, Long> {

    /**
     * Get the most used protocol.
     *
     * @return the most used Protocol instance.
     */
    Protocol getMostUsedProtocol();

    /**
     * Fetch the binary files for the given sample.
     *
     * @param sampleId the sample ID
     * @return the associated sample binary files
     */
    List<SampleBinaryFile> fetchBinaryFiles(Long sampleId);

    /**
     * Fetch the materials for the given sample.
     *
     * @param sampleId the sample ID
     * @return the associated materials
     */
    List<Material> fetchMaterials(Long sampleId);

}
