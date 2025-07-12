# GENAI Service for the OOPSOPS project - Redacta

## Quick Start

0. ⁠Change directory to the genai-service folder

   ```bash
   cd genai-service
   ```

1. ⁠Create a virtual environment

   ```⁠bash
   # With python
   python -m venv .venv

   # With python3
   python3 -m venv .venv
   ```

2. ⁠Activate the virtual environment

   ```bash
   # On Windows
   .venv\Scripts\activate

   # On Linux or MacOS
   source .venv/bin/activate
   ```

3. ⁠Install the required dependencies

   ```bash
   pip install -r requirements.txt
   ```

4. ⁠Copy the ⁠ .env.example ⁠ file to ⁠ .env ⁠ and fill in the required values

   ```bash
   cp .env.example .env
   ```

5. ⁠Set your OpenAI API key in the .env file:

   ```bash
   # Edit .env file and add your OpenAI API key
   OPENAI_API_KEY=your_actual_openai_api_key_here
   ```

6. ⁠Run the service locally:

   ```bash
   # Run with uvicorn directly
   uvicorn main:app --reload --host 0.0.0.0 --port 8000

   # Or use python -m uvicorn
   python -m uvicorn main:app --reload --host 0.0.0.0 --port 8000
   ```

7. ⁠Open your browser and go to http://localhost:8000/docs to see the API documentation.

## Alternative: Docker Setup

If you prefer to use Docker:

```bash
docker compose up -d --build
```

---

## Testing

To run the tests for the GenAI service, use the following commands:

```bash
# Make the script executable
chmod +x run_tests.sh

./run_tests.sh
```

This will execute all tests and generate a coverage report in the `htmlcov` directory.
