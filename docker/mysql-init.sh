#!/usr/bin/env bash
# MySQL 首次初始化脚本：把 codeforge/db/V*.sql 按版本号顺序执行。
# 直接挂载 /docker-entrypoint-initdb.d 会让 V10 排在 V2 前面，所以这里显式排序。
set -euo pipefail

DB="${MYSQL_DATABASE:-codeforge}"
SQL_DIR="/db"

echo "[codeforge-init] target database: ${DB}"

mapfile -t files < <(find "${SQL_DIR}" -maxdepth 1 -name 'V*.sql' | sort -V)

if [ "${#files[@]}" -eq 0 ]; then
  echo "[codeforge-init] no V*.sql found in ${SQL_DIR}"
  exit 0
fi

for file in "${files[@]}"; do
  echo "[codeforge-init] applying $(basename "${file}")"
  mysql --protocol=socket -uroot -p"${MYSQL_ROOT_PASSWORD}" \
    --default-character-set=utf8mb4 "${DB}" < "${file}"
done

echo "[codeforge-init] all schema scripts applied"
