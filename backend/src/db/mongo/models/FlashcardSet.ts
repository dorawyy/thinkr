import { Schema, model, Document } from 'mongoose';
import { FlashCard } from '../../../interfaces';

export interface IFlashcardSet extends Document {
    userId: string;
    documentId: string;
    flashcards: FlashCard[];
}

const flashcardSetSchema = new Schema<IFlashcardSet>({
    userId: { type: String, required: true },
    documentId: { type: String, required: true, trim: true },
    flashcards: [
        {
            front: { type: String, required: true, trim: true },
            back: { type: String, required: true, trim: true },
        },
    ],
});

const FlashcardSet = model<IFlashcardSet>('FlashcardSet', flashcardSetSchema);

export default FlashcardSet;
