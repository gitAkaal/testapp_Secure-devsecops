package com.wordstop;

import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/xml")
public class XMLController {

    @PostMapping("/parse")
    public Map<String, Object> parseXML(@RequestBody String xmlContent) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Vulnerable XML Parser configuration
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", true);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", true);
            factory.setXIncludeAware(true);
            factory.setExpandEntityReferences(true);
            
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(xmlContent.getBytes()));
            
            response.put("status", "success");
            response.put("root", document.getDocumentElement().getTagName());
        } catch (Exception e) {
            response.put("status", "error");
            response.put("error", e.toString());
        }
        return response;
    }

    @PostMapping("/transform")
    public Map<String, String> transformXML(@RequestBody String xmlContent, @RequestParam String xsltUrl) {
        Map<String, String> response = new HashMap<>();
        try {
            // Vulnerable: Remote XSLT processing
            // This allows SSRF and potential RCE through malicious XSLT
            response.put("status", "processing");
            response.put("xslt_url", xsltUrl);
            // Implementation omitted for brevity
        } catch (Exception e) {
            response.put("error", e.toString());
        }
        return response;
    }

    @GetMapping("/schema")
    public String getXMLSchema() {
        // Information disclosure - returns internal XML schema
        return "<?xml version=\"1.0\"?>\n" +
               "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n" +
               "  <!-- Internal system structure exposed -->\n" +
               "  <xs:element name=\"credentials\">\n" +
               "    <xs:complexType>\n" +
               "      <xs:sequence>\n" +
               "        <xs:element name=\"username\" type=\"xs:string\"/>\n" +
               "        <xs:element name=\"password\" type=\"xs:string\"/>\n" +
               "        <xs:element name=\"database\" type=\"xs:string\"/>\n" +
               "      </xs:sequence>\n" +
               "    </xs:complexType>\n" +
               "  </xs:element>\n" +
               "</xs:schema>";
    }
}
