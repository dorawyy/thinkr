import { Request, Response } from 'express';
import {
    getUserChat,
    sendMessage,
    clearChatHistory,
} from '../../controllers/chatController';
import { ChatSessionDTO } from '../../interfaces';

jest.mock('../../db/mongo/models/Chat', () => {
    const mockFindOne = jest.fn();
    const mockCreate = jest.fn();
    const mockFindOneAndUpdate = jest.fn();

    return {
        __esModule: true,
        findOne: mockFindOne,
        create: mockCreate,
        findOneAndUpdate: mockFindOneAndUpdate,
        default: {
            findOne: mockFindOne,
            create: mockCreate,
            findOneAndUpdate: mockFindOneAndUpdate,
        },
    };
});

jest.mock('../../services/RAGService', () => {
    return {
        __esModule: true,
        default: jest.fn().mockImplementation(() => ({
            initVectorStore: jest.fn().mockResolvedValue(undefined),
            getRelevantContext: jest
                .fn()
                .mockResolvedValue('Relevant context from documents'),
        })),
    };
});

jest.mock('@langchain/openai', () => {
    return {
        ChatOpenAI: jest.fn().mockImplementation(() => ({
            invoke: jest.fn().mockResolvedValue({
                content: 'This is a response from the AI assistant.',
            }),
        })),
    };
});

const ChatSession = require('../../db/mongo/models/Chat').default;

jest.mock('uuid', () => ({
    v4: jest.fn().mockReturnValue('mock-uuid-value'),
}));

