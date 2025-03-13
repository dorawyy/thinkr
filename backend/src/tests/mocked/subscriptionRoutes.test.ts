import request from 'supertest';
import express from 'express';
import { Result, UserDTO } from '../../interfaces';

// Mock SubscriptionService
jest.mock('../../services/subscriptionService', () => {
  const mockUpdateAndGetSubscriberStatus = jest.fn();
  const mockGetSubscriberStatus = jest.fn();

  return {
    __esModule: true,
    mockUpdateAndGetSubscriberStatus,
    mockGetSubscriberStatus,
    default: {
      updateAndGetSubscriberStatus: mockUpdateAndGetSubscriberStatus,
      getSubscriberStatus: mockGetSubscriberStatus,
    },
  };
});

// Import after mocks are defined
import subscriptionRouter from '../../routes/subscriptionRoutes';
const { 
  mockUpdateAndGetSubscriberStatus, 
  mockGetSubscriberStatus 
} = require('../../services/subscriptionService');

// Create express app just for testing
const app = express();
app.use(express.json());
app.use('/', subscriptionRouter);

describe('Subscription Routes (Mocked)', () => {
  afterEach(() => {
    jest.clearAllMocks();
  });

  // Subscribe Tests
  describe('POST /', () => {
    // Input: Valid userId
    // Expected status code: 200
    // Expected behavior: SubscriptionService.updateAndGetSubscriberStatus called with true
    // Expected output: updated user object
    it('should subscribe a user successfully', async () => {
      const mockUser: UserDTO = {
        email: 'user@example.com',
        name: 'Test User',
        googleId: 'user123',
        subscribed: true,
      };

      mockUpdateAndGetSubscriberStatus.mockResolvedValue(mockUser);

      const response = await request(app)
        .post('/')
        .send({ userId: 'user123' })
        .expect(200);

      const result = response.body as Result;
      expect(result.data).toEqual(mockUser);

      expect(mockUpdateAndGetSubscriberStatus).toHaveBeenCalledWith('user123', true);
    });

    // Input: Missing userId
    // Expected status code: 400
    // Expected behavior: validation error, no service calls
    // Expected output: error message
    it('should return 400 when userId is missing', async () => {
      await request(app)
        .post('/')
        .send({})
        .expect(400);

      expect(mockUpdateAndGetSubscriberStatus).not.toHaveBeenCalled();
    });

    // Input: Valid request but service throws error
    // Expected status code: 500
    // Expected behavior: SubscriptionService.updateAndGetSubscriberStatus called but throws error
    // Expected output: error message
    it('should return 500 when service throws error', async () => {
      mockUpdateAndGetSubscriberStatus.mockRejectedValue(new Error('Service error'));

      const response = await request(app)
        .post('/')
        .send({ userId: 'user123' })
        .expect(500);

      expect(response.body.message).toBe('Internal server error');
    });
  });

  // Unsubscribe Tests
  describe('DELETE /', () => {
    // Input: Valid userId
    // Expected status code: 200
    // Expected behavior: SubscriptionService.updateAndGetSubscriberStatus called with false
    // Expected output: updated user object
    it('should unsubscribe a user successfully', async () => {
      const mockUser: UserDTO = {
        email: 'user@example.com',
        name: 'Test User',
        googleId: 'user123',
        subscribed: false,
      };

      mockUpdateAndGetSubscriberStatus.mockResolvedValue(mockUser);

      const response = await request(app)
        .delete('/')
        .query({ userId: 'user123' })
        .expect(200);

      const result = response.body as Result;
      expect(result.data).toEqual(mockUser);

      expect(mockUpdateAndGetSubscriberStatus).toHaveBeenCalledWith('user123', false);
    });

    // Input: Missing userId
    // Expected status code: 400
    // Expected behavior: validation error, no service calls
    // Expected output: error message
    it('should return 400 when userId is missing', async () => {
      await request(app)
        .delete('/')
        .expect(400);

      expect(mockUpdateAndGetSubscriberStatus).not.toHaveBeenCalled();
    });

    // Input: Valid request but service throws error
    // Expected status code: 500
    // Expected behavior: SubscriptionService.updateAndGetSubscriberStatus called but throws error
    // Expected output: error message
    it('should return 500 when service throws error', async () => {
      mockUpdateAndGetSubscriberStatus.mockRejectedValue(new Error('Service error'));

      const response = await request(app)
        .delete('/')
        .query({ userId: 'user123' })
        .expect(500);

      expect(response.body.message).toBe('Internal server error');
    });
  });

  // Get Subscription Status Tests
  describe('GET /', () => {
    // Input: Valid userId
    // Expected status code: 200
    // Expected behavior: SubscriptionService.getSubscriberStatus called
    // Expected output: user object with subscription status
    it('should get subscription status for a user', async () => {
      const mockUser: UserDTO = {
        email: 'user@example.com',
        name: 'Test User',
        googleId: 'user123',
        subscribed: true,
      };

      mockGetSubscriberStatus.mockResolvedValue(mockUser);

      const response = await request(app)
        .get('/')
        .query({ userId: 'user123' })
        .expect(200);

      const result = response.body as Result;
      expect(result.data).toEqual(mockUser);

      expect(mockGetSubscriberStatus).toHaveBeenCalledWith('user123');
    });

    // Input: Missing userId
    // Expected status code: 400
    // Expected behavior: validation error, no service calls
    // Expected output: error message
    it('should return 400 when userId is missing', async () => {
      await request(app)
        .get('/')
        .expect(400);

      expect(mockGetSubscriberStatus).not.toHaveBeenCalled();
    });

    // Input: Valid request but service throws error
    // Expected status code: 500
    // Expected behavior: SubscriptionService.getSubscriberStatus called but throws error
    // Expected output: error message
    it('should return 500 when service throws error', async () => {
      mockGetSubscriberStatus.mockRejectedValue(new Error('Service error'));

      const response = await request(app)
        .get('/')
        .query({ userId: 'user123' })
        .expect(500);

      expect(response.body.message).toBe('Internal server error');
    });
  });
}); 