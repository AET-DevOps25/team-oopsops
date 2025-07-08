# GENAI Service for the OOPSOPS project - Redacta

## Quick Start

1.⁠ ⁠Create a virtual environment

   ```⁠bash
   # With python
   python -m venv .venv

   # With python3
   python3 -m venv .venv
   ```

2.⁠ ⁠Activate the virtual environment

   ```bash
   # On Windows
   .venv\Scripts\activate

   # On Linux or MacOS
   source .venv/bin/activate
   ```

3.⁠ ⁠Change directory to the genai-service folder and install the dependencies

   ```bash
   cd genai-service

   pip install -r requirements.txt
   ```


4.⁠ ⁠Copy the ⁠ .env.example ⁠ file to ⁠ .env ⁠ and fill in the required values

   ```bash
   cp .env.example .env
   ```

5.⁠ ⁠Set your OpenAI API key in the .env file:

   ```bash
   # Edit .env file and add your OpenAI API key
   OPENAI_API_KEY=your_actual_openai_api_key_here
   ```

6.⁠ ⁠Run the service locally:

   ```bash
   # Run with uvicorn directly
   uvicorn main:app --reload --host 0.0.0.0 --port 8000
   
   # Or use python -m uvicorn
   python -m uvicorn main:app --reload --host 0.0.0.0 --port 8000
   ```

7.⁠ ⁠Open your browser and go to http://localhost:8000/docs to see the API documentation.

## Alternative: Docker Setup

If you prefer to use Docker:

```bash
docker compose up -d --build
```

---