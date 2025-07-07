## Initial setup

```bash
cd team-oopsops
chmod +x api/scripts/setup.sh
./api/scripts/setup.sh
```

## Generate all code

```bash
./api/scripts/gen-all.sh
```

## Run pre-commit checks

```bash
pre-commit run --all-files
```

## Lint OpenAPI spec only

```bash
npx spectral lint docs/openapi.yaml --format pretty
```
