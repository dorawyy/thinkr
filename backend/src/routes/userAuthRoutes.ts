import { Router } from 'express';
import { userAuthLogin } from '../controllers/userAuthController';
import asyncHandler from '../utils/asyncHandler';

const router = Router();

router.post('/login', asyncHandler(userAuthLogin));

export default router;
