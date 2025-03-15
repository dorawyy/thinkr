import axios from 'axios';
import dotenv from 'dotenv';

dotenv.config();

const API_URL = process.env.PRODUCTION_URL;
const MAX_ALLOWED_TIME = 11.3;

/**
 * @group nfr
 */
describe('NFR Quiz/Flashcard Generation Performance Test', () => {
    jest.setTimeout(15000);

    it(`should generate quiz/flashcards in less than ${MAX_ALLOWED_TIME} seconds`, async () => {
        const userId = '321';
        const documentId = 'test_doc';
        const startTime = Date.now();

        try {
            const response = await axios.get(`${API_URL}/study/quiz`, {
                params: {
                    userId: userId,
                    documentId: documentId,
                },
            });

            const endTime = Date.now();
            const duration = (endTime - startTime) / 1000;

            console.log(
                `\nPERFORMANCE SUMMARY: Quiz/Flashcard Generation
           -----------------------------------------------
           ✧ Response Time:      ${duration.toFixed(2)} seconds
           ✧ Threshold:          ${MAX_ALLOWED_TIME} seconds
           ✧ Performance Margin: ${(MAX_ALLOWED_TIME - duration).toFixed(2)} seconds
           ✧ Status:             ${duration <= MAX_ALLOWED_TIME ? 'PASSED ✅' : 'FAILED ❌'}
           -----------------------------------------------`
            );

            const statusCode = response.status;

            expect(statusCode).toBe(200);
            expect(duration).toBeLessThanOrEqual(MAX_ALLOWED_TIME);
        } catch (error) {
            console.error(
                'Error during quiz generation test:',
                error instanceof Error ? error.message : 'Unknown error'
            );
            throw error;
        }
    });
});
