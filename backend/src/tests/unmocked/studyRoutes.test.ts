import { Request, Response } from 'express';
import {
    retrieveFlashcards,
    retrieveQuizzes,
    getSuggestedMaterials,
} from '../../controllers/studyController';
import { Result } from '../../interfaces';

// Mock StudyService
jest.mock('../../services/studyService', () => {
    return {
        __esModule: true,
        default: {
            retrieveFlashcards: jest
                .fn()
                .mockImplementation(async (documentId, userId) => {
                    if (userId === 'error-user') {
                        throw new Error('Database error');
                    }
                    return [
                        { front: 'Question 1', back: 'Answer 1' },
                        { front: 'Question 2', back: 'Answer 2' },
                    ];
                }),
            retrieveQuizzes: jest
                .fn()
                .mockImplementation(async (documentId, userId) => {
                    if (userId === 'error-user') {
                        throw new Error('Database error');
                    }
                    return [
                        {
                            question: 'Quiz Question 1',
                            options: [
                                'Option A',
                                'Option B',
                                'Option C',
                                'Option D',
                            ],
                            answer: 'Option A',
                        },
                        {
                            question: 'Quiz Question 2',
                            options: [
                                'Option A',
                                'Option B',
                                'Option C',
                                'Option D',
                            ],
                            answer: 'Option C',
                        },
                    ];
                }),
            getSuggestedMaterials: jest
                .fn()
                .mockImplementation(async (userId, limit) => {
                    if (userId === 'error-user') {
                        throw new Error('Database error');
                    }
                    return [
                        { id: 'doc1', title: 'Document 1', type: 'pdf' },
                        { id: 'doc2', title: 'Document 2', type: 'text' },
                    ];
                }),
        },
    };
});

// Import mocks after they're defined
const studyService = require('../../services/studyService').default;

