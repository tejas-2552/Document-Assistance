package com.docAssistance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import jakarta.annotation.PostConstruct;
import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class DocumentAssistanceApplication {

	@Autowired
	private EmbeddingStoreIngestor embeddingStoreIngestor;

	/*
	 * public DocumentAssistanceApplication(EmbeddingStoreIngestor
	 * embeddingStoreIngestor) { this.embeddingStoreIngestor =
	 * embeddingStoreIngestor; }
	 */

    @PostConstruct
    public void init() {
        Document document = loadDocument(toPath("ONDC - API Contract for Retail (v1.2.0).pdf"), new ApachePdfBoxDocumentParser());
       embeddingStoreIngestor.ingest(document);
    }

    private static Path toPath(String fileName) {
        try {
            URL fileUrl = DocumentAssistanceApplication.class.getClassLoader().getResource(fileName);
            return Paths.get(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

	public static void main(String[] args) {
		//SpringApplication.run(DocumentAssistanceApplication.class, args);
	}

}
