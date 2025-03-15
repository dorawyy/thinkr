import request from 'supertest';
import express from 'express';

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

import chatRouter from '../../routes/chatRoutes';
const {
    mockGetOrCreateUserChat,
    mockSendMessage,
    mockClearChatHistory,
} = require('../../services/ChatService');

const app = express();
app.use(express.json());
app.use('/', chatRouter);

describe('Chat Routes (Mocked)', () => {
    afterEach(() => {
        jest.clearAllMocks();
    });

    describe('GET /', () => {
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

    describe('POST /message', () => {
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

    describe('DELETE /history', () => {
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
