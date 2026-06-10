#!/bin/bash

/opt/mssql/bin/sqlservr &

echo "Iniciando SQL Server..."

# aguarda o SQL Server ficar pronto para aceitar conexões
for i in $(seq 1 30); do
    /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "$MSSQL_SA_PASSWORD" -C -Q "SELECT 1" > /dev/null 2>&1
    if [ $? -eq 0 ]; then
        echo "SQL Server pronto após ${i}s."
        break
    fi
    echo "Aguardando SQL Server... (${i}/30)"
    sleep 1
done

# verifica se banco já existe
DB_EXISTS=$(/opt/mssql-tools18/bin/sqlcmd \
    -S localhost \
    -U sa \
    -P "$MSSQL_SA_PASSWORD" \
    -Q "SET NOCOUNT ON; SELECT COUNT(*) FROM sys.databases WHERE name='JavaFX'" \
    -C -h -1 -W)

if [ "$DB_EXISTS" == "0" ]; then

    echo "Banco não encontrado. Executando scripts..."

    for script in /usr/config/sql/*.sql
    do
        echo "Executando $script"
        /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "$MSSQL_SA_PASSWORD" -C -i "$script"
    done

    echo "Inicialização concluída."

else
    echo "Banco já existe. Pulando inicialização."
fi

wait