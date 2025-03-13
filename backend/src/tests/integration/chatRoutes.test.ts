import request from 'supertest';
import express from 'express';
import { ChatSessionDTO, ChatMessage } from '../../interfaces';
import { TestResult } from './testInterfaces';
import chatRouter from '../../routes/chatRoutes';
import { mockChatSession, testApp as baseTestApp } from './setupIntegration';

// Create express app just for testing
const testApp = express();
testApp.use(express.json());
testApp.use('/', chatRouter); // Mount at root for testing

// Longer timeout for tests
jest.setTimeout(30000); // Increase timeout

// Override controller responses for testing
jest.mock('../../controllers/chatController', () => {
  const originalModule = jest.requireActual('../../controllers/chatController');
  
  return {
    ...originalModule,
    getOrCreateChatSession: jest.fn().mockImplementation((req, res) => {
      return res.status(200).json({
        success: true,
        data: {
          chat: {
            sessionId: 'mock-session-id',
            googleId: req.query.userId,
            messages: [
              {
                role: 'system',
                content: 'You are a helpful assistant.',
                timestamp: new Date().toISOString(),
              }
            ],
            metadata: { type: 'general' },
          }
        }
      });
    }),
    sendMessage: jest.fn().mockImplementation((req, res) => {
      return res.status(200).json({
        success: true,
        data: {
          response: {
            role: 'assistant',
            content: 'This is a mock response to: ' + req.body.message,
            timestamp: new Date().toISOString(),
          }
        }
      });
    }),
    clearChatHistory: jest.fn().mockImplementation((req, res) => {
      return res.status(200).json({
        message: 'Chat history cleared successfully'
      });
    })
  };
});

describe('Chat Routes Integration (Happy Path)', () => {
  beforeEach(() => {
    // Reset mocks
    jest.clearAllMocks();
  });

  // Test for getting or creating a chat session
  it('should get or create a chat session for a user', async () => {
    const response = await request(testApp)
      .get('/')
      .query({ userId: 'test-user-id' })
      .expect(200);

    const result = response.body as TestResult;
    expect(result.success).toBe(true);
    expect(result.data).toBeDefined();
    expect(result.data.chat).toBeDefined();
    expect(result.data.chat.messages).toBeInstanceOf(Array);
    expect(result.data.chat.messages.length).toBeGreaterThan(0);
    expect(result.data.chat.messages[0].role).toBe('system');
  });

  // Test for sending a message
  it('should send a message and get a response', async () => {
    const response = await request(testApp)
      .post('/message')
      .send({
        userId: 'test-user-id',
        message: 'Hello, assistant!',
      })
      .expect(200);

    const result = response.body as TestResult;
    expect(result.success).toBe(true);
    expect(result.data).toBeDefined();
    expect(result.data.response).toBeDefined();
    expect(result.data.response.role).toBe('assistant');
    expect(result.data.response.content).toContain('Hello, assistant!');
  });

  // Test for clearing chat history
  it('should clear chat history for a user', async () => {
    const response = await request(testApp)
      .delete('/history')
      .query({ userId: 'test-user-id' })
      .expect(200);

    // Response will be like "Chat history cleared successfully"
    expect(response.body.message).toContain('successfully');
  });
}); 