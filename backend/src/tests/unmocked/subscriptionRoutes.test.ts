import { Request, Response } from 'express';
import {
    subscribe,
    unsubscribe,
    getSubscriptionStatus,
} from '../../controllers/subscriptionController';
import { Result, UserDTO } from '../../interfaces';

// Mock User model
jest.mock('../../db/mongo/models/User', () => {
    const mockFindOne = jest.fn();
    const mockUpdateOne = jest.fn();

    return {
        __esModule: true,
        findOne: mockFindOne,
        updateOne: mockUpdateOne,
        default: {
            findOne: mockFindOne,
            updateOne: mockUpdateOne,
        },
    };
});

// Import mocks after they're defined
const User = require('../../db/mongo/models/User').default;

// Interface tests for Subscription Controller
describe('Subscription Controller', () => {
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
    });

    // Interface POST /subscription
    describe('subscribe', () => {
        // Input: Valid userId
        // Expected status code: 200
        // Expected behavior: User found and subscription updated
        // Expected output: Updated user with subscribed=true
        it('should subscribe a user successfully', async () => {
            // Mock data
            const userId = 'test-user-123';
            const mockUser = {
                email: 'test@example.com',
                name: 'Test User',
                googleId: userId,
                subscribed: false,
            };

            // Setup request
            mockRequest = {
                body: { userId },
            };

            // Setup mocks
            User.findOne.mockResolvedValue(mockUser);
            User.updateOne.mockResolvedValue({ modifiedCount: 1 });

            // Call controller
            await subscribe(mockRequest as Request, mockResponse as Response);

            // Assertions
            expect(User.findOne).toHaveBeenCalledWith({ googleId: userId });
            expect(User.updateOne).toHaveBeenCalledWith(
                { googleId: userId },
                { subscribed: true }
            );
            expect(statusSpy).toHaveBeenCalledWith(200);
            expect(jsonSpy).toHaveBeenCalledWith({
                data: {
                    email: mockUser.email,
                    name: mockUser.name,
                    googleId: mockUser.googleId,
                    subscribed: true,
                },
            });
        });

        // Input: Missing userId
        // Expected status code: 400
        // Expected behavior: Validation error, no database calls
        // Expected output: Error message
        it('should return 400 when userId is missing', async () => {
            // Setup request with missing userId
            mockRequest = {
                body: {},
            };

            // Call controller
            await subscribe(mockRequest as Request, mockResponse as Response);

            // Assertions
            expect(statusSpy).toHaveBeenCalledWith(400);
            expect(jsonSpy).toHaveBeenCalledWith({
                message: 'You must provide a userId identifier',
            });
            expect(User.findOne).not.toHaveBeenCalled();
            expect(User.updateOne).not.toHaveBeenCalled();
        });

        // Input: Valid userId but database error occurs
        // Expected status code: 500
        // Expected behavior: Database operation fails
        // Expected output: Error message
        it('should return 500 when database operation fails', async () => {
            // Mock data
            const userId = 'test-user-123';

            // Setup request
            mockRequest = {
                body: { userId },
            };

            // Setup mock to throw error
            User.findOne.mockRejectedValue(new Error('Database error'));

            // Call controller
            await subscribe(mockRequest as Request, mockResponse as Response);

            // Assertions
            expect(statusSpy).toHaveBeenCalledWith(500);
            expect(jsonSpy).toHaveBeenCalledWith({
                message: 'Internal server error',
            });
        });

        // Input: Valid userId but user doesn't exist
        // Expected status code: 500
        // Expected behavior: User not found in database
        // Expected output: Internal server error message
        it('should return 500 when user is not found', async () => {
            // Mock data
            const userId = 'test-user-123';

            // Setup request
            mockRequest = {
                body: { userId },
            };

            // Setup mock to return null (user not found)
            User.findOne.mockResolvedValue(null);

            // Call controller
            await subscribe(mockRequest as Request, mockResponse as Response);

            // Assertions
            expect(statusSpy).toHaveBeenCalledWith(500);
            expect(jsonSpy).toHaveBeenCalledWith({
                message: 'Internal server error',
            });
        });
    });

    // Interface DELETE /subscription
    describe('unsubscribe', () => {
        // Input: Valid userId
        // Expected status code: 200
        // Expected behavior: User found and subscription updated
        // Expected output: Updated user with subscribed=false
        it('should unsubscribe a user successfully', async () => {
            // Mock data
            const userId = 'test-user-123';
            const mockUser = {
                email: 'test@example.com',
                name: 'Test User',
                googleId: userId,
                subscribed: true,
            };

            // Setup request
            mockRequest = {
                query: { userId },
            };

            // Setup mocks
            User.findOne.mockResolvedValue(mockUser);
            User.updateOne.mockResolvedValue({ modifiedCount: 1 });

            // Call controller
            await unsubscribe(mockRequest as Request, mockResponse as Response);

            // Assertions
            expect(User.findOne).toHaveBeenCalledWith({ googleId: userId });
            expect(User.updateOne).toHaveBeenCalledWith(
                { googleId: userId },
                { subscribed: false }
            );
            expect(statusSpy).toHaveBeenCalledWith(200);
            expect(jsonSpy).toHaveBeenCalledWith({
                data: {
                    email: mockUser.email,
                    name: mockUser.name,
                    googleId: mockUser.googleId,
                    subscribed: false,
                },
            });
        });

        it('should return 400 when userId is missing', async () => {
            // Setup request with missing userId
            mockRequest = {
                query: {},
            };

            // Call controller
            await unsubscribe(mockRequest as Request, mockResponse as Response);

            // Assertions
            expect(statusSpy).toHaveBeenCalledWith(400);
            expect(jsonSpy).toHaveBeenCalledWith({
                message: 'You must provide a userId identifier',
            });
            expect(User.findOne).not.toHaveBeenCalled();
            expect(User.updateOne).not.toHaveBeenCalled();
        });

        it('should return 500 when database operation fails', async () => {
            // Mock data
            const userId = 'test-user-123';

            // Setup request
            mockRequest = {
                query: { userId },
            };

            // Setup mock to throw error
            User.findOne.mockRejectedValue(new Error('Database error'));

            // Call controller
            await unsubscribe(mockRequest as Request, mockResponse as Response);

            // Assertions
            expect(statusSpy).toHaveBeenCalledWith(500);
            expect(jsonSpy).toHaveBeenCalledWith({
                message: 'Internal server error',
            });
        });
    });

    // Interface GET /subscription
    describe('getSubscriptionStatus', () => {
        // Input: Valid userId
        // Expected status code: 200
        // Expected behavior: User found and subscription status retrieved
        // Expected output: User object with subscription status
        it('should get subscription status successfully', async () => {
            // Mock data
            const userId = 'test-user-123';
            const mockUser = {
                email: 'test@example.com',
                name: 'Test User',
                googleId: userId,
                subscribed: true,
            };

            // Setup request
            mockRequest = {
                query: { userId },
            };

            // Setup mocks
            User.findOne.mockResolvedValue(mockUser);

            // Call controller
            await getSubscriptionStatus(
                mockRequest as Request,
                mockResponse as Response
            );

            // Assertions
            expect(User.findOne).toHaveBeenCalledWith({ googleId: userId });
            expect(statusSpy).toHaveBeenCalledWith(200);
            expect(jsonSpy).toHaveBeenCalledWith({
                data: {
                    email: mockUser.email,
                    name: mockUser.name,
                    googleId: mockUser.googleId,
                    subscribed: mockUser.subscribed,
                },
            });
        });

        it('should return 400 when userId is missing', async () => {
            // Setup request with missing userId
            mockRequest = {
                query: {},
            };

            // Call controller
            await getSubscriptionStatus(
                mockRequest as Request,
                mockResponse as Response
            );

            // Assertions
            expect(statusSpy).toHaveBeenCalledWith(400);
            expect(jsonSpy).toHaveBeenCalledWith({
                message: 'You must provide a userId identifier',
            });
            expect(User.findOne).not.toHaveBeenCalled();
        });

        it('should return 500 when database operation fails', async () => {
            // Mock data
            const userId = 'test-user-123';

            // Setup request
            mockRequest = {
                query: { userId },
            };

            // Setup mock to throw error
            User.findOne.mockRejectedValue(new Error('Database error'));

            // Call controller
            await getSubscriptionStatus(
                mockRequest as Request,
                mockResponse as Response
            );

            // Assertions
            expect(statusSpy).toHaveBeenCalledWith(500);
            expect(jsonSpy).toHaveBeenCalledWith({
                message: 'Internal server error',
            });
        });

        it('should return 500 when user is not found', async () => {
            // Mock data
            const userId = 'test-user-123';

            // Setup request
            mockRequest = {
                query: { userId },
            };

            // Setup mock to return null (user not found)
            User.findOne.mockResolvedValue(null);

            // Call controller
            await getSubscriptionStatus(
                mockRequest as Request,
                mockResponse as Response
            );

            // Assertions
            expect(statusSpy).toHaveBeenCalledWith(500);
            expect(jsonSpy).toHaveBeenCalledWith({
                message: 'Internal server error',
            });
        });
    });
});
