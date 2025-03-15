import { Router } from 'express';
import {
    getSubscriptionStatus,
    subscribe,
    unsubscribe,
} from '../controllers/subscriptionController';
import asyncHandler from '../utils/asyncHandler';

const router = Router();

router.post('/', asyncHandler(subscribe));
router.delete('/', asyncHandler(unsubscribe));
router.get('/', asyncHandler(getSubscriptionStatus));
export default router;
