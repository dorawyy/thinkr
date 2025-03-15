import { Request, Response } from 'express';
import {
    retrieveFlashcards,
    retrieveQuizzes,
    getSuggestedMaterials,
} from '../../controllers/studyController';

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
                .mockImplementation(async (userId) => {
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

const studyService = require('../../services/studyService').default;

describe('Study Controller', () => {
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

    describe('retrieveFlashcards', () => {
        // Input: Valid userId and documentId
        // Expected status code: 200
        // Expected behavior: StudyService.retrieveFlashcards called
        // Expected output: Array of flashcards
        it('should retrieve flashcards successfully', async () => {
            const userId = 'test-user-123';
            const documentId = 'doc-123';

            mockRequest = {
                query: { userId, documentId },
            };

            await retrieveFlashcards(
                mockRequest as Request,
                mockResponse as Response
            );

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

        // Input: Missing userId
        // Expected status code: 400
        // Expected behavior: Validation error, no service calls
        // Expected output: Error message
        it('should return 400 when userId is missing', async () => {
            mockRequest = {
                query: { documentId: 'doc-123' },
            };

            await retrieveFlashcards(
                mockRequest as Request,
                mockResponse as Response
            );

            expect(statusSpy).toHaveBeenCalledWith(400);
            expect(jsonSpy).toHaveBeenCalledWith({
                message: 'You must provide a userId identifier',
            });
            expect(studyService.retrieveFlashcards).not.toHaveBeenCalled();
        });

        // Input: Valid request but service operation fails
        // Expected status code: 500
        // Expected behavior: StudyService.retrieveFlashcards throws error
        // Expected output: Error message
        it('should return 500 when service operation fails', async () => {
            mockRequest = {
                query: { userId: 'error-user', documentId: 'doc-123' },
            };

            const originalConsoleError = console.error;
            console.error = jest.fn();

            await retrieveFlashcards(
                mockRequest as Request,
                mockResponse as Response
            );

            console.error = originalConsoleError;

            expect(statusSpy).toHaveBeenCalledWith(500);
            expect(jsonSpy).toHaveBeenCalledWith({
                message: 'Internal server error',
            });
        });
    });

    describe('retrieveQuizzes', () => {
        // Input: Valid userId and documentId
        // Expected status code: 200
        // Expected behavior: StudyService.retrieveQuizzes called
        // Expected output: Array of quizzes
        it('should retrieve quizzes successfully', async () => {
            const userId = 'test-user-123';
            const documentId = 'doc-123';

            mockRequest = {
                query: { userId, documentId },
            };

            await retrieveQuizzes(
                mockRequest as Request,
                mockResponse as Response
            );

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

        // Input: Missing userId
        // Expected status code: 400
        // Expected behavior: Validation error, no service calls
        // Expected output: Error message
        it('should return 400 when userId is missing', async () => {
            mockRequest = {
                query: { documentId: 'doc-123' },
            };

            await retrieveQuizzes(
                mockRequest as Request,
                mockResponse as Response
            );

            expect(statusSpy).toHaveBeenCalledWith(400);
            expect(jsonSpy).toHaveBeenCalledWith({
                message: 'You must provide a userId identifier',
            });
            expect(studyService.retrieveQuizzes).not.toHaveBeenCalled();
        });

        // Input: Valid request but service operation fails
        // Expected status code: 500
        // Expected behavior: StudyService.retrieveQuizzes throws error
        // Expected output: Error message
        it('should return 500 when service operation fails', async () => {
            mockRequest = {
                query: { userId: 'error-user', documentId: 'doc-123' },
            };

            const originalConsoleError = console.error;
            console.error = jest.fn();

            await retrieveQuizzes(
                mockRequest as Request,
                mockResponse as Response
            );

            console.error = originalConsoleError;

            expect(statusSpy).toHaveBeenCalledWith(500);
            expect(jsonSpy).toHaveBeenCalledWith({
                message: 'Internal server error',
            });
        });
    });

    describe('getSuggestedMaterials', () => {
        // Input: Valid userId
        // Expected status code: 200
        // Expected behavior: StudyService.getSuggestedMaterials called
        // Expected output: Array of suggested materials
        it('should get suggested materials successfully', async () => {
            const userId = 'test-user-123';
            const limit = 5;

            mockRequest = {
                query: { userId, limit: limit.toString() },
            };

            await getSuggestedMaterials(
                mockRequest as Request,
                mockResponse as Response
            );

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

        // Input: Valid userId without limit parameter
        // Expected status code: 200
        // Expected behavior: StudyService.getSuggestedMaterials called with default limit
        // Expected output: Array of suggested materials
        it('should use default limit when not provided', async () => {
            const userId = 'test-user-123';

            mockRequest = {
                query: { userId },
            };

            await getSuggestedMaterials(
                mockRequest as Request,
                mockResponse as Response
            );

            expect(studyService.getSuggestedMaterials).toHaveBeenCalledWith(
                userId,
                5
            );
            expect(statusSpy).toHaveBeenCalledWith(200);
        });

        // Input: Missing userId
        // Expected status code: 400
        // Expected behavior: Validation error, no service calls
        // Expected output: Error message
        it('should return 400 when userId is missing', async () => {
            mockRequest = {
                query: { limit: '10' },
            };

            await getSuggestedMaterials(
                mockRequest as Request,
                mockResponse as Response
            );

            expect(statusSpy).toHaveBeenCalledWith(400);
            expect(jsonSpy).toHaveBeenCalledWith({
                message: 'You must provide a userId identifier',
            });
            expect(studyService.getSuggestedMaterials).not.toHaveBeenCalled();
        });

        // Input: Valid request but service operation fails
        // Expected status code: 500
        // Expected behavior: StudyService.getSuggestedMaterials throws error
        // Expected output: Error message
        it('should return 500 when service operation fails', async () => {
            mockRequest = {
                query: { userId: 'error-user' },
            };

            const originalConsoleError = console.error;
            console.error = jest.fn();

            await getSuggestedMaterials(
                mockRequest as Request,
                mockResponse as Response
            );

            console.error = originalConsoleError;

            expect(statusSpy).toHaveBeenCalledWith(500);
            expect(jsonSpy).toHaveBeenCalledWith({
                message: 'Internal server error',
            });
        });
    });
});
