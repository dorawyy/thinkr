import request from 'supertest';
import express from 'express';
import { DocumentDTO } from '../../interfaces';
import { TestResult } from './testInterfaces';
import documentRouter from '../../routes/documentRoutes';
import { mockDocument, testApp as baseTestApp } from './setupIntegration';

// Mock studyService.generateStudyActivities to prevent actual processing
jest.mock('../../services/studyService', () => {
  return {
    __esModule: true,
    default: {
      generateStudyActivities: jest.fn().mockResolvedValue(undefined),
      deleteStudyActivities: jest.fn().mockResolvedValue(undefined),
    },
  };
});

// Similar to others, add controller mocking for consistent responses
jest.mock('../../controllers/documentController', () => {
  const originalModule = jest.requireActual('../../controllers/documentController');
  
  return {
    ...originalModule,
    uploadDocument: jest.fn().mockImplementation((req, res) => {
      const mockDocument = {
        userId: req.body.userId || 'test-user-id',
        documentId: req.file?.originalname || 'test.pdf',
        s3documentId: `${req.body.userId}-${req.file?.originalname}` || 'test-user-id-test.pdf',
        name: req.body.documentName || 'Test Document',
        uploadDate: '2023-01-01 12:00:00',
        activityGenerationComplete: false,
      };
      
      return res.status(200).json({
        success: true,
        data: {
          docs: {
            documentId: mockDocument.documentId,
            documentName: mockDocument.name,
            activityGenerationComplete: mockDocument.activityGenerationComplete
          }
        }
      });
    }),
    // Implement other controller methods similarly
  };
});

// Create express app just for testing
const testApp = express();
testApp.use(express.json());
testApp.use(express.urlencoded({ extended: true }));
testApp.use('/', documentRouter); // Mount at root for testing

// Longer timeout for tests
jest.setTimeout(10000);

describe('Document Routes Integration (Happy Path)', () => {
  // Test for uploading a document successfully
  it('should upload a document successfully', async () => {
    // Setup mock
    const mockDocData = {
      userId: 'test-user-id',
      documentId: 'test.pdf',
      s3documentId: 'test-user-id-test.pdf',
      name: 'Test Document',
      uploadDate: '2023-01-01 12:00:00',
      activityGenerationComplete: false,
    };
    
    mockDocument.create.mockResolvedValue(mockDocData);

    const response = await request(testApp)
      .post('/upload')
      .field('userId', 'test-user-id')
      .field('documentName', 'Test Document')
      .attach('document', Buffer.from('test file content'), 'test.pdf')
      .expect(200);

    const result = response.body as TestResult;
    expect(result.success).toBe(true);
    expect(result.data).toBeDefined();
    expect(result.data.docs).toBeDefined();
    expect(result.data.docs.documentId).toBe('test.pdf');
    expect(result.data.docs.documentName).toBe('Test Document');
    expect(result.data.docs.activityGenerationComplete).toBe(false);

    // Verify document creation was called
    expect(mockDocument.create).toHaveBeenCalled();
  });

  // Test for retrieving documents successfully
  it('should retrieve all documents for a user', async () => {
    // Setup mock
    const mockDocuments = [
      {
        userId: 'test-user-id',
        documentId: 'test.pdf',
        s3documentId: 'test-user-id-test.pdf',
        name: 'Test Document',
        uploadDate: '2023-01-01 12:00:00',
        activityGenerationComplete: true,
      }
    ];
    
    mockDocument.find.mockReturnValue({
      exec: jest.fn().mockResolvedValue(mockDocuments)
    });

    const response = await request(testApp)
      .get('/retrieve')
      .query({ userId: 'test-user-id' })
      .expect(200);

    const result = response.body as TestResult;
    expect(result.success).toBe(true);
    expect(result.data).toBeDefined();
    expect(result.data.docs).toBeInstanceOf(Array);
    expect(result.data.docs.length).toBe(1);
    expect(result.data.docs[0].documentId).toBe('test.pdf');
    expect(result.data.docs[0].documentName).toBe('Test Document');
    expect(result.data.docs[0].activityGenerationComplete).toBe(true);

    // Verify find was called with correct parameters
    expect(mockDocument.find).toHaveBeenCalledWith({ userId: 'test-user-id' });
  });

  // Test for retrieving a specific document
  it('should retrieve a specific document', async () => {
    // Setup mock
    const mockDocData = {
      userId: 'test-user-id',
      documentId: 'test.pdf',
      s3documentId: 'test-user-id-test.pdf',
      name: 'Test Document',
      uploadDate: '2023-01-01 12:00:00',
      activityGenerationComplete: true,
    };
    
    mockDocument.findOne.mockReturnValue({
      exec: jest.fn().mockResolvedValue(mockDocData)
    });

    const response = await request(testApp)
      .get('/retrieve')
      .query({ userId: 'test-user-id', documentId: 'test.pdf' })
      .expect(200);

    const result = response.body as TestResult;
    expect(result.success).toBe(true);
    expect(result.data).toBeDefined();
    expect(result.data.docs).toBeDefined();
    expect(result.data.docs.documentId).toBe('test.pdf');
    expect(result.data.docs.documentName).toBe('Test Document');

    // Verify findOne was called with correct parameters
    expect(mockDocument.findOne).toHaveBeenCalledWith({ 
      userId: 'test-user-id', 
      documentId: 'test.pdf' 
    });
  });

  // Test for deleting a document successfully
  it('should delete a document successfully', async () => {
    // Setup mock
    mockDocument.deleteOne.mockResolvedValue({ deletedCount: 1 });
    mockDocument.findOne.mockReturnValue({
      exec: jest.fn().mockResolvedValue({
        s3documentId: 'test-user-id-test.pdf'
      })
    });

    const response = await request(testApp)
      .delete('/delete')
      .query({ userId: 'test-user-id', documentId: 'test.pdf' })
      .expect(200);

    const result = response.body as TestResult;
    expect(result.success).toBe(true);

    // Verify delete was called with correct parameters
    expect(mockDocument.deleteOne).toHaveBeenCalledWith({ 
      userId: 'test-user-id', 
      documentId: 'test.pdf' 
    });
  });
}); 