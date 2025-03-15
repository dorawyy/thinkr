import { Request, Response } from 'express';
import { userAuthLogin } from '../../controllers/userAuthController';
import { AuthPayload } from '../../interfaces';

jest.mock('../../services/userAuthService', () => {
    return {
        __esModule: true,
        default: {
            findCreateUser: jest
                .fn()
                .mockImplementation(async (authPayload: AuthPayload) => {
                    if (authPayload.googleId === 'error-user') {
                        throw new Error('Service error');
                    }
                    return {
                        googleId: authPayload.googleId,
                        name: authPayload.name,
                        email: authPayload.email,
                        subscribed:
                            authPayload.googleId === 'existing-google-id'
                                ? true
                                : false,
                    };
                }),
        },
    };
});

const userAuthService = require('../../services/userAuthService').default;

describe('User Auth Controller', () => {
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
    });

    describe('MOCKED POST /auth/login', () => {
        // Input: Valid googleId, name, and email for a new user
        // Expected status code: 200
        // Expected behavior: User created via userAuthService
        // Expected output: User object with provided data and subscribed=false
        it('should create a new user successfully', async () => {
            const authPayload = {
                googleId: 'new-google-id',
                name: 'New User',
                email: 'new@example.com',
            };

            mockRequest = {
                body: authPayload,
            };

            await userAuthLogin(
                mockRequest as Request,
                mockResponse as Response
            );

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

        // Input: Valid googleId, name, and email for an existing user
        // Expected status code: 200
        // Expected behavior: Existing user retrieved via userAuthService
        // Expected output: User object with correct subscription status
        it('should return existing user when found', async () => {
            const authPayload = {
                googleId: 'existing-google-id',
                name: 'Existing User',
                email: 'existing@example.com',
            };

            mockRequest = {
                body: authPayload,
            };

            await userAuthLogin(
                mockRequest as Request,
                mockResponse as Response
            );

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
                        subscribed: true,
                    },
                },
            });
        });

        // Input: Missing required fields (googleId, name, or email)
        // Expected status code: 400
        // Expected behavior: Validation error, no service calls
        // Expected output: Error message
        it('should return 400 when required fields are missing', async () => {
            mockRequest = {
                body: {
                    name: 'Test User',
                    email: 'test@example.com',
                },
            };

            await userAuthLogin(
                mockRequest as Request,
                mockResponse as Response
            );

            expect(statusSpy).toHaveBeenCalledWith(400);
            expect(jsonSpy).toHaveBeenCalledWith({
                message: 'googleId, name, or email is missing or invalid',
            });
            expect(userAuthService.findCreateUser).not.toHaveBeenCalled();

            mockRequest = {
                body: {
                    googleId: 'test-id',
                    email: 'test@example.com',
                },
            };

            await userAuthLogin(
                mockRequest as Request,
                mockResponse as Response
            );

            expect(statusSpy).toHaveBeenCalledWith(400);

            mockRequest = {
                body: {
                    googleId: 'test-id',
                    name: 'Test User',
                },
            };

            await userAuthLogin(
                mockRequest as Request,
                mockResponse as Response
            );

            expect(statusSpy).toHaveBeenCalledWith(400);
        });

        // Input: Valid data but service throws error
        // Expected status code: 500
        // Expected behavior: Service call fails
        // Expected output: Error message
        it('should handle errors when service operation fails', async () => {
            const authPayload = {
                googleId: 'error-user',
                name: 'Error User',
                email: 'error@example.com',
            };

            mockRequest = {
                body: authPayload,
            };

            const originalConsoleError = console.error;
            console.error = jest.fn();

            try {
                await userAuthLogin(
                    mockRequest as Request,
                    mockResponse as Response
                );

                expect(statusSpy).toHaveBeenCalledWith(500);
                expect(jsonSpy).toHaveBeenCalledWith(
                    expect.objectContaining({
                        message: expect.any(String),
                    })
                );
            } catch (error) {
            } finally {
                console.error = originalConsoleError;
            }

            expect(userAuthService.findCreateUser).toHaveBeenCalledWith(
                authPayload
            );
        });
    });
});
