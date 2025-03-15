import request from 'supertest';
import express from 'express';

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

import subscriptionRouter from '../../routes/subscriptionRoutes';
const {
    mockUpdateAndGetSubscriberStatus,
    mockGetSubscriberStatus,
} = require('../../services/subscriptionService');

const app = express();
app.use(express.json());
app.use('/', subscriptionRouter);

describe('Subscription Routes (Mocked)', () => {
    afterEach(() => {
        jest.clearAllMocks();
    });

    describe('POST /', () => {
        // Input: Missing userId
        // Expected status code: 400
        // Expected behavior: validation error, no service calls
        // Expected output: error message
        it('should return 400 when userId is missing', async () => {
            await request(app).post('/').send({}).expect(400);

            expect(mockUpdateAndGetSubscriberStatus).not.toHaveBeenCalled();
        });

        // Input: Valid request but service throws error
        // Expected status code: 500
        // Expected behavior: SubscriptionService.updateAndGetSubscriberStatus called but throws error
        // Expected output: error message
        it('should return 500 when service throws error', async () => {
            mockUpdateAndGetSubscriberStatus.mockRejectedValue(
                new Error('Service error')
            );

            const response = await request(app)
                .post('/')
                .send({ userId: 'user123' })
                .expect(500);

            expect(response.body.message).toBe('Internal server error');
        });
    });

    describe('DELETE /', () => {
        // Input: Missing userId
        // Expected status code: 400
        // Expected behavior: validation error, no service calls
        // Expected output: error message
        it('should return 400 when userId is missing', async () => {
            await request(app).delete('/').expect(400);

            expect(mockUpdateAndGetSubscriberStatus).not.toHaveBeenCalled();
        });

        // Input: Valid request but service throws error
        // Expected status code: 500
        // Expected behavior: SubscriptionService.updateAndGetSubscriberStatus called but throws error
        // Expected output: error message
        it('should return 500 when service throws error', async () => {
            mockUpdateAndGetSubscriberStatus.mockRejectedValue(
                new Error('Service error')
            );

            const response = await request(app)
                .delete('/')
                .query({ userId: 'user123' })
                .expect(500);

            expect(response.body.message).toBe('Internal server error');
        });
    });

    describe('GET /', () => {
        // Input: Missing userId
        // Expected status code: 400
        // Expected behavior: validation error, no service calls
        // Expected output: error message
        it('should return 400 when userId is missing', async () => {
            await request(app).get('/').expect(400);

            expect(mockGetSubscriberStatus).not.toHaveBeenCalled();
        });

        // Input: Valid request but service throws error
        // Expected status code: 500
        // Expected behavior: SubscriptionService.getSubscriberStatus called but throws error
        // Expected output: error message
        it('should return 500 when service throws error', async () => {
            mockGetSubscriberStatus.mockRejectedValue(
                new Error('Service error')
            );

            const response = await request(app)
                .get('/')
                .query({ userId: 'user123' })
                .expect(500);

            expect(response.body.message).toBe('Internal server error');
        });
    });
});
