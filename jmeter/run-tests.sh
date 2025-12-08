#!/bin/bash

# ==============================================
# JMeter Load Test Runner
# Запуск тестов изоляции транзакций
# ==============================================

# Цвета для вывода
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}  IS_LAB2 JMeter Concurrency Tests     ${NC}"
echo -e "${GREEN}========================================${NC}"

# Проверяем наличие JMeter
if ! command -v jmeter &> /dev/null; then
    echo -e "${RED}ERROR: JMeter не найден!${NC}"
    echo ""
    echo "Установите JMeter одним из способов:"
    echo ""
    echo "  macOS:   brew install jmeter"
    echo "  Linux:   sudo apt install jmeter"
    echo "  Manual:  https://jmeter.apache.org/download_jmeter.cgi"
    echo ""
    exit 1
fi

# Переходим в директорию проекта
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
cd "$PROJECT_DIR"

echo -e "${YELLOW}Project directory: $PROJECT_DIR${NC}"

# Проверяем что бэкенд запущен
echo -e "\n${YELLOW}Проверка бэкенда на localhost:8080...${NC}"
if ! curl -s http://localhost:8080 > /dev/null 2>&1; then
    echo -e "${RED}ERROR: Бэкенд не отвечает на localhost:8080${NC}"
    echo "Запустите бэкенд командой:"
    echo "  ./mvnw spring-boot:run"
    exit 1
fi
echo -e "${GREEN}✓ Бэкенд доступен${NC}"

# Проверяем тестовые данные
if [ ! -f "test-data/creatures1.yaml" ]; then
    echo -e "${RED}ERROR: Файл test-data/creatures1.yaml не найден${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Тестовые данные найдены${NC}"

# Очищаем предыдущие результаты
rm -rf jmeter/results/*
mkdir -p jmeter/results

# Запуск тестов
echo -e "\n${GREEN}Запуск JMeter тестов (20 пользователей)...${NC}"
echo -e "${YELLOW}Это может занять 2-3 минуты${NC}\n"

jmeter -n \
    -t jmeter/test-plan.jmx \
    -l jmeter/results/results.jtl \
    -e -o jmeter/results/html-report \
    -JPROJECT_DIR="$PROJECT_DIR" \
    -JBASE_URL=localhost \
    -JPORT=8080

# Проверяем результат
if [ $? -eq 0 ]; then
    echo -e "\n${GREEN}========================================${NC}"
    echo -e "${GREEN}  ✓ Тесты завершены успешно!          ${NC}"
    echo -e "${GREEN}========================================${NC}"
    echo ""
    echo "Результаты:"
    echo "  - JTL файл:    jmeter/results/results.jtl"
    echo "  - HTML отчёт:  jmeter/results/html-report/index.html"
    echo "  - CSV отчёт:   jmeter/results/aggregate-report.csv"
    echo ""
    echo "Откройте HTML отчёт:"
    echo "  open jmeter/results/html-report/index.html"
else
    echo -e "\n${RED}========================================${NC}"
    echo -e "${RED}  ✗ Тесты завершились с ошибками      ${NC}"
    echo -e "${RED}========================================${NC}"
    exit 1
fi
