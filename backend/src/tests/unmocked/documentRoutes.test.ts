import { Request, Response } from 'express';
import {
    uploadDocuments,
    deleteDocument,
    getDocuments,
} from '../../controllers/documentController';
import { DocumentDTO } from '../../interfaces';

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

jest.mock('../../services/studyService', () => {
    return {
        __esModule: true,
        default: {
            generateStudyActivities: jest.fn().mockResolvedValue(undefined),
            deleteStudyActivities: jest.fn().mockResolvedValue(undefined),
        },
    };
});

const Document = require('../../db/mongo/models/Document').default;
const StudyService = require('../../services/studyService').default;
const { TextractClient } = require('@aws-sdk/client-textract');

describe('Document Controller', () => {
    let mockRequest: Partial<Request>;
    let mockResponse: Partial<Response>;
    let jsonSpy: jest.Mock;
    let statusSpy: jest.Mock;

    beforeEach(() => {
        jest.clearAllMocks();

        jsonSpy = jest.fn().mockReturnThis();
        statusSpy = jest.fn().mockReturnValue({ json: jsonSpy });

        mockResponse = {
            status: statusSpy,
            json: jsonSpy,
        };

        jest.spyOn(global, 'Date').mockImplementation(() => {
            return {
                toISOString: () => '2023-01-01T12:00:00.000Z',
            } as unknown as Date;
        });
    });

    describe('uploadDocuments', () => {
        // Input: Valid userId, documentName, and file
        // Expected status code: 200
        // Expected behavior: Document is uploaded to S3 and saved in database
        // Expected output: DocumentDTO with uploaded document details
        it('should upload a document successfully', async () => {
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

            mockRequest = {
                body: { userId, documentName },
                file: file as Express.Multer.File,
            };

            Document.findOneAndUpdate.mockResolvedValue({
                documentId: 'test.pdf',
                userId: userId,
                name: documentName,
                uploadDate: '2023-01-01 12:00:00',
                s3documentId: `${userId}-test.pdf`,
                activityGenerationComplete: false,
            });

            const textractClient = new TextractClient({});
            const jobId = 'text-job-123';

            (textractClient.send as jest.Mock).mockImplementationOnce(() => {
                return Promise.resolve({ JobId: jobId });
            });

            (textractClient.send as jest.Mock).mockImplementationOnce(() => {
                return Promise.resolve({
                    JobStatus: 'SUCCEEDED',
                    Blocks: [
                        { BlockType: 'LINE', Text: 'First line of text' },
                        { BlockType: 'LINE', Text: 'Second line of text' },
                    ],
                });
            });

            await uploadDocuments(
                mockRequest as Request,
                mockResponse as Response
            );

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

            mockRequest = {
                body: { userId: 'user123' },
                file: { originalname: 'test.pdf' } as Express.Multer.File,
            };

            await uploadDocuments(
                mockRequest as Request,
                mockResponse as Response
            );

            expect(statusSpy).toHaveBeenCalledWith(400);

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
        it('should return 500 when an error occurs', async () => {
            const userId = 'user123';
            const documentName = 'Test Document';
            const file = {
                originalname: 'test.pdf',
                buffer: Buffer.from('test file content'),
                mimetype: 'application/pdf',
            };

            mockRequest = {
                body: { userId, documentName },
                file: file as Express.Multer.File,
            };

            Document.findOneAndUpdate.mockRejectedValue(
                new Error('Database error')
            );

            await uploadDocuments(
                mockRequest as Request,
                mockResponse as Response
            );

            expect(statusSpy).toHaveBeenCalledWith(500);
            expect(jsonSpy).toHaveBeenCalledWith({
                message: 'Failed to upload documents',
            });
        });
    });

    describe('deleteDocument', () => {
        // Input: Valid userId and documentId
        // Expected status code: 200
        // Expected behavior: Document is deleted from S3 and database, study materials removed
        // Expected output: Empty success response
        it('should delete a document successfully', async () => {
            const userId = 'user123';
            const documentId = 'test.pdf';

            mockRequest = {
                query: { userId, documentId },
            };

            Document.deleteOne.mockResolvedValue({ deletedCount: 1 });

            await deleteDocument(
                mockRequest as Request,
                mockResponse as Response
            );

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

            mockRequest = {
                query: { userId: 'user123' },
            };

            await deleteDocument(
                mockRequest as Request,
                mockResponse as Response
            );

            expect(statusSpy).toHaveBeenCalledWith(400);
        });

        // Input: Valid request but error during deletion
        // Expected status code: 500
        // Expected behavior: Deletion operation fails
        // Expected output: Error message
        it('should return 500 when an error occurs', async () => {
            const userId = 'user123';
            const documentId = 'test.pdf';

            mockRequest = {
                query: { userId, documentId },
            };

            const textractClient = new TextractClient({});
            const jobId = 'text-job-123';

            (textractClient.send as jest.Mock).mockImplementationOnce(() => {
                return Promise.resolve({ JobId: jobId });
            });

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

            Document.deleteOne.mockRejectedValue(new Error('Database error'));

            await deleteDocument(
                mockRequest as Request,
                mockResponse as Response
            );

            expect(statusSpy).toHaveBeenCalledWith(500);
            expect(jsonSpy).toHaveBeenCalledWith({
                message: 'Failed to delete documents',
            });
        });
    });

    describe('getDocuments', () => {
        // Input: Valid userId
        // Expected status code: 200
        // Expected behavior: Retrieves all documents for the user
        // Expected output: Array of DocumentDTO objects
        it('should retrieve all documents for a user', async () => {
            const userId = 'user123';
            const mockDocuments = [
                { documentId: 'doc1.pdf', userId },
                { documentId: 'doc2.pdf', userId },
            ];

            mockRequest = {
                query: { userId },
            };

            Document.find.mockResolvedValue(mockDocuments);
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

            await getDocuments(
                mockRequest as Request,
                mockResponse as Response
            );

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
            const userId = 'user123';
            const documentId = 'doc1.pdf';

            const mockDocument = {
                documentId,
                uploadDate: '2023-01-01',
                activityGenerationComplete: true,
                name: 'Document 1',
            };

            mockRequest = {
                query: { userId, documentId },
            };

            Document.findOne.mockResolvedValue(mockDocument);

            await getDocuments(
                mockRequest as Request,
                mockResponse as Response
            );

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
            mockRequest = {
                query: {},
            };

            await getDocuments(
                mockRequest as Request,
                mockResponse as Response
            );

            expect(statusSpy).toHaveBeenCalledWith(400);
            expect(jsonSpy).toHaveBeenCalledWith({
                message: 'Bad Request, a userId is required',
            });
        });

        // Input: Valid userId but database error occurs
        // Expected status code: 500
        // Expected behavior: Document retrieval fails
        // Expected output: Error message
        it('should return 500 when an error occurs', async () => {
            const userId = 'user123';

            mockRequest = {
                query: { userId },
            };

            Document.find.mockRejectedValue(new Error('Database error'));

            await getDocuments(
                mockRequest as Request,
                mockResponse as Response
            );

            expect(statusSpy).toHaveBeenCalledWith(500);
            expect(jsonSpy).toHaveBeenCalledWith({
                message: 'Failed to retrieve documents',
            });
        });
    });
});
