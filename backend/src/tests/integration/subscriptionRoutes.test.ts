import request from 'supertest';
import express from 'express';
import { UserDTO } from '../../interfaces';
import { TestResult } from './testInterfaces';
import subscriptionRouter from '../../routes/subscriptionRoutes';
import { mockUser, testApp as baseTestApp } from './setupIntegration';

// Create express app just for testing
const testApp = express();
testApp.use(express.json());
testApp.use('/', subscriptionRouter); // Mount at root for testing

// Longer timeout for tests
jest.setTimeout(10000);

describe('Subscription Routes Integration (Happy Path)', () => {
  beforeEach(() => {
    // Reset mocks
    jest.clearAllMocks();
  });

  // Test for subscribing a user
  it('should subscribe a user successfully', async () => {
    // Mock finding the user
    mockUser.findOne.mockResolvedValue({
      googleId: 'user-id-123',
      name: 'Test User',
      email: 'test@example.com',
      subscribed: false,
    });
    
    // Mock updating the user
    mockUser.updateOne.mockResolvedValue({ modifiedCount: 1 });

    const response = await request(testApp)
      .post('/')
      .send({ userId: 'user-id-123' })
      .expect(200);

    const result = response.body as TestResult;
    expect(result.success).toBe(true);
    expect(result.data).toBeDefined();
    expect(result.data.email).toBe('test@example.com');
    expect(result.data.name).toBe('Test User');
    expect(result.data.googleId).toBe('user-id-123');
    expect(result.data.subscribed).toBe(true);

    // Verify update was called
    expect(mockUser.updateOne).toHaveBeenCalledWith(
      { googleId: 'user-id-123' },
      { subscribed: true }
    );
  });

  // Test for unsubscribing a user
  it('should unsubscribe a user successfully', async () => {
    // Mock finding the user
    mockUser.findOne.mockResolvedValue({
      googleId: 'user-id-123',
      name: 'Test User',
      email: 'test@example.com',
      subscribed: true,
    });
    
    // Mock updating the user
    mockUser.updateOne.mockResolvedValue({ modifiedCount: 1 });

    const response = await request(testApp)
      .delete('/')
      .query({ userId: 'user-id-123' })
      .expect(200);

    const result = response.body as TestResult;
    expect(result.success).toBe(true);
    expect(result.data).toBeDefined();
    expect(result.data.subscribed).toBe(false);

    // Verify update was called
    expect(mockUser.updateOne).toHaveBeenCalledWith(
      { googleId: 'user-id-123' },
      { subscribed: false }
    );
  });

  // Test for getting subscription status
  it('should get subscription status for a user', async () => {
    // Mock finding the user
    mockUser.findOne.mockResolvedValue({
      googleId: 'user-id-123',
      name: 'Test User',
      email: 'test@example.com',
      subscribed: true,
    });

    const response = await request(testApp)
      .get('/')
      .query({ userId: 'user-id-123' })
      .expect(200);

    const result = response.body as TestResult;
    expect(result.success).toBe(true);
    expect(result.data).toBeDefined();
    expect(result.data.subscribed).toBe(true);
    
    // Verify find was called
    expect(mockUser.findOne).toHaveBeenCalledWith({ googleId: 'user-id-123' });
  });
}); 