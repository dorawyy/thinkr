import { Request, Response } from 'express';
import {
    uploadDocuments,
    deleteDocument,
    getDocuments,
} from '../../controllers/documentController';
import { Result, DocumentDTO } from '../../interfaces';

// Mock Document model
jest.mock('../../db/mongo/models/Document', () => {
    const mockFindOne = jest.fn();
    const mockFindOneAndUpdate = jest.fn();
    const mockDeleteOne = jest.fn();
    const mockFind = jest.fn();

    return {
        __esModule: true,
        findOne: mockFindOne,
        findOneAndUpdate: mockFindOneAndUpdate,
        deleteOne: mockDeleteOne,
        find: mockFind,
        default: {
            findOne: mockFindOne,
            findOneAndUpdate: mockFindOneAndUpdate,
            deleteOne: mockDeleteOne,
            find: mockFind,
        },
    };
});

// Mock AWS S3 and Textract clients
jest.mock('@aws-sdk/client-s3', () => {
    return {
        S3Client: jest.fn().mockImplementation(() => ({
            send: jest.fn().mockResolvedValue({}),
        })),
        PutObjectCommand: jest.fn(),
        DeleteObjectCommand: jest.fn(),
    };
});

jest.mock('@aws-sdk/client-textract', () => {
    const mockSend = jest.fn();

    return {
        TextractClient: jest.fn().mockImplementation(() => ({
            send: mockSend,
        })),
        StartDocumentTextDetectionCommand: jest.fn(),
        GetDocumentTextDetectionCommand: jest.fn(),
    };
});

// Mock StudyService
jest.mock('../../services/studyService', () => {
    return {
        __esModule: true,
        default: {
            generateStudyActivities: jest.fn().mockResolvedValue(undefined),
            deleteStudyActivities: jest.fn().mockResolvedValue(undefined),
        },
    };
});

// Import mocks after they're defined
const Document = require('../../db/mongo/models/Document').default;
const StudyService = require('../../services/studyService').default;
const { S3Client } = require('@aws-sdk/client-s3');
const {
    TextractClient,
    StartDocumentTextDetectionCommand,
    GetDocumentTextDetectionCommand,
} = require('@aws-sdk/client-textract');

