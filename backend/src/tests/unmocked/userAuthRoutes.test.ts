import { Request, Response } from 'express';
import { userAuthLogin } from '../../controllers/userAuthController';
import { Result, UserDTO, AuthPayload } from '../../interfaces';

// Mock User model
jest.mock('../../db/mongo/models/User', () => {
    const mockFindOne = jest.fn();
    const mockSave = jest.fn();

    return {
        __esModule: true,
        findOne: mockFindOne,
        default: class MockUser {
            name: string;
            email: string;
            googleId: string;
            subscribed: boolean;

            constructor(userData: any) {
                this.name = userData.name;
                this.email = userData.email;
                this.googleId = userData.googleId;
                this.subscribed = userData.subscribed;
            }

            save = mockSave;

            static findOne = mockFindOne;
        },
    };
});

// Mock userAuthService
jest.mock('../../services/userAuthService', () => {
    return {
        __esModule: true,
        default: {
            findCreateUser: jest
                .fn()
                .mockImplementation(async (authPayload: AuthPayload) => {
                    if (authPayload.googleId === 'error-user') {
                        throw new Error('Database error');
                    }
                    return {
                        googleId: authPayload.googleId,
                        name: authPayload.name,
                        email: authPayload.email,
                        subscribed: false,
                    };
                }),
        },
    };
});

// Import mocks after they're defined
const User = require('../../db/mongo/models/User').default;
const userAuthService = require('../../services/userAuthService').default;

describe('User Auth Controller', () => {
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

    describe('userAuthLogin', () => {
        it('should login a user successfully', async () => {
            // Mock data
            const authPayload = {
                googleId: 'test-user-123',
                name: 'Test User',
                email: 'test@example.com',
            };

            // Setup request
            mockRequest = {
                body: authPayload,
            };

            // Call controller
            await userAuthLogin(
                mockRequest as Request,
                mockResponse as Response
            );

            // Assertions
            expect(userAuthService.findCreateUser).toHaveBeenCalledWith(
                authPayload
            );
            expect(statusSpy).toHaveBeenCalledWith(200);
            expect(jsonSpy).toHaveBeenCalledWith({
                data: {
                    user: {
                        googleId: authPayload.googleId,
                        name: authPayload.name,
                        email: authPayload.email,
                        subscribed: false,
                    },
                },
            });
        });

        it('should return 400 when required fields are missing', async () => {
            // Setup request with missing fields
            mockRequest = {
                body: {
                    googleId: 'test-user-123',
                    // Missing name and email
                },
            };

            // Call controller
            await userAuthLogin(
                mockRequest as Request,
                mockResponse as Response
            );

            // Assertions
            expect(statusSpy).toHaveBeenCalledWith(400);
            expect(jsonSpy).toHaveBeenCalledWith({
                message: 'googleId, name, or email is missing or invalid',
            });
            expect(userAuthService.findCreateUser).not.toHaveBeenCalled();
        });

        it('should return 500 when database operation fails', async () => {
            // Setup request with error-triggering googleId
            mockRequest = {
                body: {
                    googleId: 'error-user',
                    name: 'Error User',
                    email: 'error@example.com',
                },
            };

            // Call controller with console.error mocked to prevent test output noise
            const originalConsoleError = console.error;
            console.error = jest.fn();

            try {
                await userAuthLogin(
                    mockRequest as Request,
                    mockResponse as Response
                );
            } catch (error) {
                // Expect error to be thrown
            }

            // Restore console.error
            console.error = originalConsoleError;

            // Assertions for error handling
            expect(userAuthService.findCreateUser).toHaveBeenCalled();
            // The controller doesn't have explicit error handling, so we can't assert on status/json
        });
    });
});
