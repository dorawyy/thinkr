import { Request, Response } from 'express';
import { userAuthLogin } from '../../controllers/userAuthController';
import { AuthPayload } from '../../interfaces';

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

    describe('UNMOCKED POST /auth/login', () => {
        // Input: Valid googleId, name, and email
        // Expected status code: 200
        // Expected behavior: User created or retrieved via userAuthService
        // Expected output: User object with provided information
        it('should login a user successfully', async () => {
            const authPayload = {
                googleId: 'test-user-123',
                name: 'Test User',
                email: 'test@example.com',
            };

            mockRequest = {
                body: authPayload,
            };

            await userAuthLogin(
                mockRequest as Request,
                mockResponse as Response
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

        // Input: Missing required fields (googleId, name, or email)
        // Expected status code: 400
        // Expected behavior: Validation error, no service calls
        // Expected output: Error message
        it('should return 400 when required fields are missing', async () => {
            mockRequest = {
                body: {
                    googleId: 'test-user-123',
                    // Missing name and email
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
        });
    });
});
