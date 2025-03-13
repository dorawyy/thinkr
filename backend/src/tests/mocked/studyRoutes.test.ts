import request from 'supertest';
import express from 'express';
import { Result, FlashCardDTO, QuizDTO } from '../../interfaces';

// Mock StudyService
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

// Import after mocks are defined
import studyRouter from '../../routes/studyRoutes';
const { 
  mockRetrieveFlashcards, 
  mockRetrieveQuizzes, 
  mockGetSuggestedMaterials 
} = require('../../services/studyService');

// Create express app just for testing
const app = express();
app.use(express.json());
app.use('/study', studyRouter);

describe('Study Routes (Mocked)', () => {
  afterEach(() => {
    jest.clearAllMocks();
  });

  // Flashcards Tests
  describe('GET /study/flashcards', () => {
    // Input: Valid userId
    // Expected status code: 200
    // Expected behavior: StudyService.retrieveFlashcards called
    // Expected output: array of flashcard sets
    it('should retrieve all flashcards for a user', async () => {
      const mockFlashcards: FlashCardDTO[] = [
        {
          userId: 'user123',
          documentId: 'doc1',
          flashcards: [
            { front: 'Term 1', back: 'Definition 1' },
            { front: 'Term 2', back: 'Definition 2' },
          ],
        },
        {
          userId: 'user123',
          documentId: 'doc2',
          flashcards: [
            { front: 'Term 3', back: 'Definition 3' },
            { front: 'Term 4', back: 'Definition 4' },
          ],
        },
      ];

      mockRetrieveFlashcards.mockResolvedValue(mockFlashcards);

      const response = await request(app)
        .get('/study/flashcards')
        .query({ userId: 'user123' })
        .expect(200);

      const result = response.body as Result;
      expect(result.data).toEqual(mockFlashcards);

      expect(mockRetrieveFlashcards).toHaveBeenCalledWith(undefined, 'user123');
    });

    // Input: Valid userId and documentId
    // Expected status code: 200
    // Expected behavior: StudyService.retrieveFlashcards called with documentId
    // Expected output: single flashcard set
    it('should retrieve flashcards for a specific document', async () => {
      const mockFlashcard: FlashCardDTO = {
        userId: 'user123',
        documentId: 'doc1',
        flashcards: [
          { front: 'Term 1', back: 'Definition 1' },
          { front: 'Term 2', back: 'Definition 2' },
        ],
      };

      mockRetrieveFlashcards.mockResolvedValue(mockFlashcard);

      const response = await request(app)
        .get('/study/flashcards')
        .query({ userId: 'user123', documentId: 'doc1' })
        .expect(200);

      const result = response.body as Result;
      expect(result.data).toEqual(mockFlashcard);

      expect(mockRetrieveFlashcards).toHaveBeenCalledWith('doc1', 'user123');
    });

    // Input: Missing userId
    // Expected status code: 400
    // Expected behavior: validation error, no service calls
    // Expected output: error message
    it('should return 400 when userId is missing', async () => {
      await request(app)
        .get('/study/flashcards')
        .expect(400);

      expect(mockRetrieveFlashcards).not.toHaveBeenCalled();
    });

    // Input: Valid request but service throws error
    // Expected status code: 500
    // Expected behavior: StudyService.retrieveFlashcards called but throws error
    // Expected output: error message
    it('should return 500 when service throws error', async () => {
      mockRetrieveFlashcards.mockRejectedValue(new Error('Service error'));

      const response = await request(app)
        .get('/study/flashcards')
        .query({ userId: 'user123' })
        .expect(500);

      expect(response.body.message).toBe('Internal server error');
    });
  });

  // Quizzes Tests
  describe('GET /study/quiz', () => {
    // Input: Valid userId
    // Expected status code: 200
    // Expected behavior: StudyService.retrieveQuizzes called
    // Expected output: array of quiz sets
    it('should retrieve all quizzes for a user', async () => {
      const mockQuizzes: QuizDTO[] = [
        {
          userId: 'user123',
          documentId: 'doc1',
          quiz: [
            {
              question: 'Question 1?',
              answer: 'A',
              options: { A: 'Option A', B: 'Option B', C: 'Option C', D: 'Option D' },
            },
          ],
        },
        {
          userId: 'user123',
          documentId: 'doc2',
          quiz: [
            {
              question: 'Question 2?',
              answer: 'B',
              options: { A: 'Option A', B: 'Option B', C: 'Option C', D: 'Option D' },
            },
          ],
        },
      ];

      mockRetrieveQuizzes.mockResolvedValue(mockQuizzes);

      const response = await request(app)
        .get('/study/quiz')
        .query({ userId: 'user123' })
        .expect(200);

      const result = response.body as Result;
      expect(result.data).toEqual(mockQuizzes);

      expect(mockRetrieveQuizzes).toHaveBeenCalledWith(undefined, 'user123');
    });

    // Input: Valid userId and documentId
    // Expected status code: 200
    // Expected behavior: StudyService.retrieveQuizzes called with documentId
    // Expected output: single quiz set
    it('should retrieve quizzes for a specific document', async () => {
      const mockQuiz: QuizDTO = {
        userId: 'user123',
        documentId: 'doc1',
        quiz: [
          {
            question: 'Question 1?',
            answer: 'A',
            options: { A: 'Option A', B: 'Option B', C: 'Option C', D: 'Option D' },
          },
        ],
      };

      mockRetrieveQuizzes.mockResolvedValue(mockQuiz);

      const response = await request(app)
        .get('/study/quiz')
        .query({ userId: 'user123', documentId: 'doc1' })
        .expect(200);

      const result = response.body as Result;
      expect(result.data).toEqual(mockQuiz);

      expect(mockRetrieveQuizzes).toHaveBeenCalledWith('doc1', 'user123');
    });

    // Input: Missing userId
    // Expected status code: 400
    // Expected behavior: validation error, no service calls
    // Expected output: error message
    it('should return 400 when userId is missing', async () => {
      await request(app)
        .get('/study/quiz')
        .expect(400);

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

  // Suggested Materials Tests
  describe('GET /study/suggestedMaterials', () => {
    // Input: Valid userId
    // Expected status code: 200
    // Expected behavior: StudyService.getSuggestedMaterials called
    // Expected output: suggested materials object
    it('should retrieve suggested materials for a user', async () => {
      const mockSuggestedMaterials = {
        flashcards: [
          {
            userId: 'other-user',
            documentId: 'doc3',
            flashcards: [{ front: 'Term 5', back: 'Definition 5' }],
          },
        ],
        quizzes: [
          {
            userId: 'other-user',
            documentId: 'doc3',
            quiz: [
              {
                question: 'Question 3?',
                answer: 'C',
                options: { A: 'Option A', B: 'Option B', C: 'Option C', D: 'Option D' },
              },
            ],
          },
        ],
      };

      mockGetSuggestedMaterials.mockResolvedValue(mockSuggestedMaterials);

      const response = await request(app)
        .get('/study/suggestedMaterials')
        .query({ userId: 'user123' })
        .expect(200);

      const result = response.body as Result;
      expect(result.data).toEqual(mockSuggestedMaterials);

      expect(mockGetSuggestedMaterials).toHaveBeenCalledWith('user123', 5);
    });

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
            flashcards: [{ front: 'Term 5', back: 'Definition 5' }],
          },
        ],
        quizzes: [
          {
            userId: 'other-user',
            documentId: 'doc3',
            quiz: [
              {
                question: 'Question 3?',
                answer: 'C',
                options: { A: 'Option A', B: 'Option B', C: 'Option C', D: 'Option D' },
              },
            ],
          },
        ],
      };

      mockGetSuggestedMaterials.mockResolvedValue(mockSuggestedMaterials);

      const response = await request(app)
        .get('/study/suggestedMaterials')
        .query({ userId: 'user123', limit: '10' })
        .expect(200);

      expect(mockGetSuggestedMaterials).toHaveBeenCalledWith('user123', 10);
    });

    // Input: Missing userId
    // Expected status code: 400
    // Expected behavior: validation error, no service calls
    // Expected output: error message
    it('should return 400 when userId is missing', async () => {
      await request(app)
        .get('/study/suggestedMaterials')
        .expect(400);

      expect(mockGetSuggestedMaterials).not.toHaveBeenCalled();
    });

    // Input: Valid request but service throws error
    // Expected status code: 500
    // Expected behavior: StudyService.getSuggestedMaterials called but throws error
    // Expected output: error message
    it('should return 500 when service throws error', async () => {
      mockGetSuggestedMaterials.mockRejectedValue(new Error('Service error'));

      const response = await request(app)
        .get('/study/suggestedMaterials')
        .query({ userId: 'user123' })
        .expect(500);

      expect(response.body.message).toBe('Internal server error');
    });
  });
}); 