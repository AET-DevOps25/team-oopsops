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
   
    ⁠

5.⁠ Build with Docker

    open docker

```bash
    docker compose up -d --build
```

    ⁠

6.⁠ ⁠Open your browser and go to http://localhost:8000/docs to see the API documentation.

---