describe('Study Controller', () => {
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

    describe('retrieveFlashcards', () => {
        it('should retrieve flashcards successfully', async () => {
            // Mock data
            const userId = 'test-user-123';
            const documentId = 'doc-123';

            // Setup request
            mockRequest = {
                query: { userId, documentId },
            };

            // Call controller
            await retrieveFlashcards(
                mockRequest as Request,
                mockResponse as Response
            );

            // Assertions
            expect(studyService.retrieveFlashcards).toHaveBeenCalledWith(
                documentId,
                userId
            );
            expect(statusSpy).toHaveBeenCalledWith(200);
            expect(jsonSpy).toHaveBeenCalledWith({
                data: [
                    { front: 'Question 1', back: 'Answer 1' },
                    { front: 'Question 2', back: 'Answer 2' },
                ],
            });
        });

        it('should return 400 when userId is missing', async () => {
            // Setup request with missing userId
            mockRequest = {
                query: { documentId: 'doc-123' },
            };

            // Call controller
            await retrieveFlashcards(
                mockRequest as Request,
                mockResponse as Response
            );

            // Assertions
            expect(statusSpy).toHaveBeenCalledWith(400);
            expect(jsonSpy).toHaveBeenCalledWith({
                message: 'You must provide a userId identifier',
            });
            expect(studyService.retrieveFlashcards).not.toHaveBeenCalled();
        });

        it('should return 500 when service operation fails', async () => {
            // Setup request with error-triggering userId
            mockRequest = {
                query: { userId: 'error-user', documentId: 'doc-123' },
            };

            // Call controller with console.error mocked to prevent test output noise
            const originalConsoleError = console.error;
            console.error = jest.fn();

            await retrieveFlashcards(
                mockRequest as Request,
                mockResponse as Response
            );

            // Restore console.error
            console.error = originalConsoleError;

            // Assertions
            expect(statusSpy).toHaveBeenCalledWith(500);
            expect(jsonSpy).toHaveBeenCalledWith({
                message: 'Internal server error',
            });
        });
    });

    describe('retrieveQuizzes', () => {
        it('should retrieve quizzes successfully', async () => {
            // Mock data
            const userId = 'test-user-123';
            const documentId = 'doc-123';

            // Setup request
            mockRequest = {
                query: { userId, documentId },
            };

            // Call controller
            await retrieveQuizzes(
                mockRequest as Request,
                mockResponse as Response
            );

            // Assertions
            expect(studyService.retrieveQuizzes).toHaveBeenCalledWith(
                documentId,
                userId
            );
            expect(statusSpy).toHaveBeenCalledWith(200);
            expect(jsonSpy).toHaveBeenCalledWith({
                data: [
                    {
                        question: 'Quiz Question 1',
                        options: [
                            'Option A',
                            'Option B',
                            'Option C',
                            'Option D',
                        ],
                        answer: 'Option A',
                    },
                    {
                        question: 'Quiz Question 2',
                        options: [
                            'Option A',
                            'Option B',
                            'Option C',
                            'Option D',
                        ],
                        answer: 'Option C',
                    },
                ],
            });
        });

        it('should return 400 when userId is missing', async () => {
            // Setup request with missing userId
            mockRequest = {
                query: { documentId: 'doc-123' },
            };

            // Call controller
            await retrieveQuizzes(
                mockRequest as Request,
                mockResponse as Response
            );

            // Assertions
            expect(statusSpy).toHaveBeenCalledWith(400);
            expect(jsonSpy).toHaveBeenCalledWith({
                message: 'You must provide a userId identifier',
            });
            expect(studyService.retrieveQuizzes).not.toHaveBeenCalled();
        });

        it('should return 500 when service operation fails', async () => {
            // Setup request with error-triggering userId
            mockRequest = {
                query: { userId: 'error-user', documentId: 'doc-123' },
            };

            // Call controller with console.error mocked to prevent test output noise
            const originalConsoleError = console.error;
            console.error = jest.fn();

            await retrieveQuizzes(
                mockRequest as Request,
                mockResponse as Response
            );

            // Restore console.error
            console.error = originalConsoleError;

            // Assertions
            expect(statusSpy).toHaveBeenCalledWith(500);
            expect(jsonSpy).toHaveBeenCalledWith({
                message: 'Internal server error',
            });
        });
    });

    describe('getSuggestedMaterials', () => {
        it('should get suggested materials successfully', async () => {
            // Mock data
            const userId = 'test-user-123';
            const limit = 5;

            // Setup request
            mockRequest = {
                query: { userId, limit: limit.toString() },
            };

            // Call controller
            await getSuggestedMaterials(
                mockRequest as Request,
                mockResponse as Response
            );

            // Assertions
            expect(studyService.getSuggestedMaterials).toHaveBeenCalledWith(
                userId,
                limit
            );
            expect(statusSpy).toHaveBeenCalledWith(200);
            expect(jsonSpy).toHaveBeenCalledWith({
                data: [
                    { id: 'doc1', title: 'Document 1', type: 'pdf' },
                    { id: 'doc2', title: 'Document 2', type: 'text' },
                ],
            });
        });

        it('should use default limit when not provided', async () => {
            // Mock data
            const userId = 'test-user-123';

            // Setup request without limit
            mockRequest = {
                query: { userId },
            };

            // Call controller
            await getSuggestedMaterials(
                mockRequest as Request,
                mockResponse as Response
            );

            // Assertions
            expect(studyService.getSuggestedMaterials).toHaveBeenCalledWith(
                userId,
                5
            ); // Default limit
            expect(statusSpy).toHaveBeenCalledWith(200);
        });

        it('should return 400 when userId is missing', async () => {
            // Setup request with missing userId
            mockRequest = {
                query: { limit: '10' },
            };

            // Call controller
            await getSuggestedMaterials(
                mockRequest as Request,
                mockResponse as Response
            );

            // Assertions
            expect(statusSpy).toHaveBeenCalledWith(400);
            expect(jsonSpy).toHaveBeenCalledWith({
                message: 'You must provide a userId identifier',
            });
            expect(studyService.getSuggestedMaterials).not.toHaveBeenCalled();
        });

        it('should return 500 when service operation fails', async () => {
            // Setup request with error-triggering userId
            mockRequest = {
                query: { userId: 'error-user' },
            };

            // Call controller with console.error mocked to prevent test output noise
            const originalConsoleError = console.error;
            console.error = jest.fn();

            await getSuggestedMaterials(
                mockRequest as Request,
                mockResponse as Response
            );

            // Restore console.error
            console.error = originalConsoleError;

            // Assertions
            expect(statusSpy).toHaveBeenCalledWith(500);
            expect(jsonSpy).toHaveBeenCalledWith({
                message: 'Internal server error',
            });
        });
    });
});
