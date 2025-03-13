import request from 'supertest';
import express from 'express';
import { FlashCardDTO, QuizDTO } from '../../interfaces';
import { TestResult } from './testInterfaces';
import studyRouter from '../../routes/studyRoutes';
import { mockFlashcardSet, mockQuizSet, testApp as baseTestApp } from './setupIntegration';

// Create express app just for testing
const testApp = express();
testApp.use(express.json());
testApp.use('/', studyRouter); // Mount at root level

// Longer timeout for tests
jest.setTimeout(30000);

// Mock studyService
jest.mock('../../services/studyService', () => {
  return {
    __esModule: true,
    default: {
      retrieveFlashcards: jest.fn().mockImplementation(async (userId, documentId) => {
        if (documentId) {
          return {
            userId,
            documentId,
            flashcards: [
              { front: 'Term 1', back: 'Definition 1' },
              { front: 'Term 2', back: 'Definition 2' },
            ],
          };
        } else {
          return [
            {
              userId,
              documentId: 'doc1',
              flashcards: [
                { front: 'Term 1', back: 'Definition 1' },
                { front: 'Term 2', back: 'Definition 2' },
              ],
            },
          ];
        }
      }),
      retrieveQuizzes: jest.fn().mockImplementation(async (userId, documentId) => {
        if (documentId) {
          return {
            userId,
            documentId,
            quiz: [
              {
                question: 'Question 1?',
                answer: 'A',
                options: { A: 'Option A', B: 'Option B', C: 'Option C', D: 'Option D' },
              },
            ],
          };
        } else {
          return [
            {
              userId,
              documentId: 'doc1',
              quiz: [
                {
                  question: 'Question 1?',
                  answer: 'A',
                  options: { A: 'Option A', B: 'Option B', C: 'Option C', D: 'Option D' },
                },
              ],
            },
          ];
        }
      }),
      getSuggestedMaterials: jest.fn().mockResolvedValue({
        flashcards: [
          {
            userId: 'test-user',
            documentId: 'doc2',
            flashcards: [{ front: 'Term 3', back: 'Definition 3' }],
          },
        ],
        quizzes: [
          {
            userId: 'test-user',
            documentId: 'doc2',
            quiz: [
              {
                question: 'Question 2?',
                answer: 'B',
                options: { A: 'Option A', B: 'Option B', C: 'Option C', D: 'Option D' },
              },
            ],
          },
        ],
      }),
    },
  };
});

describe('Study Routes Integration (Happy Path)', () => {
  beforeEach(() => {
    // Reset mocks
    jest.clearAllMocks();
  });

  // Test for retrieving flashcards
  it('should retrieve flashcards for a user', async () => {
    const response = await request(testApp)
      .get('/flashcards')
      .query({ userId: 'test-user' })
      .expect(200);

    const result = response.body as TestResult;
    expect(result.success).toBe(true);
    expect(result.data).toBeInstanceOf(Array);
    expect(result.data.length).toBe(1);
    expect(result.data[0].userId).toBe('test-user');
    expect(result.data[0].documentId).toBe('doc1');
    expect(result.data[0].flashcards).toBeInstanceOf(Array);
    expect(result.data[0].flashcards.length).toBe(2);
    expect(result.data[0].flashcards[0].front).toBe('Term 1');
    expect(result.data[0].flashcards[0].back).toBe('Definition 1');
  });

  // Test for retrieving flashcards for a specific document
  it('should retrieve flashcards for a specific document', async () => {
    const response = await request(testApp)
      .get('/flashcards')
      .query({ userId: 'test-user', documentId: 'doc1' })
      .expect(200);

    const result = response.body as TestResult;
    expect(result.success).toBe(true);
    expect(result.data).toBeDefined();
    expect(result.data.userId).toBe('test-user');
    expect(result.data.documentId).toBe('doc1');
    expect(result.data.flashcards).toBeInstanceOf(Array);
    expect(result.data.flashcards.length).toBe(2);
  });

  // Test for retrieving quizzes
  it('should retrieve quizzes for a user', async () => {
    const response = await request(testApp)
      .get('/quiz')
      .query({ userId: 'test-user' })
      .expect(200);

    const result = response.body as TestResult;
    expect(result.success).toBe(true);
    expect(result.data).toBeInstanceOf(Array);
    expect(result.data.length).toBe(1);
    expect(result.data[0].userId).toBe('test-user');
    expect(result.data[0].documentId).toBe('doc1');
    expect(result.data[0].quiz).toBeInstanceOf(Array);
    expect(result.data[0].quiz.length).toBe(1);
    expect(result.data[0].quiz[0].question).toBe('Question 1?');
    expect(result.data[0].quiz[0].answer).toBe('A');
  });

  // Test for retrieving quizzes for a specific document
  it('should retrieve quizzes for a specific document', async () => {
    const response = await request(testApp)
      .get('/quiz')
      .query({ userId: 'test-user', documentId: 'doc1' })
      .expect(200);

    const result = response.body as TestResult;
    expect(result.success).toBe(true);
    expect(result.data).toBeDefined();
    expect(result.data.userId).toBe('test-user');
    expect(result.data.documentId).toBe('doc1');
    expect(result.data.quiz).toBeInstanceOf(Array);
    expect(result.data.quiz.length).toBe(1);
  });

  // Test for retrieving suggested materials
  it('should retrieve suggested materials for a user', async () => {
    const response = await request(testApp)
      .get('/suggestedMaterials')
      .query({ userId: 'test-user' })
      .expect(200);

    const result = response.body as TestResult;
    expect(result.success).toBe(true);
    expect(result.data).toBeDefined();
    expect(result.data.flashcards).toBeDefined();
    expect(result.data.quizzes).toBeDefined();
  });
}); 