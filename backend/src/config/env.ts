// Define allowed environment variables
const ALLOWED_ENV_VARS = [
  'OPENAI_API_KEY',
  'VECTOR_STORE_URL',
  'MONGODB_URI',
  'MONGO_URI',
  'PORT',
  'NODE_ENV',
  'PRODUCTION_URL',
  // Add these from your .env file
  'GOOGLE_CLIENT_ID',
  'JWT_SECRET',
  'AWS_ACCESS_KEY_ID',
  'AWS_SECRET_ACCESS_KEY',
  'AWS_REGION',
  'S3_BUCKET_NAME'
] as const;

// Create a type from the allowed variables
type EnvVar = typeof ALLOWED_ENV_VARS[number];

// Updated function with explicit variable mapping - no bracket notation
export function getEnvVariable(name: EnvVar): string {
    switch (name) {
        case 'OPENAI_API_KEY':
            return process.env.OPENAI_API_KEY ?? '';
        case 'VECTOR_STORE_URL':
            return process.env.VECTOR_STORE_URL ?? '';
        case 'MONGODB_URI':
            return process.env.MONGODB_URI ?? '';
        case 'MONGO_URI':
            return process.env.MONGO_URI ?? '';
        case 'PORT':
            return process.env.PORT ?? '';
        case 'NODE_ENV':
            return process.env.NODE_ENV ?? '';
        case 'PRODUCTION_URL':
            return process.env.PRODUCTION_URL ?? '';
        case 'GOOGLE_CLIENT_ID':
            return process.env.GOOGLE_CLIENT_ID ?? '';
        case 'JWT_SECRET':
            return process.env.JWT_SECRET ?? '';
        case 'AWS_ACCESS_KEY_ID':
            return process.env.AWS_ACCESS_KEY_ID ?? '';
        case 'AWS_SECRET_ACCESS_KEY':
            return process.env.AWS_SECRET_ACCESS_KEY ?? '';
        case 'AWS_REGION':
            return process.env.AWS_REGION ?? '';
        case 'S3_BUCKET_NAME':
            return process.env.S3_BUCKET_NAME ?? '';
        default:
            return '';
    }
}
