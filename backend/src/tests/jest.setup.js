// Set a longer default timeout for all tests
jest.setTimeout(30000);

// Prevent MongoDB connections
jest.mock('mongoose', () => {
  const mConnect = jest.fn().mockResolvedValue({});
  const mSchema = jest.fn().mockImplementation(() => ({
    pre: jest.fn().mockReturnThis(),
    index: jest.fn().mockReturnThis()
  }));
  
  return {
    connect: mConnect,
    Schema: mSchema,
    model: jest.fn(),
    Connection: jest.fn(),
    set: jest.fn(),
  };
});

// Mock db connection
jest.mock('../db/mongo/connection', () => ({
  connectDB: jest.fn().mockResolvedValue(true)
})); 