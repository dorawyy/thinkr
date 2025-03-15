export function validateEnv() {
    const requiredEnvVars = [
        'MONGO_URI',
        'OPENAI_API_KEY',
        'VECTOR_STORE_URL',
        'AWS_ACCESS_KEY_ID',
        'AWS_SECRET_ACCESS_KEY',
        'AWS_REGION',
        'S3_BUCKET_NAME',
        'PRODUCTION_URL',
    ];

    const missingEnvVars = requiredEnvVars.filter(
        (envVar) => !process.env[envVar]
    );

    if (missingEnvVars.length > 0) {
        throw new Error(
            `Missing required environment variables: ${missingEnvVars.join(', ')}`
        );
    }
}
