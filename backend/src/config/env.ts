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

// Updated function with type safety
export function getEnvVariable(name: EnvVar): string {
    const value = process.env[name];
    return value ?? '';
}
