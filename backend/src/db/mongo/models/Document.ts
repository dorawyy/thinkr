import { Schema, model, Document as MongooseDocument } from 'mongoose';

export interface IDocument extends MongooseDocument {
    name: string;
    documentId: string;
    userId: string;
    uploadDate: string;
    s3documentId: string;
    activityGenerationComplete: boolean;
    public: boolean;
}

const documentSchema = new Schema<IDocument>({
    name: { type: String, required: true },
    userId: { type: String, required: true },
    uploadDate: { type: String, required: true },
    s3documentId: { type: String, required: true, unique: true },
    documentId: { type: String, required: true },
    activityGenerationComplete: {
        type: Boolean,
        required: true,
        default: false,
    },
    public: {
        type: Boolean,
        default: false,
    },
});

const Document = model<IDocument>('Document', documentSchema);

export default Document;
