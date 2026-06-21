const fs = require('fs');
const dotenv = require('dotenv');

// Load environment variables from .env file
dotenv.config();

// Determine production mode
const isProduction = process.env.NODE_ENV === 'production' || process.env.PRODUCTION === 'true';

const targetPath = './src/environments/environment.ts';

// You can add more environment variables here as needed
const envConfigFile = `export const environment = {
  production: ${isProduction},
  apiUrl: '${process.env.API_URL || 'http://localhost:8081/api'}',
  geminiApiKey: '${process.env.GEMINI_API_KEY || ''}'
};
`;

const dir = './src/environments';
if (!fs.existsSync(dir)){
    fs.mkdirSync(dir, { recursive: true });
}

fs.writeFileSync(targetPath, envConfigFile);
console.log(`Environment file generated at ${targetPath} \n`);
