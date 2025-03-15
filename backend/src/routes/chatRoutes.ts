import { Router } from 'express';
import {
    getUserChat,
    sendMessage,
    clearChatHistory,
} from '../controllers/chatController';
import asyncHandler from '../utils/asyncHandler';

const router = Router();

// Get or create a user's chat
router.get('/', asyncHandler(getUserChat));

// Send a message to the user's chat
router.post('/message', asyncHandler(sendMessage));

// Clear chat history
router.delete('/history', asyncHandler(clearChatHistory));

export default router;
