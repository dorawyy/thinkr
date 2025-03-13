import request from 'supertest';
import express from 'express';
import { AuthPayload } from '../../interfaces';
import { TestResult } from './testInterfaces';
import authRouter from '../../routes/userAuthRoutes';
import { mockUser, testApp as baseTestApp } from './setupIntegration';

// Create express app just for testing
const testApp = express();
testApp.use(express.json());
testApp.use('/', authRouter); // Mount at root for testing

// Longer timeout for tests
jest.setTimeout(10000);

describe('User Auth Routes Integration (Happy Path)', () => {
  // Test for creating a new user
  it('should create a new user when none exists', async () => {
    const payload: AuthPayload = {
      googleId: 'new-google-id',
      name: 'New User',
      email: 'new@example.com',
    };
    
    // Mock user not found, then created successfully
    mockUser.findOne.mockResolvedValue(null);
    mockUser.create.mockResolvedValue({
      googleId: 'new-google-id',
      name: 'New User',
      email: 'new@example.com',
      subscribed: false,
    });

    const response = await request(testApp)
      .post('/login')
      .send(payload)
      .expect(200);

    const result = response.body as TestResult;
    expect(result.success).toBe(true);
    expect(result.data).toBeDefined();
    expect(result.data.user).toBeDefined();
    expect(result.data.user.googleId).toBe('new-google-id');
    expect(result.data.user.name).toBe('New User');
    expect(result.data.user.email).toBe('new@example.com');
    expect(result.data.user.subscribed).toBe(false);

    // Verify user creation was called
    expect(mockUser.create).toHaveBeenCalled();
  });

  // Test for logging in an existing user
  it('should return existing user when found', async () => {
    const existingUser = {
      googleId: 'existing-google-id',
      name: 'Existing User',
      email: 'existing@example.com',
      subscribed: true,
    };
    
    // Mock user found
    mockUser.findOne.mockResolvedValue(existingUser);
    mockUser.countDocuments.mockResolvedValue(1);

    const payload: AuthPayload = {
      googleId: 'existing-google-id',
      name: 'Existing User',
      email: 'existing@example.com',
    };

    const response = await request(testApp)
      .post('/login')
      .send(payload)
      .expect(200);

    const result = response.body as TestResult;
    expect(result.success).toBe(true);
    expect(result.data).toBeDefined();
    expect(result.data.user).toBeDefined();
    expect(result.data.user.googleId).toBe('existing-google-id');
    expect(result.data.user.name).toBe('Existing User');
    expect(result.data.user.email).toBe('existing@example.com');
    expect(result.data.user.subscribed).toBe(true);

    // Verify user was looked up but not created
    expect(mockUser.findOne).toHaveBeenCalled();
    expect(mockUser.create).not.toHaveBeenCalled();
  });
}); 