import {
    S3Client,
    PutObjectCommand,
    DeleteObjectCommand,
} from '@aws-sdk/client-s3';
import dotenv from 'dotenv';
import Document from '../db/mongo/models/Document';
import { DocumentDTO } from '../interfaces';
import {
    TextractClient,
    StartDocumentTextDetectionCommand,
    GetDocumentTextDetectionCommand,
} from '@aws-sdk/client-textract';
import { getEnvVariable } from '../config/env';

dotenv.config();

class DocumentService {
    private s3Client: S3Client;
    private bucketName: string;
    private date: Date;
    private textractClient: TextractClient;

    constructor() {
        this.s3Client = new S3Client({
            region: getEnvVariable('AWS_REGION'),
            credentials: {
                accessKeyId: getEnvVariable('AWS_ACCESS_KEY_ID'),
                secretAccessKey: getEnvVariable('AWS_SECRET_ACCESS_KEY'),
            },
        });
        this.bucketName = getEnvVariable('S3_BUCKET_NAME');
        this.date = new Date();
        this.textractClient = new TextractClient({
            region: getEnvVariable('AWS_REGION'),
            credentials: {
                accessKeyId: getEnvVariable('AWS_ACCESS_KEY_ID'),
                secretAccessKey: getEnvVariable('AWS_SECRET_ACCESS_KEY'),
            },
        });
    }

    /**
     * Uploads a file to s3 and mongodb
     */
    public async uploadDocument(
        file: Express.Multer.File,
        userId: string,
        name: string,
        isPublic = false
    ): Promise<DocumentDTO> {
        const key = `${userId}-${file.originalname}`;
        const documentId = file.originalname;

        const params = {
            Bucket: this.bucketName,
            Key: key,
            Body: file.buffer,
            ContentType: file.mimetype,
        };

        // s3
        const command = new PutObjectCommand(params);
        await this.s3Client.send(command);
        const dateFormatted = this.date
            .toISOString()
            .replace(/T/, ' ')
            .replace(/\..+/, '');

        // mongodb
        await Document.findOneAndUpdate(
            { documentId: documentId, userId: userId },
            {
                name: name,
                userId: userId,
                s3documentId: key,
                documentId: documentId,
                uploadDate: dateFormatted,
                activityGenerationComplete: false,
                public: isPublic,
            },
            { upsert: true, new: true }
        );

        return {
            documentId: documentId,
            uploadTime: dateFormatted,
            activityGenerationComplete: false,
            documentName: name,
            public: isPublic,
        } as DocumentDTO;
    }

    /**
     * Deletes a document on s3 and mongodb
     *
     */
    public async deleteDocument(key: string): Promise<void> {
        const params = {
            Bucket: this.bucketName,
            Key: key,
        };

        // s3
        const command = new DeleteObjectCommand(params);
        await this.s3Client.send(command);

        // mongodb
        await Document.deleteOne({ s3documentId: key });
        return;
    }

    /**
     * Retrieves a document from s3
     *
     */
    public async getDocument(
        key: string,
        userId: string
    ): Promise<DocumentDTO> {
        const doc = await Document.findOne({
            documentId: key,
            userId: userId,
        });

        return {
            documentId: doc?.documentId,
            uploadTime: doc?.uploadDate,
            activityGenerationComplete: doc?.activityGenerationComplete,
            documentName: doc?.name,
            public: doc?.public,
        } as DocumentDTO;
    }

    /**
     * Retrieves all of users documents
     *
     */
    public async getDocuments(userId: string): Promise<DocumentDTO[]> {
        const allKeys = (await Document.find({ userId: userId })).map(
            (doc): string => doc.documentId
        );
        const documents = await Promise.all(
            allKeys.map((key: string) => this.getDocument(key, userId))
        );

        return documents;
    }

    /**
     * Extracts text from a file using AWS Textract.
     */
    public async extractTextFromFile(s3FilePath: string): Promise<string> {
        const params = {
            DocumentLocation: {
                S3Object: {
                    Bucket: this.bucketName,
                    Name: s3FilePath,
                },
            },
        };

        try {
            const command = new StartDocumentTextDetectionCommand(params);
            const response = await this.textractClient.send(command);
            const jobId = response.JobId;
            let jobStatus: string | undefined;
            let extractedText = '';

            if (!jobId) {
                throw new Error('Failed to start Textract job');
            }

            do {
                const describeResponse = await this.textractClient.send(
                    new GetDocumentTextDetectionCommand({ JobId: jobId })
                );

                jobStatus = describeResponse.JobStatus;

                if (jobStatus === 'SUCCEEDED' && describeResponse.Blocks) {
                    for (const block of describeResponse.Blocks) {
                        if (block.BlockType === 'LINE' && block.Text) {
                            extractedText += block.Text + '\n';
                        }
                    }
                } else if (jobStatus === 'FAILED') {
                    throw new Error('Textract job failed');
                }

                await new Promise((resolve) => setTimeout(resolve, 1000));
            } while (jobStatus === 'IN_PROGRESS');

            return extractedText.trim();
        } catch (error) {
            console.error('Error extracting text from PDF:', error);
            throw new Error('Failed to extract text from PDF');
        }
    }
}

export default new DocumentService();
