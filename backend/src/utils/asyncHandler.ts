import { Request, Response, NextFunction } from 'express';

const asyncHandler = (
    fn: (req: Request, res: Response, next: NextFunction) => Promise<any>
) => {
    return (_req: Request, _res: Response, _next: NextFunction): void => {
        Promise.resolve(fn(_req, _res, _next)).catch(_next);
    };
};

export default asyncHandler;
