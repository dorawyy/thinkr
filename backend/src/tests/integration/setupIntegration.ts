import express from 'express';
import { Express } from 'express';

// Create a central express app for integration testing
const testApp = express();
testApp.use(express.json());
testApp.use(express.urlencoded({ extended: true }));

// Mock MongoDB - export the mock functions directly
export const mockDocument = {
  create: jest.fn(),
  findOne: jest.fn(),
  find: jest.fn(),
  deleteOne: jest.fn(),
  countDocuments: jest.fn(),
  exec: jest.fn()
};

export const mockUser = {
  create: jest.fn(),
  findOne: jest.fn(),
  updateOne: jest.fn(),
  countDocuments: jest.fn(),
  exec: jest.fn()
};

export const mockFlashcardSet = {
  create: jest.fn(),
  findOne: jest.fn(),
  find: jest.fn(),
  exec: jest.fn()
};

export const mockQuizSet = {
  create: jest.fn(),
  findOne: jest.fn(),
  find: jest.fn(),
  exec: jest.fn()
};

export const mockChatSession = {
  create: jest.fn(),
  findOne: jest.fn(),
  findOneAndUpdate: jest.fn(),
  exec: jest.fn()
};

// Aggressively mock mongoose
jest.mock('mongoose', () => {
  const mConnect = jest.fn().mockResolvedValue({});
  const mSchema = jest.fn().mockImplementation(() => ({
    pre: jest.fn().mockReturnThis(),
    index: jest.fn().mockReturnThis()
  }));
  
  return {
    connect: mConnect,
    Schema: mSchema,
    model: jest.fn(),
    Connection: jest.fn(),
    set: jest.fn(),
  };
});

// Mock MongoDB models
jest.mock('../../db/mongo/models/Document', () => ({
  __esModule: true,
  default: mockDocument,
}));

jest.mock('../../db/mongo/models/User', () => ({
  __esModule: true,
  default: mockUser,
}));

jest.mock('../../db/mongo/models/FlashcardSet', () => ({
  __esModule: true,
  default: mockFlashcardSet,
}));

jest.mock('../../db/mongo/models/QuizSet', () => ({
  __esModule: true,
  default: mockQuizSet,
}));

jest.mock('../../db/mongo/models/Chat', () => ({
  __esModule: true,
  default: mockChatSession,
}));

// Mock db connection
jest.mock('../../db/mongo/connection', () => ({
  connectDB: jest.fn().mockResolvedValue(true)
}));

// Mock external AWS services
jest.mock('@aws-sdk/client-s3', () => {
  const mockSend = jest.fn().mockResolvedValue({});
  
  return {
    S3Client: jest.fn().mockImplementation(() => ({
      send: mockSend,
    })),
    PutObjectCommand: jest.fn(),
    DeleteObjectCommand: jest.fn(),
    GetObjectCommand: jest.fn(),
  };
});

jest.mock('@aws-sdk/client-textract', () => {
  return {
    TextractClient: jest.fn().mockImplementation(() => ({
      send: jest.fn().mockImplementation((command) => {
        if (command.constructor.name === 'StartDocumentTextDetectionCommand') {
          return Promise.resolve({ JobId: 'mock-job-id' });
        } else if (command.constructor.name === 'GetDocumentTextDetectionCommand') {
          return Promise.resolve({
            JobStatus: 'SUCCEEDED',
            Blocks: [
              { BlockType: 'LINE', Text: 'Mock text line 1' },
              { BlockType: 'LINE', Text: 'Mock text line 2' },
            ],
          });
        }
        return Promise.resolve({});
      }),
    })),
    StartDocumentTextDetectionCommand: jest.fn(),
    GetDocumentTextDetectionCommand: jest.fn(),
  };
});

// Mock OpenAI and LangChain
jest.mock('@langchain/openai', () => {
  return {
    OpenAIEmbeddings: jest.fn().mockImplementation(() => ({
      embedDocuments: jest.fn().mockResolvedValue([
        [0.1, 0.2, 0.3], // Mock embedding vector 1
        [0.2, 0.3, 0.4], // Mock embedding vector 2
      ]),
      embedQuery: jest.fn().mockResolvedValue([0.1, 0.2, 0.3]),
    })),
    ChatOpenAI: jest.fn().mockImplementation(() => ({
      invoke: jest.fn().mockResolvedValue({ content: 'Mock AI response' }),
      call: jest.fn().mockResolvedValue({ content: 'Mock AI response' }),
    })),
  };
});

jest.mock('@langchain/community/vectorstores/chroma', () => {
  return {
    Chroma: {
      fromExistingCollection: jest.fn().mockResolvedValue({
        similaritySearch: jest.fn().mockResolvedValue([
          { pageContent: 'Mock document content 1', metadata: {} },
          { pageContent: 'Mock document content 2', metadata: {} },
        ]),
        addDocuments: jest.fn().mockResolvedValue({}),
        collection: {
          get: jest.fn().mockResolvedValue({ 
            ids: ['id1', 'id2'], 
            documents: ['Mock document 1', 'Mock document 2'] 
          }),
          delete: jest.fn().mockResolvedValue({}),
        },
      }),
      fromTexts: jest.fn().mockResolvedValue({
        similaritySearch: jest.fn().mockResolvedValue([
          { pageContent: 'Mock document content 1', metadata: {} },
          { pageContent: 'Mock document content 2', metadata: {} },
        ]),
        addDocuments: jest.fn().mockResolvedValue({}),
        collection: {
          get: jest.fn().mockResolvedValue({ 
            ids: ['id1', 'id2'], 
            documents: ['Mock document 1', 'Mock document 2'] 
          }),
          delete: jest.fn().mockResolvedValue({}),
        },
      }),
    },
  };
});

// Mock multer to simulate file uploads
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

// Force Jest to return mock values for all environment variables
process.env = {
  ...process.env,
  MONGODB_URI: 'mongodb://localhost:27017/test-db',
  AWS_REGION: 'us-west-2',
  AWS_BUCKET: 'test-bucket',
  PORT: '3000',
  // Add any other environment variables needed for tests
};

// Reset all mocks after each test
afterEach(() => {
  jest.clearAllMocks();
});

export { testApp }; 