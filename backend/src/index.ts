import express from 'express';
import dotenv from 'dotenv';
import authRouter from './routes/userAuthRoutes';
import documentRouter from './routes/documentRoutes';
import studyRouter from './routes/studyRoutes';
import connectMongoDB from './db/mongo/connection';
import chatRouter from './routes/chatRoutes';
import subsriptionRouter from './routes/subscriptionRoutes';

dotenv.config();

const PORT = 3000;
const app = express();

app.use(express.json());
app.use('/auth', authRouter);
app.use('/document', documentRouter);
app.use('/study', studyRouter);
app.use('/chat', chatRouter);
app.use('/subscription', subsriptionRouter);

void connectMongoDB();

app.listen(PORT, '0.0.0.0', () => {
    console.log(`Server is running on http://localhost:${PORT}`);
});
