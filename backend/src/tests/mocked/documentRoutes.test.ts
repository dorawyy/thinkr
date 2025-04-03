import request from 'supertest';
import express from 'express';
import { Result } from '../../interfaces';

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

const app = express();
app.use(express.json());
app.use('/', documentRouter);

describe('Document Routes (Mocked)', () => {
    afterEach(() => {
        jest.clearAllMocks();
    });

    describe('MOCKED POST /document/upload', () => {
        // Input: Missing userId, documentName, or file
        // Expected status code: 400
        // Expected behavior: validation error, no service calls
        // Expected output: error message
        it('should return 400 when required fields are missing', async () => {
            await request(app)
                .post('/upload')
                .send({
                    documentName: 'Test Document',
                })
                .expect(400);

            await request(app)
                .post('/upload')
                .send({
                    userId: 'user123',
                })
                .expect(400);

            expect(mockUploadDocument).not.toHaveBeenCalled();
            expect(mockGenerateStudyActivities).not.toHaveBeenCalled();
        });

        // Input: Valid request but service throws error
        // Expected status code: 500
        // Expected behavior: Upload attempt is made but service fails
        // Expected output: error message
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

    describe('MOCKED DELETE /document/delete', () => {
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

    describe('MOCKED GET /document/retrieve', () => {
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
