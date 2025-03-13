import { Request, Response } from 'express';
import { subscribe, unsubscribe, getSubscriptionStatus } from '../../controllers/subscriptionController';
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
      updateOne: mockUpdateOne
    }
  };
});

// Import mocks after they're defined
const User = require('../../db/mongo/models/User').default;

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
      json: jsonSpy
    };
  });

  describe('subscribe', () => {
    it('should subscribe a user successfully', async () => {
      // Mock data
      const userId = 'test-user-123';
      const mockUser = {
        email: 'test@example.com',
        name: 'Test User',
        googleId: userId,
        subscribed: false
      };

      // Setup request
      mockRequest = {
        body: { userId }
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
          subscribed: true
        }
      });
    });

    it('should return 400 when userId is missing', async () => {
      // Setup request with missing userId
      mockRequest = {
        body: {}
      };

      // Call controller
      await subscribe(mockRequest as Request, mockResponse as Response);

      // Assertions
      expect(statusSpy).toHaveBeenCalledWith(400);
      expect(jsonSpy).toHaveBeenCalledWith({
        message: 'You must provide a userId identifier'
      });
      expect(User.findOne).not.toHaveBeenCalled();
      expect(User.updateOne).not.toHaveBeenCalled();
    });

    it('should return 500 when database operation fails', async () => {
      // Mock data
      const userId = 'test-user-123';

      // Setup request
      mockRequest = {
        body: { userId }
      };

      // Setup mock to throw error
      User.findOne.mockRejectedValue(new Error('Database error'));

      // Call controller
      await subscribe(mockRequest as Request, mockResponse as Response);

      // Assertions
      expect(statusSpy).toHaveBeenCalledWith(500);
      expect(jsonSpy).toHaveBeenCalledWith({
        message: 'Internal server error'
      });
    });

    it('should return 500 when user is not found', async () => {
      // Mock data
      const userId = 'test-user-123';

      // Setup request
      mockRequest = {
        body: { userId }
      };

      // Setup mock to return null (user not found)
      User.findOne.mockResolvedValue(null);

      // Call controller
      await subscribe(mockRequest as Request, mockResponse as Response);

      // Assertions
      expect(statusSpy).toHaveBeenCalledWith(500);
      expect(jsonSpy).toHaveBeenCalledWith({
        message: 'Internal server error'
      });
    });
  });

  describe('unsubscribe', () => {
    it('should unsubscribe a user successfully', async () => {
      // Mock data
      const userId = 'test-user-123';
      const mockUser = {
        email: 'test@example.com',
        name: 'Test User',
        googleId: userId,
        subscribed: true
      };

      // Setup request
      mockRequest = {
        query: { userId }
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
          subscribed: false
        }
      });
    });

    it('should return 400 when userId is missing', async () => {
      // Setup request with missing userId
      mockRequest = {
        query: {}
      };

      // Call controller
      await unsubscribe(mockRequest as Request, mockResponse as Response);

      // Assertions
      expect(statusSpy).toHaveBeenCalledWith(400);
      expect(jsonSpy).toHaveBeenCalledWith({
        message: 'You must provide a userId identifier'
      });
      expect(User.findOne).not.toHaveBeenCalled();
      expect(User.updateOne).not.toHaveBeenCalled();
    });

    it('should return 500 when database operation fails', async () => {
      // Mock data
      const userId = 'test-user-123';

      // Setup request
      mockRequest = {
        query: { userId }
      };

      // Setup mock to throw error
      User.findOne.mockRejectedValue(new Error('Database error'));

      // Call controller
      await unsubscribe(mockRequest as Request, mockResponse as Response);

      // Assertions
      expect(statusSpy).toHaveBeenCalledWith(500);
      expect(jsonSpy).toHaveBeenCalledWith({
        message: 'Internal server error'
      });
    });
  });

  describe('getSubscriptionStatus', () => {
    it('should get subscription status successfully', async () => {
      // Mock data
      const userId = 'test-user-123';
      const mockUser = {
        email: 'test@example.com',
        name: 'Test User',
        googleId: userId,
        subscribed: true
      };

      // Setup request
      mockRequest = {
        query: { userId }
      };

      // Setup mocks
      User.findOne.mockResolvedValue(mockUser);

      // Call controller
      await getSubscriptionStatus(mockRequest as Request, mockResponse as Response);

      // Assertions
      expect(User.findOne).toHaveBeenCalledWith({ googleId: userId });
      expect(statusSpy).toHaveBeenCalledWith(200);
      expect(jsonSpy).toHaveBeenCalledWith({
        data: {
          email: mockUser.email,
          name: mockUser.name,
          googleId: mockUser.googleId,
          subscribed: mockUser.subscribed
        }
      });
    });

    it('should return 400 when userId is missing', async () => {
      // Setup request with missing userId
      mockRequest = {
        query: {}
      };

      // Call controller
      await getSubscriptionStatus(mockRequest as Request, mockResponse as Response);

      // Assertions
      expect(statusSpy).toHaveBeenCalledWith(400);
      expect(jsonSpy).toHaveBeenCalledWith({
        message: 'You must provide a userId identifier'
      });
      expect(User.findOne).not.toHaveBeenCalled();
    });

    it('should return 500 when database operation fails', async () => {
      // Mock data
      const userId = 'test-user-123';

      // Setup request
      mockRequest = {
        query: { userId }
      };

      // Setup mock to throw error
      User.findOne.mockRejectedValue(new Error('Database error'));

      // Call controller
      await getSubscriptionStatus(mockRequest as Request, mockResponse as Response);

      // Assertions
      expect(statusSpy).toHaveBeenCalledWith(500);
      expect(jsonSpy).toHaveBeenCalledWith({
        message: 'Internal server error'
      });
    });

    it('should return 500 when user is not found', async () => {
      // Mock data
      const userId = 'test-user-123';

      // Setup request
      mockRequest = {
        query: { userId }
      };

      // Setup mock to return null (user not found)
      User.findOne.mockResolvedValue(null);

      // Call controller
      await getSubscriptionStatus(mockRequest as Request, mockResponse as Response);

      // Assertions
      expect(statusSpy).toHaveBeenCalledWith(500);
      expect(jsonSpy).toHaveBeenCalledWith({
        message: 'Internal server error'
      });
    });
  });
});