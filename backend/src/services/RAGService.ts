import { OpenAIEmbeddings } from '@langchain/openai';
import { Document } from '@langchain/core/documents';
import { ChatOpenAI } from '@langchain/openai';
import { Chroma } from '@langchain/community/vectorstores/chroma';
import { SystemMessage, HumanMessage } from '@langchain/core/messages';

/**
 * Represents configuration options for RAGService
 */
interface RAGServiceConfig {
    openAIApiKey: string;
    vectorStoreUrl: string;
    maxContextLength?: number;
    temperature?: number;
}

/**
 * Interface for document metadata
 */
interface DocumentMetadata {
    relevanceScore?: number;
    [key: string]: any;
}

/**
 * Custom error class for RAG-related errors
 */
class RAGServiceError extends Error {
    constructor(message: string) {
        super(message);
        this.name = 'RAGServiceError';
    }
}

/**
 * Service class for Retrieval Augmented Generation (RAG)
 * Handles document retrieval and LLM querying with context
 */
class RAGService {
    private embeddings: OpenAIEmbeddings;
    private llm: ChatOpenAI;
    private vectorStore: Chroma | null = null;
    private maxContextLength: number;

    constructor(config: RAGServiceConfig) {
        this.embeddings = new OpenAIEmbeddings({
            openAIApiKey: config.openAIApiKey
        });

        this.llm = new ChatOpenAI({
            openAIApiKey: config.openAIApiKey,
            temperature: config.temperature || 0.7
        });

        this.maxContextLength = config.maxContextLength || 4000;
    }

    /**
     * Initialize connection to vector store
     */
    private async initVectorStore(vectorStoreUrl: string): Promise<void> {
        try {
            this.vectorStore = await Chroma.fromExistingCollection(
                this.embeddings,
                { 
                    collectionName: "documents",
                    url: vectorStoreUrl
                }
            );
        } catch (error: unknown) {
            console.error('Vector store initialization error:', error);
            // Create a new collection if it doesn't exist
            this.vectorStore = await Chroma.fromTexts(
                ["Initial document"],
                { source: "initialization" },
                this.embeddings,
                {
                    collectionName: "documents",
                    url: vectorStoreUrl
                }
            );
        }
    }

    /**
     * Ensure vector store is initialized before use
     */
    private async ensureVectorStore(vectorStoreUrl: string): Promise<void> {
        if (!this.vectorStore) {
            await this.initVectorStore(vectorStoreUrl);
        }
    }

    /**
     * Fetch relevant documents based on query similarity
     */
    public async fetchRelevantDocuments(query: string): Promise<Document<DocumentMetadata>[]> {
        try {
            if (!query.trim()) {
                throw new RAGServiceError('Query cannot be empty');
            }

            await this.ensureVectorStore(process.env.VECTOR_STORE_URL!);

            if (!this.vectorStore) {
                throw new RAGServiceError('Vector store not initialized');
            }

            const documents = await this.vectorStore.similaritySearch(query, 5);
            return documents;

        } catch (error: unknown) {
            if (error instanceof Error) {
                throw new RAGServiceError(`Error fetching relevant documents: ${error.message}`);
            }
            throw new RAGServiceError('Error fetching relevant documents: Unknown error');
        }
    }

    /**
     * Query LLM with context-aware prompting
     */
    public async queryLLM(query: string, contextDocuments: Document<DocumentMetadata>[]): Promise<string> {
        try {
            // Validate inputs
            if (!query.trim()) {
                throw new RAGServiceError('Query cannot be empty');
            }

            // Construct context string from documents
            const context = this.constructContext(contextDocuments);

            // Create messages for the chat model
            const messages = [
                new SystemMessage("You are a helpful assistant that answers questions based on the provided context."),
                new HumanMessage(`Context: ${context}\n\nQuestion: ${query}`)
            ];

            // Generate response
            const response = await this.llm.call(messages);

            return response.content as string;

        } catch (error: unknown) {
            if (error instanceof Error) {
                throw new RAGServiceError(`Error querying LLM: ${error.message}`);
            }
            throw new RAGServiceError('Error querying LLM: Unknown error');
        }
    }

    /**
     * Construct context string from documents while respecting token limit
     */
    private constructContext(documents: Document<DocumentMetadata>[]): string {
        let context = '';
        
        for (const doc of documents) {
            // Simple token estimation (can be replaced with more accurate counting)
            const estimatedTokens = doc.pageContent.length / 4;
            
            if ((context.length / 4) + estimatedTokens > this.maxContextLength) {
                break;
            }
            
            context += `${doc.pageContent}\n\n`;
        }

        return context.trim();
    }

    /**
     * Create composite prompt combining query and context
     */
    private createPrompt(query: string, context: string): string {
        return `
Context information is below:
--------------------
${context}
--------------------

Given the context information and no other information, answer the following query:
Query: ${query}

Answer:`;
    }
}

export default RAGService; 