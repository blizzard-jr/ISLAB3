#!/bin/bash
# Скрипт для запуска JMeter тестов в GUI режиме

JMETER_HOME="/Users/blizzard.jr/Downloads/apache-jmeter-5.6.3"
PROJECT_DIR="/Users/blizzard.jr/ITMOUniversity/InformationSystems/ISLAB2/ISLAB2"

echo "=== JMeter Transaction Isolation Test ==="
echo "Project: $PROJECT_DIR"
echo ""

# Проверяем, что бэкенд запущен
if ! curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo "WARNING: Backend is not running on port 8080!"
    echo "Please start the backend first."
fi

echo "Starting JMeter GUI..."
echo ""

# Запуск JMeter GUI с тест-планом
cd "$PROJECT_DIR"
"$JMETER_HOME/bin/jmeter" -t "$PROJECT_DIR/jmeter/test-plan.jmx" -Duser.dir="$PROJECT_DIR"
