import { Result as BaseResult } from '../../interfaces';

// Extended interface for test purposes that includes success property
export interface TestResult extends BaseResult {
  success: boolean;
} 