# Thinkr

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/9965e0b931bb4b45aed3afdc73313972)](https://app.codacy.com/gh/jaidensiu/thinkr/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)

## Frontend/Android Architecture

The Android app implements a Model-View-ViewModel inspired layer architecture outlined [here](https://developer.android.com/topic/architecture#recommended-app-arch).

## Backend Setup

1. `npm install`.
2. Set up mongoDB (connection URI)
3. Create a `.env` file in the `backend` directory with the following variables:
   ```plaintext
   MONGO_URI=<your_mongodb_uri>
   OPENAI_API_KEY=<your_openai_api_key>
   VECTOR_STORE_URL=http://localhost:8000
   AWS_ACCESS_KEY_ID=<your AWS access key>
   AWS_SECRET_ACCESS_KEY=<your AWS IAM secret access key>
   AWS_REGION=<your aws_region>
   S3_BUCKET_NAME=<your s3 bucket name>
   PRODUCTION_URL=<production_backend_server_url>
   ```
4. **Run ChromaDB:**
   Ensure Docker is running, then execute:
   ```bash
   docker pull chromadb/chroma
   docker run -d -p 8000:8000 chromadb/chroma
   ```
5. `npm start` or `npm run dev` (nodemon).

## Architecture Overview

### ChromaDB Implementation

The system uses ChromaDB to store document embeddings with the following architecture:

- **User-Specific Collections**: Each user has their own ChromaDB collection named `user_{userId}`.
- **Document Metadata**: Documents within a collection are tagged with metadata including:
  - `userId`: The owner of the document.
  - `documentId`: The identifier of the document.
  - `chunkIndex`: Position of the chunk within the document.
- **Document Chunking**: Large documents are split into manageable chunks for better retrieval.
- **Filtering**: Queries can be filtered to specific documents or search across all user documents.

## API Endpoints

### User Authentication

**Endpoint: `/auth/login`**
- Method: `POST`
- Body: raw
```json
{
   "userId": "google id of user",
   "name": "name of user",
   "email": "email of user"
}
```
- Response:
```json
{
    "data": {
       "user": { 
            "email": "user email",
            "name": "user name",
            "googleId": "google id of user",
            "subscribed": false
       }
    }
 }
```

### Chat

**Endpoint: `/chat`**
- Method: `GET`
- Description: Retrieves the user's chat history or creates a new chat if one doesn't exist
- Params:
```json
   "userId": "user google id",
```
- Response:
```json
{
   "data": {
      "chat": {
         "messages": [
            {
               "role": "system",
               "content": "You are a helpful assistant that provides accurate information based on the context provided.",
               "timestamp": "2023-07-10T12:34:56.789Z"
            },
            {
               "role": "user",
               "content": "What is artificial intelligence?",
               "timestamp": "2023-07-10T12:35:10.123Z"
            },
            {
               "role": "assistant",
               "content": "Artificial intelligence (AI) refers to the simulation of human intelligence in machines...",
               "timestamp": "2023-07-10T12:35:15.456Z"
            }
         ],
         "createdAt": "2023-07-10T12:34:56.789Z",
         "updatedAt": "2023-07-10T12:35:15.456Z",
         "metadata": {
            "type": "general"
         }
      }
   }
}
```

**Endpoint: `/chat/message`**
- Method: `POST`
- Description: Sends a message to the user's chat and gets an AI response
- Body:
```json
{
   "userId": "user123",
   "message": "What can you tell me about the documents I uploaded?"
}
```
- Response:
```json
{
   "data": {
      "response": {
         "role": "assistant",
         "content": "Based on the documents you've uploaded, I can see that you have several files related to machine learning. One document discusses neural networks and their applications in image recognition...",
         "timestamp": "2023-07-10T12:36:25.789Z"
      }
   }
}
```

**Endpoint: `/chat/history`**
- Method: `DELETE`
- Description: Deletes the user's chat history
- Params:
```json
   "userId": "user google id",
```
- Response:
```json
{
   "message": "Chat history cleared successfully"
}
```

### Documents

**Endpoint: `/document/upload`**
- Uploads a single document
- Method: `POST`
- Body: multipart/form-data
```json
{
   "document": "<your file (single) here>",
   "userId": "user google id",
   "documentName": "<user's given name for this document>",
   "context": "<user's provided context about this document>",
   "public" : false
}
```
- Response:
```json
{
   "data": {
      "docs": {
         "documentId": "first file",
         "uploadTime": "time of file upload",
         "activityGenerationComplete": false,
         "public": false
      },
   }
}
```
- Note: The system supports PDF, JPEG, PNG, TIFF, and text files. This will also extract document text into the ChromaDB, and create the quiz and flashcards associated with the document uploaded in a separate background process.

**Endpoint: `/document/delete`**
- Deletes one document given a userId and the documentId you want to delete
- Method: `DELETE`
- Params:
```json
   "userId": "user google id",
   "documentId": "document id 1"
```
- Response: N/A
- Note: This deletes both the document from S3, mongodb and its embeddings from ChromaDB and also deletes the study materials (quiz, flashcards) associated with the document.

**Endpoint: `/document/retrieve`**
- Retrieves the user's documents. Retrieves one if a single documentId is provided and all of them if no documentId is provided
- Method: `GET`
- Params: documentId is an OPTIONAL field
```json
   "userId": "user google id",
   "documentId": "document id 1"
```
- Response:

`Multiple`
```json
{
   "data": {
      "docs": [
         {
            "documentId": "first file",
            "documentName": "<user's given name for this document>",
            "uploadTime": "time of file upload",
            "activityGenerationComplete": false,
            "public": false
         },
         {
            "documentId": "second file",
            "documentName": "<user's given name for this document>",
            "uploadTime": "time of file upload",
            "activityGenerationComplete": true,
            "public": false
         }
      ]
   }
}
```
`Single`
```json
{
   "data": {
      "docs": {
         "documentId": "first file",
         "documentName": "<user's given name for this document>",
         "uploadTime": "time of file upload",
         "activityGenerationComplete": false
      }
   }
}
```

### Study

**Endpoint: `/study/quiz`**
- Retrieves the quizzes associated with a documentId from a user. If no documentId is provided, all of the user's quizzes are retrieved.
- Method: `GET`
- Params: documentId is an OPTIONAL field
```json
   "userId": "user google id",
   "documentId": "document id 1"
```
- Response

`Multiple`
```json
{
   "data": [
      {
         "userId": "user google id",
         "documentId": "file documentId 1",
         "quiz": [
            {
               "question": "Question 1",
               "answer": "C",
               "options": {
                  "A": "Answer 1",
                  "B": "Answer 2",
                  "C": "Answer 3",
                  "D": "Answer 4"
               } 
            },
            {
               "question": "Question 2",
               "answer": "A",
               "options": {
                  "A": "Answer 1",
                  "B": "Answer 2",
                  "C": "Answer 3",
                  "D": "Answer 4"
               }
            }
         ]
      }
   ]
}
```
`Singular`
```json
{
   "data": {
      "userId": "user google id",
      "documentId": "file documentId 1",
      "quiz": [
         {
            "question": "Question 1",
            "answer": "C",
            "options": {
               "A": "Answer 1",
               "B": "Answer 2",
               "C": "Answer 3",
               "D": "Answer 4"
            } 
         },
         {
            "question": "Question 2",
            "answer": "A",
            "options": {
               "A": "Answer 1",
               "B": "Answer 2",
               "C": "Answer 3",
               "D": "Answer 4"
            }
         }
      ]
   }
}
```

**Endpoint: `/study/flashcards`**
- Retrieves the flashcards associated with a documentId from a user. If no documentId is provided, all of the user's flashcards are retrieved. 
- Method: `GET`
- Params: documentId is an OPTIONAL field
```json
   "userId": "user google id",
   "documentId": "document id 1"
```
- Response

`Multiple`
```json
{
   "data": [
      {
         "userId": "user google id",
         "documentId": "file documentId 1",
         "flashcards": [
            {
               "front": "first word",
               "back": "definition of first word"
            },
            {
               "front": "second word",
               "back": "definition of second word"
            }
         ]
      }
   ]
}
```
`Singular`

```json
{
   "data": {
      "userId": "user google id",
      "documentId": "file documentId 1",
      "flashcards": [
         {
            "front": "first word",
            "back": "definition of first word"
         },
         {
            "front": "second word",
            "back": "definition of second word"
         }
      ]
   }
}
```

**Endpoint: `/study/suggestedMaterials`**
- Retrieves suggested study materials (flashcards and quizzes) from other users' documents that are similar to the user's documents.
- Method: `GET`
- Params:
```json
   "userId": "user google id",
   "limit": 5  // Optional, default is 5
```
- Response

```json
{
   "data": {
      "flashcards": [
         {
            "userId": "other user google id",
            "documentId": "file documentId",
            "documentName": "doc name",
            "flashcards": [
               {
                  "front": "term",
                  "back": "definition"
               }
            ]
         }
      ],
      "quizzes": [
         {
            "userId": "other user google id",
            "documentId": "file documentId",
            "documentName": "doc name",
            "quiz": [
               {
                  "question": "Question?",
                  "answer": "A",
                  "options": {
                     "A": "Correct answer",
                     "B": "Wrong answer",
                     "C": "Wrong answer",
                     "D": "Wrong answer"
                  }
               }
            ]
         }
      ]
   }
}
```
- Note: This endpoint finds study materials from other users' documents that are most similar to the requesting user's documents. The similarity is calculated using document embeddings and cosine similarity. If no similar documents are found, empty arrays will be returned for both flashcards and quizzes.

### Subscription

**Endpoint: `/subscription`**
- Subscribes a user
- Method: `POST`
- Body: raw
```json
{
   "userId": "user google id"
}
```
- Response
```json
{
   "data": {
      "email": "user email",
      "name": "user name",
      "userId": "google id of user",
      "subscribed": true
   }
}
```

**Endpoint: `/subscription`**
- Unsubscribes a user
- Method: `DELETE`
- Params:
```json
   "userId": "user google id",
```
- Response
```json
{
   "data": {
      "email": "user email",
      "name": "user name",
      "userId": "google id of user",
      "subscribed": false
   }
}

```
**Endpoint: `/subscription`**
- Get User subscription status
- Method: `GET`
- Params:
```json
   "userId": "user google id",
```
- Response
```json
{
   "data": {
      "email": "user email",
      "name": "user name",
      "userId": "google id of user",
      "subscribed": false
   }
}
```
## Testing with cURL

Here are some example cURL commands to test the API:

### Document Upload
```
curl -X POST http://localhost:3000/document/upload -F "document=@/path/to/your/document.pdf" -F "userId=user123"
```

### RAG Query (All Documents)
```
curl -X POST http://localhost:3000/rag/query -H "Content-Type: application/json" -d '{"query": "What is the main topic of my documents?", "userId": "user123"}'
```

### RAG Query (Specific Document)
```
curl -X POST http://localhost:3000/rag/query -H "Content-Type: application/json" -d '{"query": "What is discussed in this document?", "userId": "user123", "documentId": "document.pdf"}'
```

### Create Chat Session
```
curl -X POST http://localhost:3000/chat -H "Content-Type: application/json" -d '{"userId": "user123", "metadata": {"documentId": "document.pdf"}}'
```
