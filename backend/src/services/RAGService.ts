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
    [key: string]: number | undefined;
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
    private vectorStoreUrl: string;

    constructor(config: RAGServiceConfig) {
        this.embeddings = new OpenAIEmbeddings({
            openAIApiKey: config.openAIApiKey,
        });

        this.llm = new ChatOpenAI({
            openAIApiKey: config.openAIApiKey,
            temperature: config.temperature ?? 0.7,
        });

        this.maxContextLength = config.maxContextLength ?? 4000;
        this.vectorStoreUrl = config.vectorStoreUrl;
    }

    /**
     * Initialize connection to vector store
     */
    public async initVectorStore(collectionName: string): Promise<void> {
        try {
            console.log(
                `Attempting to connect to ChromaDB at ${this.vectorStoreUrl}`
            );
            this.vectorStore = await Chroma.fromExistingCollection(
                this.embeddings,
                {
                    collectionName: collectionName,
                    url: this.vectorStoreUrl,
                }
            );
            console.log(
                `Successfully connected to ChromaDB collection: ${collectionName}`
            );
        } catch (error: unknown) {
            console.error('Vector store initialization error:', error);

            try {
                console.log(
                    `Attempting to create new collection: ${collectionName}`
                );
                this.vectorStore = await Chroma.fromTexts(
                    ['Initial document for testing RAG capabilities.'],
                    { source: 'initialization' },
                    this.embeddings,
                    {
                        collectionName: collectionName,
                        url: this.vectorStoreUrl,
                    }
                );
                console.log(
                    `Successfully created new ChromaDB collection: ${collectionName}`
                );
            } catch (innerError) {
                console.error(
                    'Failed to create ChromaDB collection:',
                    innerError
                );
                throw new RAGServiceError(
                    `ChromaDB connection failed. Please ensure ChromaDB is running at ${this.vectorStoreUrl}`
                );
            }
        }
    }

    /**
     * Insert a document into a vector db collection
     */
    public async insertDocument(
        userId: string,
        documentId: string,
        text: string
    ): Promise<string> {
        try {
            // Create a collection name based on userId
            const collectionName = `user_${userId}`;

            await this.initVectorStore(collectionName);

            // Split text into chunks if it's too large
            const textChunks = this.splitTextIntoChunks(text, 1000);

            // Create documents with metadata including documentId
            const docs = textChunks.map((chunk, index) => ({
                pageContent: chunk,
                metadata: {
                    userId,
                    documentId,
                    chunkIndex: index,
                    source: documentId,
                },
            }));

            // Generate unique IDs for each chunk
            const ids = {
                ids: textChunks.map(
                    (_, index) => `${documentId}_chunk_${index}`
                ),
            };

            await this.vectorStore?.addDocuments(docs, ids);

            console.log(
                `Text uploaded to Chroma DB in collection ${collectionName} for document: ${documentId}`
            );
            return documentId;
        } catch (error) {
            console.error('Error uploading text to Chroma DB:', error);
            throw new Error('Failed to upload text to Chroma DB');
        }
    }

    /**
     * Split text into manageable chunks
     */
    private splitTextIntoChunks(text: string, chunkSize: number): string[] {
        const chunks: string[] = [];
        let currentChunk = '';

        // Split by paragraphs first
        const paragraphs = text.split(/\n\s*\n/);

        for (const paragraph of paragraphs) {
            // If adding this paragraph would exceed chunk size, save current chunk and start a new one
            if (
                currentChunk.length + paragraph.length > chunkSize &&
                currentChunk.length > 0
            ) {
                chunks.push(currentChunk);
                currentChunk = paragraph;
            } else {
                currentChunk += (currentChunk ? '\n\n' : '') + paragraph;
            }
        }

        // Add the last chunk if it's not empty
        if (currentChunk) {
            chunks.push(currentChunk);
        }

        return chunks;
    }

    /**
     * Fetch relevant documents based on query similarity
     */
    public async fetchRelevantDocumentsFromQuery(
        query: string,
        userId: string,
        documentId?: string
    ): Promise<Document[]> {
        try {
            // Create collection name based on userId
            const collectionName = `user_${userId}`;

            await this.initVectorStore(collectionName);

            // Set up filter if documentId is provided
            const filter = documentId ? { documentId: documentId } : undefined;

            // Fetch relevant documents with optional filter
            const docs = await this.vectorStore?.similaritySearch(
                query,
                5, // Number of documents to retrieve
                filter
            );

            return docs ?? ([] as Document<DocumentMetadata>[]);
        } catch (error) {
            console.error('Error fetching relevant documents:', error);
            throw new Error('Failed to fetch relevant documents');
        }
    }

    /**
     * Deletes documents in a vector DB collection
     */
    public async deleteDocuments(
        documentIds: string[],
        userId: string
    ): Promise<void> {
        try {
            const collectionName = `user_${userId}`;
            await this.initVectorStore(collectionName);

            // For each document ID, we need to find all chunks
            for (const documentId of documentIds) {
                // Get all chunks for this document
                const results = await this.vectorStore?.collection?.get({
                    where: { documentId: { $eq: documentId } },
                });

                if (results?.ids && results.ids.length > 0) {
                    // Delete all chunks for this document
                    await this.vectorStore?.collection?.delete({
                        ids: results.ids,
                    });

                    console.log(
                        `Deleted embeddings in Chroma DB collection ${collectionName} for document: ${documentId}`
                    );
                }
            }

            return;
        } catch (error) {
            console.error('Error deleting embeddings from Chroma DB:', error);
            throw new Error('Failed to delete embeddings from Chroma DB');
        }
    }

    /**
     * Fetch documents from vectorDB collection based on a documentId provided
     */
    public async fetchDocumentsFromVectorDB(
        documentId: string,
        collectionName: string
    ): Promise<string[]> {
        try {
            await this.initVectorStore(`user_${collectionName}`);

            const results = await this.vectorStore?.collection?.get({
                where: { documentId: { $eq: documentId } },
            });

            const documents = results?.documents as string[];
            return documents;
        } catch (error) {
            console.error('Error fetching documents from ChromaDB:', error);
            throw new Error('Failed to fetch documents from ChromaDB');
        }
    }

    /**
     * Query LLM with context-aware prompting
     */
    public async queryLLM(
        query: string,
        contextDocuments: Document<DocumentMetadata>[]
    ): Promise<string> {
        try {
            if (!query.trim()) {
                throw new RAGServiceError('Query cannot be empty');
            }

            const context = this.constructContext(contextDocuments);

            // Create messages for the chat model
            const messages = [
                new SystemMessage(
                    'You are a helpful assistant that answers questions based on the provided context.'
                ),
                new HumanMessage(`Context: ${context}\n\nQuestion: ${query}`),
            ];

            // Generate response
            const response = await this.llm.call(messages);

            return response.content as string;
        } catch (error: unknown) {
            if (error instanceof Error) {
                throw new RAGServiceError(
                    `Error querying LLM: ${error.message}`
                );
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

            if (context.length / 4 + estimatedTokens > this.maxContextLength) {
                break;
            }

            context += `${doc.pageContent}\n\n`;
        }

        return context.trim();
    }

    /**
     * Get relevant context from user documents based on a query
     */
    public async getRelevantContext(
        query: string,
        userId: string
    ): Promise<string> {
        try {
            await this.initVectorStore(`user_${userId}`);

            if (!this.vectorStore) {
                return 'No documents found to provide context.';
            }

            const results = (await this.vectorStore.similaritySearch(query, 5))
                .map((doc: { pageContent: string }): string => doc.pageContent)
                .join('\n\n');

            return results;
        } catch (error) {
            console.error('Error getting context from RAG:', error);
            return 'Unable to retrieve context from your documents.';
        }
    }
}

export default RAGService;