// Interface tests for Document Controller
describe('Document Controller', () => {
    let mockRequest: Partial<Request>;
    let mockResponse: Partial<Response>;
    let jsonSpy: jest.Mock;
    let statusSpy: jest.Mock;

    beforeEach(() => {
        // Reset mocks
        jest.clearAllMocks();

        // Setup mock response with spies
        jsonSpy = jest.fn().mockReturnThis();
        statusSpy = jest.fn().mockReturnValue({ json: jsonSpy });

        mockResponse = {
            status: statusSpy,
            json: jsonSpy,
        };

        // Reset environment date for consistent testing
        jest.spyOn(global, 'Date').mockImplementation(() => {
            return {
                toISOString: () => '2023-01-01T12:00:00.000Z',
            } as unknown as Date;
        });
    });

    // Interface POST /document/upload
    describe('uploadDocuments', () => {
        // Input: Valid userId, documentName, and file
        // Expected status code: 200
        // Expected behavior: Document is uploaded to S3 and saved in database
        // Expected output: DocumentDTO with uploaded document details
        it('should upload a document successfully', async () => {
            // Mock data
            const userId = 'user123';
            const documentName = 'Test Document';
            const file = {
                originalname: 'test.pdf',
                buffer: Buffer.from('test file content'),
                mimetype: 'application/pdf',
            };

            const expectedDocumentDTO: Partial<DocumentDTO> = {
                documentId: 'test.pdf',
                activityGenerationComplete: false,
                documentName: documentName,
            };

            // Setup request
            mockRequest = {
                body: { userId, documentName },
                file: file as Express.Multer.File,
            };

            // Setup MongoDB mock
            Document.findOneAndUpdate.mockResolvedValue({
                documentId: 'test.pdf',
                userId: userId,
                name: documentName,
                uploadDate: '2023-01-01 12:00:00',
                s3documentId: `${userId}-test.pdf`,
                activityGenerationComplete: false,
            });

            // Mock Textract for text extraction (called during generateStudyActivities)
            const textractClient = new TextractClient({});
            const jobId = 'text-job-123';

            // Mock the Textract job start
            (textractClient.send as jest.Mock).mockImplementationOnce(() => {
                return Promise.resolve({ JobId: jobId });
            });

            // Mock job status check - SUCCEEDED
            (textractClient.send as jest.Mock).mockImplementationOnce(() => {
                return Promise.resolve({
                    JobStatus: 'SUCCEEDED',
                    Blocks: [
                        { BlockType: 'LINE', Text: 'First line of text' },
                        { BlockType: 'LINE', Text: 'Second line of text' },
                    ],
                });
            });

            // Call controller
            await uploadDocuments(
                mockRequest as Request,
                mockResponse as Response
            );

            // Assertions
            expect(Document.findOneAndUpdate).toHaveBeenCalledWith(
                { documentId: 'test.pdf', userId: userId },
                {
                    name: documentName,
                    userId: userId,
                    s3documentId: `${userId}-test.pdf`,
                    documentId: 'test.pdf',
                    uploadDate: expect.any(String),
                    activityGenerationComplete: false,
                },
                { upsert: true, new: true }
            );

            expect(StudyService.generateStudyActivities).toHaveBeenCalledWith(
                'test.pdf',
                userId
            );
            expect(statusSpy).toHaveBeenCalledWith(200);
            expect(jsonSpy).toHaveBeenCalledWith({
                data: {
                    docs: expect.objectContaining({
                        documentId: expectedDocumentDTO.documentId,
                        activityGenerationComplete:
                            expectedDocumentDTO.activityGenerationComplete,
                        documentName: expectedDocumentDTO.documentName,
                    }),
                },
            });
        });

        // Input: Missing required fields (userId, documentName, or file)
        // Expected status code: 400
        // Expected behavior: Validation error, no uploads occur
        // Expected output: Error message
        it('should return 400 when required fields are missing', async () => {
            // Test missing userId
            mockRequest = {
                body: { documentName: 'Test Document' },
                file: { originalname: 'test.pdf' } as Express.Multer.File,
            };

            await uploadDocuments(
                mockRequest as Request,
                mockResponse as Response
            );

            expect(statusSpy).toHaveBeenCalledWith(400);
            expect(jsonSpy).toHaveBeenCalledWith({
                message: 'Bad Request, missing userId or documentName or file',
            });

            // Test missing documentName
            mockRequest = {
                body: { userId: 'user123' },
                file: { originalname: 'test.pdf' } as Express.Multer.File,
            };

            await uploadDocuments(
                mockRequest as Request,
                mockResponse as Response
            );

            expect(statusSpy).toHaveBeenCalledWith(400);

            // Test missing file
            mockRequest = {
                body: { userId: 'user123', documentName: 'Test Document' },
                file: undefined,
            };

            await uploadDocuments(
                mockRequest as Request,
                mockResponse as Response
            );

            expect(statusSpy).toHaveBeenCalledWith(400);
        });

        // Input: Valid request but database error occurs
        // Expected status code: 500
        // Expected behavior: S3 upload succeeds but database operation fails
        // Expected output: Error message
        // Input: Valid request but error during deletion
        // Expected status code: 500
        // Expected behavior: Deletion operation fails
        // Expected output: Error message
        // Input: Valid userId but database error occurs
        // Expected status code: 500
        // Expected behavior: Document retrieval fails
        // Expected output: Error message
        it('should return 500 when an error occurs', async () => {
            // Mock data
            const userId = 'user123';
            const documentName = 'Test Document';
            const file = {
                originalname: 'test.pdf',
                buffer: Buffer.from('test file content'),
                mimetype: 'application/pdf',
            };

            // Setup request
            mockRequest = {
                body: { userId, documentName },
                file: file as Express.Multer.File,
            };

            // Setup MongoDB mock to throw error
            Document.findOneAndUpdate.mockRejectedValue(
                new Error('Database error')
            );

            // Call controller
            await uploadDocuments(
                mockRequest as Request,
                mockResponse as Response
            );

            // Assertions
            expect(statusSpy).toHaveBeenCalledWith(500);
            expect(jsonSpy).toHaveBeenCalledWith({
                message: 'Failed to upload documents',
            });
        });
    });

    // Interface DELETE /document/delete
    describe('deleteDocument', () => {
        // Input: Valid userId and documentId
        // Expected status code: 200
        // Expected behavior: Document is deleted from S3 and database, study materials removed
        // Expected output: Empty success response
        it('should delete a document successfully', async () => {
            // Mock data
            const userId = 'user123';
            const documentId = 'test.pdf';

            // Setup request
            mockRequest = {
                query: { userId, documentId },
            };

            // Setup mocks
            Document.deleteOne.mockResolvedValue({ deletedCount: 1 });

            // Call controller
            await deleteDocument(
                mockRequest as Request,
                mockResponse as Response
            );

            // Assertions
            expect(Document.deleteOne).toHaveBeenCalledWith({
                s3documentId: `${userId}-${documentId}`,
            });
            expect(StudyService.deleteStudyActivities).toHaveBeenCalledWith(
                documentId,
                userId
            );
            expect(statusSpy).toHaveBeenCalledWith(200);
            expect(jsonSpy).toHaveBeenCalled();
        });

        // Input: Missing required parameters (userId or documentId)
        // Expected status code: 400
        // Expected behavior: Validation error, no deletion occurs
        // Expected output: Error message
        it('should return 400 when required parameters are missing', async () => {
            // Test missing userId
            mockRequest = {
                query: { documentId: 'test.pdf' },
            };

            await deleteDocument(
                mockRequest as Request,
                mockResponse as Response
            );

            expect(statusSpy).toHaveBeenCalledWith(400);
            expect(jsonSpy).toHaveBeenCalledWith({
                message: 'Bad Request, userId and documentId are required',
            });

            // Test missing documentId
            mockRequest = {
                query: { userId: 'user123' },
            };

            await deleteDocument(
                mockRequest as Request,
                mockResponse as Response
            );

            expect(statusSpy).toHaveBeenCalledWith(400);
        });

        it('should return 500 when an error occurs', async () => {
            // Mock data
            const userId = 'user123';
            const documentId = 'test.pdf';

            // Setup request
            mockRequest = {
                query: { userId, documentId },
            };

            // Setup mock to throw error after text extraction
            // First, successfully start a text extraction job
            const textractClient = new TextractClient({});
            const jobId = 'text-job-123';

            // Mock the Textract job start
            (textractClient.send as jest.Mock).mockImplementationOnce(() => {
                return Promise.resolve({ JobId: jobId });
            });

            // Mock successful job status check
            (textractClient.send as jest.Mock).mockImplementationOnce(() => {
                return Promise.resolve({
                    JobStatus: 'SUCCEEDED',
                    Blocks: [
                        {
                            BlockType: 'LINE',
                            Text: 'Extracted text from document',
                        },
                    ],
                });
            });

            // Then the document deletion fails
            Document.deleteOne.mockRejectedValue(new Error('Database error'));

            // Call controller
            await deleteDocument(
                mockRequest as Request,
                mockResponse as Response
            );

            // Assertions
            expect(statusSpy).toHaveBeenCalledWith(500);
            expect(jsonSpy).toHaveBeenCalledWith({
                message: 'Failed to delete documents',
            });
        });
    });

    // Interface GET /document/retrieve
    describe('getDocuments', () => {
        // Input: Valid userId
        // Expected status code: 200
        // Expected behavior: Retrieves all documents for the user
        // Expected output: Array of DocumentDTO objects
        it('should retrieve all documents for a user', async () => {
            // Mock data
            const userId = 'user123';
            const mockDocuments = [
                { documentId: 'doc1.pdf', userId },
                { documentId: 'doc2.pdf', userId },
            ];

            // We don't need this anymore since we're using expect.objectContaining() below

            // Setup request
            mockRequest = {
                query: { userId },
            };

            // Setup mocks
            Document.find.mockResolvedValue(mockDocuments);
            // Mock the getDocument method calls for each document
            Document.findOne
                .mockResolvedValueOnce({
                    documentId: 'doc1.pdf',
                    uploadDate: '2023-01-01',
                    activityGenerationComplete: true,
                    name: 'Document 1',
                })
                .mockResolvedValueOnce({
                    documentId: 'doc2.pdf',
                    uploadDate: '2023-01-02',
                    activityGenerationComplete: false,
                    name: 'Document 2',
                });

            // Call controller
            await getDocuments(
                mockRequest as Request,
                mockResponse as Response
            );

            // Assertions
            expect(Document.find).toHaveBeenCalledWith({ userId });
            expect(Document.findOne).toHaveBeenCalledTimes(2);
            expect(statusSpy).toHaveBeenCalledWith(200);
            expect(jsonSpy).toHaveBeenCalledWith({
                data: {
                    docs: expect.arrayContaining([
                        expect.objectContaining({
                            documentId: 'doc1.pdf',
                            activityGenerationComplete: true,
                            documentName: 'Document 1',
                        }),
                        expect.objectContaining({
                            documentId: 'doc2.pdf',
                            activityGenerationComplete: false,
                            documentName: 'Document 2',
                        }),
                    ]),
                },
            });
        });

        // Input: Valid userId and documentId
        // Expected status code: 200
        // Expected behavior: Retrieves specific document for the user
        // Expected output: Single DocumentDTO object
        it('should retrieve a specific document for a user', async () => {
            // Mock data
            const userId = 'user123';
            const documentId = 'doc1.pdf';

            const mockDocument = {
                documentId,
                uploadDate: '2023-01-01',
                activityGenerationComplete: true,
                name: 'Document 1',
            };

            // We don't need this anymore since we're using expect.objectContaining() below

            // Setup request
            mockRequest = {
                query: { userId, documentId },
            };

            // Setup mocks
            Document.findOne.mockResolvedValue(mockDocument);

            // Call controller
            await getDocuments(
                mockRequest as Request,
                mockResponse as Response
            );

            // Assertions
            expect(Document.findOne).toHaveBeenCalledWith({
                documentId,
                userId,
            });
            expect(statusSpy).toHaveBeenCalledWith(200);
            expect(jsonSpy).toHaveBeenCalledWith({
                data: {
                    docs: expect.objectContaining({
                        documentId,
                        activityGenerationComplete: true,
                        documentName: 'Document 1',
                    }),
                },
            });
        });

        // Input: Missing userId
        // Expected status code: 400
        // Expected behavior: Validation error, no retrieval occurs
        // Expected output: Error message
        it('should return 400 when userId is missing', async () => {
            // Setup request with missing userId
            mockRequest = {
                query: {},
            };

            // Call controller
            await getDocuments(
                mockRequest as Request,
                mockResponse as Response
            );

            // Assertions
            expect(statusSpy).toHaveBeenCalledWith(400);
            expect(jsonSpy).toHaveBeenCalledWith({
                message: 'Bad Request, a userId is required',
            });
        });

        it('should return 500 when an error occurs', async () => {
            // Mock data
            const userId = 'user123';

            // Setup request
            mockRequest = {
                query: { userId },
            };

            // Setup mock to throw error
            Document.find.mockRejectedValue(new Error('Database error'));

            // Call controller
            await getDocuments(
                mockRequest as Request,
                mockResponse as Response
            );

            // Assertions
            expect(statusSpy).toHaveBeenCalledWith(500);
            expect(jsonSpy).toHaveBeenCalledWith({
                message: 'Failed to retrieve documents',
            });
        });
    });
});
