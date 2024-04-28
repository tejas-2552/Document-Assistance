package com.docAssistance;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.langchain4j.chain.ConversationalRetrievalChain;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/chat")

public class ChatController {

	
	@Autowired
	private ConversationalRetrievalChain conversationalRetrievalChain;

	@PostMapping
	public String chatWithPdf(@RequestBody String text) {
		///var answer = conversationalRetrievalChain.execute(text);
		return "Hello world";
	}
}
