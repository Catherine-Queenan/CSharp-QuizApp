#!/bin/bash
set -e

until mysql -h "$DB_HOST" -u "$DB_USER" -p"$DB_PASSWORD" -e 'quit'; do
  >&2 echo "MySQL is unavailable - sleeping"
  sleep 1
done

>&2 echo "MySQL is up - executing command"
exec "$@"
