export function getEnvVariable(name: string): string {
    const value = process.env[name];
    return value ?? '';
}
