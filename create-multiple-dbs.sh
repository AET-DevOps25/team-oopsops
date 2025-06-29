# create-multiple-dbs.sh
#!/bin/bash
set -e
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE DATABASE documentdb;
    CREATE DATABASE anonymizationdb;
EOSQL
