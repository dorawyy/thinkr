import request from 'supertest';
import express from 'express';
import { Result, ChatSessionDTO, ChatMessage } from '../../interfaces';

// Mock ChatService
jest.mock('../../services/ChatService', () => {
    const mockGetOrCreateUserChat = jest.fn();
    const mockSendMessage = jest.fn();
    const mockClearChatHistory = jest.fn();

    return {
        __esModule: true,
        mockGetOrCreateUserChat,
        mockSendMessage,
        mockClearChatHistory,
        default: {
            getOrCreateUserChat: mockGetOrCreateUserChat,
            sendMessage: mockSendMessage,
            clearChatHistory: mockClearChatHistory,
        },
    };
});

// Import after mocks are defined
import chatRouter from '../../routes/chatRoutes';
const {
    mockGetOrCreateUserChat,
    mockSendMessage,
    mockClearChatHistory,
} = require('../../services/ChatService');

// Create express app just for testing
const app = express();
app.use(express.json());
app.use('/', chatRouter);

describe('Chat Routes (Mocked)', () => {
    afterEach(() => {
        jest.clearAllMocks();
    });

    // Get User Chat Tests
    describe('GET /', () => {
        // Input: Valid userId
        // Expected status code: 200
        // Expected behavior: ChatService.getOrCreateUserChat called
        // Expected output: chat session object
        it('should get or create a chat session for a user', async () => {
            const mockChatSession: ChatSessionDTO = {
                userId: 'user123',
                messages: [
                    {
                        role: 'system',
                        content:
                            'You are a helpful assistant that provides accurate information based on the context provided.',
                        timestamp: '2023-01-01T12:00:00.000Z',
                    },
                ],
                createdAt: '2023-01-01T12:00:00.000Z',
                updatedAt: '2023-01-01T12:00:00.000Z',
                metadata: { type: 'general' },
            };

            mockGetOrCreateUserChat.mockResolvedValue(mockChatSession);

            const response = await request(app)
                .get('/')
                .query({ userId: 'user123' })
                .expect(200);

            const result = response.body as Result;
            expect(result.data).toBeDefined();
            expect(result.data.chat).toEqual(mockChatSession);

            expect(mockGetOrCreateUserChat).toHaveBeenCalledWith('user123');
        });

        // Input: Missing userId
        // Expected status code: 400
        // Expected behavior: validation error, no service calls
        // Expected output: error message
        it('should return 400 when userId is missing', async () => {
            await request(app).get('/').expect(400);

            expect(mockGetOrCreateUserChat).not.toHaveBeenCalled();
        });

        // Input: Valid request but service throws error
        // Expected status code: 500
        // Expected behavior: ChatService.getOrCreateUserChat called but throws error
        // Expected output: error message
        it('should return 500 when service throws error', async () => {
            mockGetOrCreateUserChat.mockRejectedValue(
                new Error('Service error')
            );

            const response = await request(app)
                .get('/')
                .query({ userId: 'user123' })
                .expect(500);

            expect(response.body.message).toBe('Internal server error');
        });
    });

    // Send Message Tests
    describe('POST /message', () => {
        // Input: Valid userId and message
        // Expected status code: 200
        // Expected behavior: ChatService.sendMessage called
        // Expected output: response message
        it('should send a message and get a response', async () => {
            const mockResponseMessage: ChatMessage = {
                role: 'assistant',
                content: 'This is a response from the assistant.',
                timestamp: '2023-01-01T12:01:00.000Z',
            };

            mockSendMessage.mockResolvedValue(mockResponseMessage);

            const response = await request(app)
                .post('/message')
                .send({
                    userId: 'user123',
                    message: 'Hello, assistant!',
                })
                .expect(200);

            const result = response.body as Result;
            expect(result.data).toBeDefined();
            expect(result.data.response).toEqual(mockResponseMessage);

            expect(mockSendMessage).toHaveBeenCalledWith(
                'user123',
                'Hello, assistant!'
            );
        });

        // Input: Missing userId or message
        // Expected status code: 400
        // Expected behavior: validation error, no service calls
        // Expected output: error message
        it('should return 400 when required fields are missing', async () => {
            await request(app)
                .post('/message')
                .send({
                    userId: 'user123',
                })
                .expect(400);

            await request(app)
                .post('/message')
                .send({
                    message: 'Hello, assistant!',
                })
                .expect(400);

            expect(mockSendMessage).not.toHaveBeenCalled();
        });

        // Input: Valid request but service throws error
        // Expected status code: 500
        // Expected behavior: ChatService.sendMessage called but throws error
        // Expected output: error message
        it('should return 500 when service throws error', async () => {
            mockSendMessage.mockRejectedValue(new Error('Service error'));

            const response = await request(app)
                .post('/message')
                .send({
                    userId: 'user123',
                    message: 'Hello, assistant!',
                })
                .expect(500);

            expect(response.body.message).toBe('Internal server error');
        });
    });

    // Clear Chat History Tests
    describe('DELETE /history', () => {
        // Input: Valid userId
        // Expected status code: 200
        // Expected behavior: ChatService.clearChatHistory called
        // Expected output: success message
        it('should clear chat history for a user', async () => {
            mockClearChatHistory.mockResolvedValue(undefined);

            const response = await request(app)
                .delete('/history')
                .query({ userId: 'user123' })
                .expect(200);

            expect(response.body.message).toBe(
                'Chat history cleared successfully'
            );
            expect(mockClearChatHistory).toHaveBeenCalledWith('user123');
        });

        // Input: Missing userId
        // Expected status code: 400
        // Expected behavior: validation error, no service calls
        // Expected output: error message
        it('should return 400 when userId is missing', async () => {
            await request(app).delete('/history').expect(400);

            expect(mockClearChatHistory).not.toHaveBeenCalled();
        });

        // Input: Valid request but service throws error
        // Expected status code: 500
        // Expected behavior: ChatService.clearChatHistory called but throws error
        // Expected output: error message
        it('should return 500 when service throws error', async () => {
            mockClearChatHistory.mockRejectedValue(new Error('Service error'));

            const response = await request(app)
                .delete('/history')
                .query({ userId: 'user123' })
                .expect(500);

            expect(response.body.message).toBe('Internal server error');
        });
    });
});
