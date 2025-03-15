import { RequestHandler, Router } from 'express';
import {
    deleteDocument,
    getDocuments,
    uploadDocuments,
} from '../controllers/documentController';
import multer from 'multer';
import asyncHandler from '../utils/asyncHandler';

const upload = multer();
const router = Router();

router.post(
    '/upload',
    upload.single('document'),
    (asyncHandler(uploadDocuments) as RequestHandler)
);
router.delete('/delete', asyncHandler(deleteDocument));
router.get('/retrieve', asyncHandler(getDocuments));

export default router;
