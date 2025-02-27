import { Schema, model, Document as MongooseDocument } from 'mongoose';

export interface IDocument extends MongooseDocument {
    name: string;
    userId: string;
    uploadDate: string;
    s3Path: string;
    embeddingsId: string;
    activityGenerationComplete: boolean;
}

const documentSchema = new Schema<IDocument>({
    name: { type: String, required: true },
    userId: { type: String, required: true },
    uploadDate: { type: String, required: true },
    s3Path: { type: String, required: true, unique: true },
    embeddingsId: { type: String, required: true },
    activityGenerationComplete: { type: Boolean, required: true, default: false}
});

const Document = model<IDocument>('Document', documentSchema);

export default Document;
