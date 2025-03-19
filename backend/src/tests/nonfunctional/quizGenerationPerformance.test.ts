import axios from 'axios';
import dotenv from 'dotenv';
import * as fs from 'fs';
import * as path from 'path';
import FormData from 'form-data';

dotenv.config();

const API_URL = process.env.PRODUCTION_URL;
const MAX_ALLOWED_TIME = 11.3;
const POLL_INTERVAL = 500;

/**
 * @group nfr
 */
describe('NFR Quiz/Flashcard Generation Performance Test', () => {
    jest.setTimeout(20000);

    // Upload a test document and return the document ID
    async function uploadTestDocument(
        userId: string,
        documentName: string,
        context: string
    ): Promise<string> {
        const testFilePath = path.resolve(__dirname, './testDoc.pdf');
        const formData = new FormData();
        formData.append('document', fs.createReadStream(testFilePath));
        formData.append('userId', userId);
        formData.append('documentName', documentName);
        formData.append('context', context);

        // Manually create headers to bypass the linter issue
        const boundary = formData.getBoundary();
        const headers = {
            'Content-Type': `multipart/form-data; boundary=${boundary}`,
        };

        const uploadResponse = await axios.post(
            `${API_URL}/document/upload`,
            formData,
            { headers }
        );

        expect(uploadResponse.status).toBe(200);
        return uploadResponse.data.data.docs.documentId;
    }

    // Wait for document processing to complete
    async function waitForDocumentProcessing(
        userId: string,
        documentId: string
    ): Promise<void> {
        let isGenerationComplete = false;
        while (!isGenerationComplete) {
            const retrieveResponse = await axios.get(
                `${API_URL}/document/retrieve`,
                {
                    params: {
                        userId: userId,
                        documentId: documentId,
                    },
                }
            );

            expect(retrieveResponse.status).toBe(200);

            if (retrieveResponse.data.data.docs.activityGenerationComplete) {
                isGenerationComplete = true;
            } else {
                await new Promise((resolve) =>
                    setTimeout(resolve, POLL_INTERVAL)
                );
            }
        }
    }

    // Delete a test document
    async function cleanupTestDocument(
        userId: string,
        documentId: string
    ): Promise<void> {
        await axios.delete(`${API_URL}/document/delete`, {
            params: {
                userId: userId,
                documentId: documentId,
            },
        });
    }

    it(`should generate quiz/flashcards in less than ${MAX_ALLOWED_TIME} seconds`, async () => {
        const userId = '321';
        const documentName = 'Test Document';
        const context = 'This is a test document for performance testing';
        const startTime = Date.now();
        let documentId;

        try {
            // Upload document and get document ID
            documentId = await uploadTestDocument(
                userId,
                documentName,
                context
            );

            // Wait for document processing to complete
            await waitForDocumentProcessing(userId, documentId);

            // Calculate and log performance metrics
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

            expect(duration).toBeLessThanOrEqual(MAX_ALLOWED_TIME);

            // Cleanup: Delete the test document
            await cleanupTestDocument(userId, documentId);
        } catch (error) {
            console.error(
                'Error during quiz generation test:',
                error instanceof Error ? error.message : 'Unknown error'
            );

            // Attempt cleanup even if test fails
            if (documentId) {
                try {
                    await cleanupTestDocument(userId, documentId);
                } catch (cleanupError) {
                    console.error(
                        'Error during test cleanup:',
                        cleanupError instanceof Error
                            ? cleanupError.message
                            : 'Unknown error'
                    );
                }
            }

            throw error;
        }
    });
});
