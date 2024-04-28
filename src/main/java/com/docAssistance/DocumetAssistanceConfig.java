package com.docAssistance;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.langchain4j.chain.ConversationalRetrievalChain;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.retriever.EmbeddingStoreRetriever;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.cassandra.AstraDbEmbeddingConfiguration;
import dev.langchain4j.store.embedding.cassandra.AstraDbEmbeddingStore;

@Configuration
public class DocumetAssistanceConfig {

	@Bean
	EmbeddingModel embeddingModel() {
		return new AllMiniLmL6V2EmbeddingModel();
	}

	@Bean
	AstraDbEmbeddingStore astraDbEmbeddingStore() {

		String astraDbToken = "{{DbToken}}";
		String astraDbId = "{{DbId}}";
		return new AstraDbEmbeddingStore(AstraDbEmbeddingConfiguration.builder().token(astraDbToken)
				.databaseId(astraDbId).databaseRegion("us-east-2").keyspace("\"documentAssistant\"").table("docchat")
				.dimension(384).build());

	}

	@Bean
	EmbeddingStoreIngestor embeddingStoreIngestor() {

		EmbeddingStoreIngestor emdStore = EmbeddingStoreIngestor.builder()
				.documentSplitter(DocumentSplitters.recursive(300, 0)).embeddingModel(embeddingModel())
				.embeddingStore(astraDbEmbeddingStore()).build();

		return emdStore;
	}

	@Bean
	ConversationalRetrievalChain conversationalRetrievalChain() {
		return ConversationalRetrievalChain.builder()
				.chatLanguageModel(
						OpenAiChatModel.withApiKey("{{OpenAIKey}}"))
				.retriever(EmbeddingStoreRetriever.from(astraDbEmbeddingStore(), embeddingModel())).build();

	}
	
	public ConversationalRetrievalChain conversationalRetrievalChainForOllama() {
        return ConversationalRetrievalChain.builder()
                .chatLanguageModel(OllamaChatModel.builder()
                        .baseUrl("http://localhost:11434")
                        .modelName("wizard-vicuna-uncensored:7b")
                        .build())
                .retriever(EmbeddingStoreRetriever.from(astraDbEmbeddingStore(), embeddingModel()))
                .build();
    }

	public static void main(String args[]) {
		DocumetAssistanceConfig doc = new DocumetAssistanceConfig();
		ConversationalRetrievalChain conversationalRetrievalChain = doc.conversationalRetrievalChainForOllama();
		EmbeddingStoreIngestor embeddingStoreIngestor = doc.embeddingStoreIngestor();
		Document document = loadDocument(toPath("cassandra.doc"),
				new ApachePdfBoxDocumentParser());
		embeddingStoreIngestor.ingest(document);
		var answer = conversationalRetrievalChain.execute("What is cassandra?");
		System.out.println("Question : " + "What is cassandra?");
		System.out.println("answer : " + answer);
	}

	private static Path toPath(String fileName) {
		try {
			URL fileUrl = DocumetAssistanceConfig.class.getClassLoader().getResource(fileName);
			return Paths.get(fileUrl.toURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
}
