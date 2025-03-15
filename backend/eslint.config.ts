// eslint.config.js
import tseslint from 'typescript-eslint';

export default tseslint.config(
  {
    extends: [
      ...tseslint.configs.recommended,
    ],
    rules: {
      '@typescript-eslint/no-non-null-assertion': 'error',
      '@typescript-eslint/no-unsafe-argument': 'error', 
      '@typescript-eslint/no-unsafe-return': 'error',
      '@typescript-eslint/prefer-nullish-coalescing': 'error',
      'no-unused-vars': 'off',
      '@typescript-eslint/no-unused-vars': ['error']
    },
    ignores: ['dist/', 'node_modules/']
  },
  {
    languageOptions: {
      parserOptions: {
        project: './tsconfig.json',
        tsconfigRootDir: import.meta.dirname,
      },
    },
    files: ['**/*.ts']
  }
);