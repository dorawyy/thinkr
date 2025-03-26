import request from 'supertest';
import express from 'express';

jest.mock('../../services/studyService', () => {
    const mockRetrieveFlashcards = jest.fn();
    const mockRetrieveQuizzes = jest.fn();
    const mockGetSuggestedMaterials = jest.fn();

    return {
        __esModule: true,
        mockRetrieveFlashcards,
        mockRetrieveQuizzes,
        mockGetSuggestedMaterials,
        default: {
            retrieveFlashcards: mockRetrieveFlashcards,
            retrieveQuizzes: mockRetrieveQuizzes,
            getSuggestedMaterials: mockGetSuggestedMaterials,
        },
    };
});

import studyRouter from '../../routes/studyRoutes';
const {
    mockRetrieveFlashcards,
    mockRetrieveQuizzes,
    mockGetSuggestedMaterials,
} = require('../../services/studyService');

const app = express();
app.use(express.json());
app.use('/study', studyRouter);

describe('Study Routes (Mocked)', () => {
    afterEach(() => {
        jest.clearAllMocks();
    });

    describe('MOCKED GET /study/flashcards', () => {
        // Input: Missing userId
        // Expected status code: 400
        // Expected behavior: validation error, no service calls
        // Expected output: error message
        it('should return 400 when userId is missing', async () => {
            await request(app).get('/study/flashcards').expect(400);

            expect(mockRetrieveFlashcards).not.toHaveBeenCalled();
        });

        // Input: Valid request but service throws error
        // Expected status code: 500
        // Expected behavior: StudyService.retrieveFlashcards called but throws error
        // Expected output: error message
        it('should return 500 when service throws error', async () => {
            mockRetrieveFlashcards.mockRejectedValue(
                new Error('Service error')
            );

            const response = await request(app)
                .get('/study/flashcards')
                .query({ userId: 'user123' })
                .expect(500);

            expect(response.body.message).toBe('Internal server error');
        });
    });

    describe('MOCKED GET /study/quiz', () => {
        // Input: Missing userId
        // Expected status code: 400
        // Expected behavior: validation error, no service calls
        // Expected output: error message
        it('should return 400 when userId is missing', async () => {
            await request(app).get('/study/quiz').expect(400);

            expect(mockRetrieveQuizzes).not.toHaveBeenCalled();
        });

        // Input: Valid request but service throws error
        // Expected status code: 500
        // Expected behavior: StudyService.retrieveQuizzes called but throws error
        // Expected output: error message
        it('should return 500 when service throws error', async () => {
            mockRetrieveQuizzes.mockRejectedValue(new Error('Service error'));

            const response = await request(app)
                .get('/study/quiz')
                .query({ userId: 'user123' })
                .expect(500);

            expect(response.body.message).toBe('Internal server error');
        });
    });

    describe('MOCKED GET /study/suggestedMaterials', () => {
        // Input: Valid userId with custom limit
        // Expected status code: 200
        // Expected behavior: StudyService.getSuggestedMaterials called with custom limit
        // Expected output: suggested materials object
        it('should respect the limit parameter', async () => {
            const mockSuggestedMaterials = {
                flashcards: [
                    {
                        userId: 'other-user',
                        documentId: 'doc3',
                        documentName: 'Document 3',
                        flashcards: [{ front: 'Term 5', back: 'Definition 5' }],
                    },
                ],
                quizzes: [
                    {
                        userId: 'other-user',
                        documentId: 'doc3',
                        documentName: 'Document 3',
                        quiz: [
                            {
                                question: 'Question 3?',
                                answer: 'C',
                                options: {
                                    A: 'Option A',
                                    B: 'Option B',
                                    C: 'Option C',
                                    D: 'Option D',
                                },
                            },
                        ],
                    },
                ],
            };

            mockGetSuggestedMaterials.mockResolvedValue(mockSuggestedMaterials);

            await request(app)
                .get('/study/suggestedMaterials')
                .query({ userId: 'user123', limit: '10' })
                .expect(200);

            expect(mockGetSuggestedMaterials).toHaveBeenCalledWith(
                'user123',
                10
            );
        });

        // Input: Missing userId
        // Expected status code: 400
        // Expected behavior: validation error, no service calls
        // Expected output: error message
        it('should return 400 when userId is missing', async () => {
            await request(app).get('/study/suggestedMaterials').expect(400);

            expect(mockGetSuggestedMaterials).not.toHaveBeenCalled();
        });

        // Input: Valid request but service throws error
        // Expected status code: 500
        // Expected behavior: StudyService.getSuggestedMaterials called but throws error
        // Expected output: error message
        it('should return 500 when service throws error', async () => {
            mockGetSuggestedMaterials.mockRejectedValue(
                new Error('Service error')
            );

            const response = await request(app)
                .get('/study/suggestedMaterials')
                .query({ userId: 'user123' })
                .expect(500);

            expect(response.body.message).toBe('Internal server error');
        });
    });
});
