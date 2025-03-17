// Define allowed environment variables
const ALLOWED_ENV_VARS = [
    'OPENAI_API_KEY',
    'VECTOR_STORE_URL',
    'MONGODB_URI',
    'MONGO_URI',
    'PORT',
    'NODE_ENV',
    'PRODUCTION_URL',
    'GOOGLE_CLIENT_ID',
    'JWT_SECRET',
    'AWS_ACCESS_KEY_ID',
    'AWS_SECRET_ACCESS_KEY',
    'AWS_REGION',
    'S3_BUCKET_NAME',
] as const;

type EnvVar = (typeof ALLOWED_ENV_VARS)[number];

export function getEnvVariable(name: EnvVar): string {
    return process.env[name] ?? '';
}
