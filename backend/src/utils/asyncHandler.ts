import { Request, Response, NextFunction } from 'express';

const asyncHandler = (
    fn: (
        req: Request,
        res: Response,
        next: NextFunction
    ) => Promise<void | Response>
) => {
    return (req: Request, res: Response, next: NextFunction): void => {
        Promise.resolve(fn(req, res, next)).catch(next);
    };
};

export default asyncHandler;
