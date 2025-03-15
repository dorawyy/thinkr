import request from 'supertest';
import express from 'express';
import { Result, DocumentDTO } from '../../interfaces';

// Mock DocumentService
jest.mock('../../services/documentService', () => {
    const mockUploadDocument = jest.fn();
    const mockDeleteDocument = jest.fn();
    const mockGetDocument = jest.fn();
    const mockGetDocuments = jest.fn();

    return {
        __esModule: true,
        mockUploadDocument,
        mockDeleteDocument,
        mockGetDocument,
        mockGetDocuments,
        default: {
            uploadDocument: mockUploadDocument,
            deleteDocument: mockDeleteDocument,
            getDocument: mockGetDocument,
            getDocuments: mockGetDocuments,
        },
    };
});

// Mock StudyService
jest.mock('../../services/studyService', () => {
    const mockGenerateStudyActivities = jest.fn();
    const mockDeleteStudyActivities = jest.fn();

    return {
        __esModule: true,
        mockGenerateStudyActivities,
        mockDeleteStudyActivities,
        default: {
            generateStudyActivities: mockGenerateStudyActivities,
            deleteStudyActivities: mockDeleteStudyActivities,
        },
    };
});

// Mock multer
jest.mock('multer', () => {
    return () => ({
        single: () => (req: any, res: any, next: any) => {
            req.file = {
                buffer: Buffer.from('test file content'),
                originalname: 'test.pdf',
                mimetype: 'application/pdf',
            };
            next();
        },
    });
});

// Import after mocks are defined
import documentRouter from '../../routes/documentRoutes';
const {
    mockUploadDocument,
    mockDeleteDocument,
    mockGetDocument,
    mockGetDocuments,
} = require('../../services/documentService');
const {
    mockGenerateStudyActivities,
    mockDeleteStudyActivities,
} = require('../../services/studyService');

// Create express app just for testing
const app = express();
app.use(express.json());
app.use('/', documentRouter); // Mount at root for testing

