FROM mcr.microsoft.com/mssql/server:2022-latest

USER root

WORKDIR /usr/config

COPY entrypoint.sh /usr/config/
COPY initialize.sql /usr/config/sql/

RUN chmod +x /usr/config/entrypoint.sh

ENTRYPOINT ["/usr/config/entrypoint.sh"]