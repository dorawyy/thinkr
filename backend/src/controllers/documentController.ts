import { Request, Response } from 'express';
import DocumentService from '../services/documentService';
import { Result } from '../interfaces';
import StudyService from '../services/studyService';

/**
 * Handles document uploads
 */
export const uploadDocuments = async (
    req: Request,
    res: Response
): Promise<void> => {
    const { userId, documentName, public: isPublic } = req.body;

    if (!userId || !req.file || !documentName) {
        res.status(400).json({
            message: 'Bad Request, missing userId or documentName or file',
        } as Result);
        return;
    }

    try {
        const file = req.file as Express.Multer.File;
        const docs = await DocumentService.uploadDocument(
            file,
            userId as string,
            documentName as string,
            isPublic === 'true'
        );

        res.status(200).json({
            data: { docs },
        } as Result);

        // generate activities as a background job
        void StudyService.generateStudyActivities(
            docs.documentId,
            userId as string
        );
    } catch (error) {
        console.error('Error uploading documents:', error);

        res.status(500).json({
            message: 'Failed to upload documents',
        } as Result);
    }
};

/**
 * Handles deleting documents
 */
export const deleteDocument = async (
    req: Request,
    res: Response
): Promise<void> => {
    const userId = req.query.userId as string;
    const documentId = req.query.documentId as string;

    if (!userId || !documentId) {
        res.status(400).json({
            message: 'Bad Request, userId and documentId are required',
        } as Result);
        return;
    }

    try {
        await DocumentService.deleteDocument(`${userId}-${documentId}`);
        await StudyService.deleteStudyActivities(documentId, userId);

        res.status(200).json();
        return;
    } catch (error) {
        console.error('Error deleting documents:', error);

        res.status(500).json({
            message: 'Failed to delete documents',
        } as Result);
    }
};

/**
 * Handles retrieving documents
 */
export const getDocuments = async (
    req: Request,
    res: Response
): Promise<void> => {
    const userId = req.query.userId as string;
    const documentId = req.query.documentId as string;

    if (!userId) {
        res.status(400).json({
            message: 'Bad Request, a userId is required',
        } as Result);
        return;
    }

    try {
        const docs = documentId
            ? [await DocumentService.getDocument(documentId, userId)]
            : await DocumentService.getDocuments(userId);

        const result = {
            data: { docs },
        } as Result;
        res.status(200).json(result);
    } catch (error) {
        console.error('Error retrieving documents:', error);
        const result: Result = {
            message: 'Failed to retrieve documents',
        };
        res.status(500).json(result);
    }
};