describe('Document Routes (Mocked)', () => {
    afterEach(() => {
        jest.clearAllMocks();
    });

    // Upload Document Tests
    describe('POST /upload', () => {
        it('should upload a document successfully', async () => {
            const mockDoc = {
                documentId: 'test.pdf',
                uploadTime: '2023-01-01 12:00:00',
                activityGenerationComplete: false,
                documentName: 'Test Document',
            };

            mockUploadDocument.mockResolvedValue(mockDoc);
            mockGenerateStudyActivities.mockResolvedValue(undefined);

            const response = await request(app)
                .post('/upload')
                .send({
                    userId: 'user123',
                    documentName: 'Test Document',
                })
                .expect(200);

            const result = response.body as Result;
            expect(result.data).toBeDefined();
            expect(result.data.docs).toEqual(mockDoc);

            expect(mockUploadDocument).toHaveBeenCalledWith(
                expect.objectContaining({
                    originalname: 'test.pdf',
                    buffer: expect.any(Buffer),
                }),
                'user123',
                'Test Document'
            );
            expect(mockGenerateStudyActivities).toHaveBeenCalledWith(
                'test.pdf',
                'user123'
            );
        });

        // Input: Missing userId, documentName, or file
        // Expected status code: 400
        // Expected behavior: validation error, no service calls
        // Expected output: error message
        it('should return 400 when required fields are missing', async () => {
            await request(app)
                .post('/upload')
                .send({
                    // No userId
                    documentName: 'Test Document',
                })
                .expect(400);

            // For missing documentName
            await request(app)
                .post('/upload')
                .send({
                    userId: 'user123',
                    // No documentName
                })
                .expect(400);

            // We can't easily test missing file because multer mock always adds a file

            expect(mockUploadDocument).not.toHaveBeenCalled();
            expect(mockGenerateStudyActivities).not.toHaveBeenCalled();
        });

        it('should return 500 when service throws error', async () => {
            mockUploadDocument.mockRejectedValue(new Error('Service error'));

            const response = await request(app)
                .post('/upload')
                .send({
                    userId: 'user123',
                    documentName: 'Test Document',
                })
                .expect(500);

            const result = response.body as Result;
            expect(result.message).toBe('Failed to upload documents');
        });
    });

    // Delete Document Tests
    describe('DELETE /delete', () => {
        // Input: Valid userId and documentId
        // Expected status code: 200
        // Expected behavior: DocumentService.deleteDocument called, StudyService.deleteStudyActivities called
        // Expected output: empty response
        it('should delete a document successfully', async () => {
            mockDeleteDocument.mockResolvedValue(undefined);
            mockDeleteStudyActivities.mockResolvedValue(undefined);

            await request(app)
                .delete('/delete')
                .query({ userId: 'user123', documentId: 'doc1' })
                .expect(200);

            expect(mockDeleteDocument).toHaveBeenCalledWith('user123-doc1');
            expect(mockDeleteStudyActivities).toHaveBeenCalledWith(
                'doc1',
                'user123'
            );
        });

        // Input: Missing userId or documentId
        // Expected status code: 400
        // Expected behavior: validation error, no service calls
        // Expected output: error message
        it('should return 400 when required fields are missing', async () => {
            await request(app)
                .delete('/delete')
                .query({ documentId: 'doc1' })
                .expect(400);

            await request(app)
                .delete('/delete')
                .query({ userId: 'user123' })
                .expect(400);

            expect(mockDeleteDocument).not.toHaveBeenCalled();
            expect(mockDeleteStudyActivities).not.toHaveBeenCalled();
        });

        // Input: Valid request but service throws error
        // Expected status code: 500
        // Expected behavior: DocumentService.deleteDocument called but throws error
        // Expected output: error message
        it('should return 500 when service throws error', async () => {
            mockDeleteDocument.mockRejectedValue(new Error('Service error'));

            const response = await request(app)
                .delete('/delete')
                .query({ userId: 'user123', documentId: 'doc1' })
                .expect(500);

            const result = response.body as Result;
            expect(result.message).toBe('Failed to delete documents');
        });
    });

    // Get Documents Tests
    describe('GET /retrieve', () => {
        // Input: Valid userId
        // Expected status code: 200
        // Expected behavior: DocumentService.getDocuments called
        // Expected output: array of documents
        it('should retrieve all documents for a user', async () => {
            const mockDocs = [
                {
                    documentId: 'doc1',
                    uploadTime: '2023-01-01 12:00:00',
                    activityGenerationComplete: true,
                    documentName: 'Document 1',
                },
                {
                    documentId: 'doc2',
                    uploadTime: '2023-01-02 12:00:00',
                    activityGenerationComplete: false,
                    documentName: 'Document 2',
                },
            ];

            mockGetDocuments.mockResolvedValue(mockDocs);

            const response = await request(app)
                .get('/retrieve')
                .query({ userId: 'user123' })
                .expect(200);

            const result = response.body as Result;
            expect(result.data).toBeDefined();
            expect(result.data.docs).toEqual(mockDocs);

            expect(mockGetDocuments).toHaveBeenCalledWith('user123');
        });

        // Input: Valid userId and documentId
        // Expected status code: 200
        // Expected behavior: DocumentService.getDocument called
        // Expected output: single document
        it('should retrieve a specific document', async () => {
            const mockDoc = {
                documentId: 'doc1',
                uploadTime: '2023-01-01 12:00:00',
                activityGenerationComplete: true,
                documentName: 'Document 1',
            };

            mockGetDocument.mockResolvedValue(mockDoc);

            const response = await request(app)
                .get('/retrieve')
                .query({ userId: 'user123', documentId: 'doc1' })
                .expect(200);

            const result = response.body as Result;
            expect(result.data).toBeDefined();
            expect(result.data.docs).toEqual(mockDoc);

            expect(mockGetDocument).toHaveBeenCalledWith('doc1', 'user123');
        });

        // Input: Missing userId
        // Expected status code: 400
        // Expected behavior: validation error, no service calls
        // Expected output: error message
        it('should return 400 when userId is missing', async () => {
            await request(app).get('/retrieve').expect(400);

            expect(mockGetDocuments).not.toHaveBeenCalled();
            expect(mockGetDocument).not.toHaveBeenCalled();
        });

        // Input: Valid request but service throws error
        // Expected status code: 500
        // Expected behavior: DocumentService method called but throws error
        // Expected output: error message
        it('should return 500 when service throws error', async () => {
            mockGetDocuments.mockRejectedValue(new Error('Service error'));

            const response = await request(app)
                .get('/retrieve')
                .query({ userId: 'user123' })
                .expect(500);

            const result = response.body as Result;
            expect(result.message).toBe('Failed to retrieve documents');
        });
    });
});