describe('Chat Controller', () => {
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

    describe('UNMOCKED GET /chat', () => {
        // Input: Valid userId with existing chat
        // Expected status code: 200
        // Expected behavior: Returns user's existing chat session
        // Expected output: Chat session object with messages
        it('should get an existing chat session', async () => {
            const userId = 'user123';
            const mockChatSession = {
                sessionId: 'session-123',
                googleId: userId,
                messages: [
                    {
                        role: 'system',
                        content:
                            'You are a helpful assistant that provides accurate information based on the context provided.',
                        timestamp: '2023-01-01T10:00:00.000Z',
                    },
                ],
                createdAt: '2023-01-01T10:00:00.000Z',
                updatedAt: '2023-01-01T10:00:00.000Z',
                metadata: { type: 'general' },
            };

            mockRequest = {
                query: { userId },
            };

            ChatSession.findOne.mockResolvedValue(mockChatSession);

            await getUserChat(mockRequest as Request, mockResponse as Response);

            expect(ChatSession.findOne).toHaveBeenCalledWith({
                googleId: userId,
            });
            expect(statusSpy).toHaveBeenCalledWith(200);
            expect(jsonSpy).toHaveBeenCalledWith({
                data: {
                    chat: expect.objectContaining({
                        messages: expect.any(Array),
                        createdAt: expect.any(String),
                        updatedAt: expect.any(String),
                        metadata: expect.any(Object),
                    }),
                },
            });
        });

        // Input: Valid userId with no existing chat
        // Expected status code: 200
        // Expected behavior: Creates new chat session for user
        // Expected output: New chat session object with system message
        it('should create a new chat session if none exists', async () => {
            const userId = 'user123';
            const newChatSession = {
                sessionId: 'mock-uuid-value',
                googleId: userId,
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

            mockRequest = {
                query: { userId },
            };

            ChatSession.findOne.mockResolvedValue(null);
            ChatSession.create.mockResolvedValue(newChatSession);

            await getUserChat(mockRequest as Request, mockResponse as Response);

            expect(ChatSession.findOne).toHaveBeenCalledWith({
                googleId: userId,
            });
            expect(ChatSession.create).toHaveBeenCalledWith(
                expect.objectContaining({
                    sessionId: 'mock-uuid-value',
                    googleId: userId,
                    messages: expect.any(Array),
                    metadata: { type: 'general' },
                })
            );
            expect(statusSpy).toHaveBeenCalledWith(200);
            expect(jsonSpy).toHaveBeenCalledWith({
                data: {
                    chat: expect.objectContaining({
                        messages: expect.any(Array),
                        createdAt: expect.any(String),
                        updatedAt: expect.any(String),
                        metadata: expect.any(Object),
                    }),
                },
            });
        });

        // Input: Missing userId
        // Expected status code: 400
        // Expected behavior: Validation error, no chat retrieval
        // Expected output: Error message
        it('should return 400 when userId is missing', async () => {
            mockRequest = {
                query: {},
            };

            await getUserChat(mockRequest as Request, mockResponse as Response);

            expect(statusSpy).toHaveBeenCalledWith(400);
            expect(jsonSpy).toHaveBeenCalledWith({
                message: 'User ID is required',
            });
            expect(ChatSession.findOne).not.toHaveBeenCalled();
        });

        // Input: Valid userId but database error occurs
        // Expected status code: 500
        // Expected behavior: Chat retrieval fails
        // Expected output: Error message
        it('should return 500 when database operation fails', async () => {
            const userId = 'user123';

            mockRequest = {
                query: { userId },
            };

            ChatSession.findOne.mockRejectedValue(new Error('Database error'));

            await getUserChat(mockRequest as Request, mockResponse as Response);

            expect(statusSpy).toHaveBeenCalledWith(500);
            expect(jsonSpy).toHaveBeenCalledWith({
                message: 'Internal server error',
            });
        });
    });

    describe('UNMOCKED POST /chat/message', () => {
        // Input: Valid userId and message
        // Expected status code: 200
        // Expected behavior: Message sent to AI, response generated and saved
        // Expected output: AI response message
        it('should send a message and get a response', async () => {
            const userId = 'user123';
            const userMessage = 'Hello, assistant!';
            const mockChatSession: Partial<ChatSessionDTO> = {
                userId,
                messages: [
                    {
                        role: 'system',
                        content:
                            'You are a helpful assistant that provides accurate information based on the context provided.',
                        timestamp: '2023-01-01T10:00:00.000Z',
                    },
                ],
                createdAt: '2023-01-01T10:00:00.000Z',
                updatedAt: '2023-01-01T10:00:00.000Z',
                metadata: { type: 'general' },
            };

            mockRequest = {
                body: { userId, message: userMessage },
            };

            ChatSession.findOne.mockResolvedValue({
                sessionId: 'session-123',
                googleId: userId,
                messages: mockChatSession.messages,
                createdAt: mockChatSession.createdAt,
                updatedAt: mockChatSession.updatedAt,
                metadata: mockChatSession.metadata,
            });

            ChatSession.findOneAndUpdate.mockResolvedValue({});

            await sendMessage(mockRequest as Request, mockResponse as Response);

            expect(ChatSession.findOneAndUpdate).toHaveBeenCalledWith(
                { googleId: userId },
                {
                    $push: {
                        messages: {
                            $each: expect.arrayContaining([
                                expect.objectContaining({
                                    role: 'user',
                                    content: userMessage,
                                }),
                                expect.objectContaining({
                                    role: 'assistant',
                                    content:
                                        'This is a response from the AI assistant.',
                                }),
                            ]),
                        },
                    },
                    $set: expect.any(Object),
                }
            );

            expect(statusSpy).toHaveBeenCalledWith(200);
            expect(jsonSpy).toHaveBeenCalledWith({
                data: {
                    response: expect.objectContaining({
                        role: 'assistant',
                        content: 'This is a response from the AI assistant.',
                        timestamp: expect.any(String),
                    }),
                },
            });
        });

        // Input: Missing userId or message
        // Expected status code: 400
        // Expected behavior: Validation error, no message sent
        // Expected output: Error message
        it('should return 400 when required fields are missing', async () => {
            mockRequest = {
                body: { message: 'Hello' },
            };

            await sendMessage(mockRequest as Request, mockResponse as Response);

            expect(statusSpy).toHaveBeenCalledWith(400);
            expect(jsonSpy).toHaveBeenCalledWith({
                message: 'User ID and message are required',
            });

            mockRequest = {
                body: { userId: 'user123' },
            };

            await sendMessage(mockRequest as Request, mockResponse as Response);

            expect(statusSpy).toHaveBeenCalledWith(400);
        });

        // Input: Valid input but error during processing
        // Expected status code: 500
        // Expected behavior: Message sending fails
        // Expected output: Error message
        it('should return 500 when an error occurs', async () => {
            const userId = 'user123';
            const userMessage = 'Hello, assistant!';

            mockRequest = {
                body: { userId, message: userMessage },
            };

            ChatSession.findOne.mockRejectedValue(new Error('Database error'));

            await sendMessage(mockRequest as Request, mockResponse as Response);

            expect(statusSpy).toHaveBeenCalledWith(500);
            expect(jsonSpy).toHaveBeenCalledWith({
                message: 'Internal server error',
            });
        });
    });

    describe('UNMOCKED DELETE /chat/history', () => {
        // Input: Valid userId
        // Expected status code: 200
        // Expected behavior: Chat history is cleared, only system message remains
        // Expected output: Success message
        it('should clear chat history successfully', async () => {
            const userId = 'user123';

            mockRequest = {
                query: { userId },
            };

            ChatSession.findOneAndUpdate.mockResolvedValue({});

            await clearChatHistory(
                mockRequest as Request,
                mockResponse as Response
            );

            expect(ChatSession.findOneAndUpdate).toHaveBeenCalledWith(
                { googleId: userId },
                {
                    $set: {
                        messages: expect.arrayContaining([
                            expect.objectContaining({
                                role: 'system',
                                content:
                                    'You are a helpful assistant that provides accurate information based on the context provided.',
                            }),
                        ]),
                        updatedAt: expect.any(Object),
                    },
                },
                { upsert: true }
            );

            expect(statusSpy).toHaveBeenCalledWith(200);
            expect(jsonSpy).toHaveBeenCalledWith({
                message: 'Chat history cleared successfully',
            });
        });

        // Input: Missing userId
        // Expected status code: 400
        // Expected behavior: Validation error, no clearing occurs
        // Expected output: Error message
        it('should return 400 when userId is missing', async () => {
            mockRequest = {
                query: {},
            };

            await clearChatHistory(
                mockRequest as Request,
                mockResponse as Response
            );

            expect(statusSpy).toHaveBeenCalledWith(400);
            expect(jsonSpy).toHaveBeenCalledWith({
                message: 'User ID is required',
            });
            expect(ChatSession.findOneAndUpdate).not.toHaveBeenCalled();
        });

        // Input: Valid userId but database error occurs
        // Expected status code: 500
        // Expected behavior: History clearing fails
        // Expected output: Error message
        it('should return 500 when an error occurs', async () => {
            const userId = 'user123';

            mockRequest = {
                query: { userId },
            };

            ChatSession.findOneAndUpdate.mockRejectedValue(
                new Error('Database error')
            );

            await clearChatHistory(
                mockRequest as Request,
                mockResponse as Response
            );

            expect(statusSpy).toHaveBeenCalledWith(500);
            expect(jsonSpy).toHaveBeenCalledWith({
                message: 'Internal server error',
            });
        });
    });
});
