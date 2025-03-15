import mongoose from 'mongoose';
import dotenv from 'dotenv';
import { getEnvVariable } from '../../config/env';

dotenv.config();

const MONGO_URI = getEnvVariable('MONGO_URI');

const connectMongoDB = async () => {
    try {
        await mongoose.connect(MONGO_URI);
        console.log('MongoDB connected successfully');
    } catch (error) {
        console.error('MongoDB connection error:', error);
        process.exit(1);
    }
};

export default connectMongoDB;
