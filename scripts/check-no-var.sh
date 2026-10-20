#!/usr/bin/env bash
set -euo pipefail

# Проверка требования лабораторной работы: в коде не используется var,
# только конкретные типы.
if grep -rnE '(^|[^A-Za-z0-9_])var[[:space:]]+[A-Za-z_][A-Za-z0-9_]*[[:space:]]*=' \
        src/main/java src/test/java; then
    echo "::error::Использование 'var' запрещено: указывайте конкретный тип переменной."
    exit 1
fi

echo "Проверка пройдена: 'var' в исходном коде не используется."
