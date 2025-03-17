import { Request, Response, NextFunction, RequestHandler } from 'express';

const asyncHandler = <
    P = unknown,
    ResBody = unknown,
    ReqBody = unknown,
    ReqQuery = unknown,
>(
    fn: (
        req: Request<P, ResBody, ReqBody, ReqQuery>,
        res: Response<ResBody>,
        next: NextFunction
    ) => Promise<unknown>
): RequestHandler<P, ResBody, ReqBody, ReqQuery> => {
    return (req, res, next): void => {
        Promise.resolve(fn(req, res, next)).catch((err: unknown) => {
            next(err);
        });
    };
};

export default asyncHandler;
