/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.UniProtService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Implementation of the UniProtService interface.
 *
 * @author demet
 */
@Service("uniProtService")
public class UniProtServiceImpl implements UniProtService {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(OlsServiceImpl.class);

    private static final String UNIPROT_BASE_URL = "http://www.uniprot.org/uniprot";

    /**
     * The Spring RestTemplate instance for accessing the OLS rest API.
     */
    private final RestTemplate restTemplate;

    @Autowired
    public UniProtServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Map<String, String> getUniProtByAccession(String accession) throws RestClientException, IOException {
        Map<String, String> uniProt = new HashMap<>();

        try {
            // Set XML content type explicitly to force response in XML (If not spring gets response in JSON)
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<String> response = restTemplate.exchange(UNIPROT_BASE_URL + "/" + accession + ".xml", HttpMethod.GET, entity, String.class);
            String responseBody = response.getBody();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(responseBody));

            Document document = builder.parse(is);
            document.getDocumentElement().normalize();
            
            Element element;
            
            NodeList nodeList = document.getElementsByTagName("recommendedName");
            Node node = nodeList.item(0);
            if(node != null){
                element = (Element) node;
                if (element.getElementsByTagName("fullName").item(0).getTextContent() != null && !element.getElementsByTagName("fullName").item(0).getTextContent().equals("")) {
                    uniProt.put("description", element.getElementsByTagName("fullName").item(0).getTextContent());
                }
            }else{
                nodeList = document.getElementsByTagName("protein");
                node = nodeList.item(0);
                if(node != null){
                    element = (Element) node;
                    if (element.getElementsByTagName("fullName").item(0).getTextContent() != null && !element.getElementsByTagName("fullName").item(0).getTextContent().equals("")) {
                        uniProt.put("description", element.getElementsByTagName("fullName").item(0).getTextContent());
                    }
                }
            }
            

            nodeList = document.getElementsByTagName("organism");
            node = nodeList.item(0);
            element = (Element) node;
            if (element.getElementsByTagName("name").item(0).getTextContent() != null && !element.getElementsByTagName("name").item(0).getTextContent().equals("")) {
                uniProt.put("species", element.getElementsByTagName("name").item(0).getTextContent());
            }

            nodeList = document.getElementsByTagName("dbReference");
            node = nodeList.item(0);
            element = (Element) node;
            if (element.getAttribute("id") != null && !element.getAttribute("id").equals("")) {
                uniProt.put("taxid", element.getAttribute("id"));
            }

        } catch (HttpClientErrorException ex) {
            LOGGER.error(ex.getMessage(), ex);
            //ignore the exception if the namespace doesn't correspond to an ontology
            if (!ex.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                throw ex;
            }
        } catch (ParserConfigurationException | SAXException ex) {
            java.util.logging.Logger.getLogger(UniProtServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return uniProt;
    }

}
