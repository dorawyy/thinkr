import { Router } from 'express';
import {
    retrieveFlashcards,
    retrieveQuizzes,
    getSuggestedMaterials,
} from '../controllers/studyController';
import asyncHandler from '../utils/asyncHandler';

const router = Router();

router.get('/flashcards', asyncHandler(retrieveFlashcards));
router.get('/quiz', asyncHandler(retrieveQuizzes));
router.get('/suggestedMaterials', asyncHandler(getSuggestedMaterials));

export default router;
