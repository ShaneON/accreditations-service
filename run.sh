#!/bin/bash

APP_NAME="AccreditationService"
PORT=9999

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}Starting $APP_NAME on port $PORT...${NC}"

# Check if Maven Wrapper exists
if [ ! -f "./mvnw" ]; then
  echo -e "${RED}Error: Maven Wrapper (mvnw) not found. Please generate it using 'mvn -N wrapper:wrapper'.${NC}"
  exit 1
fi

# Clean and build
echo -e "${GREEN}Building the application...${NC}"
./mvnw clean package -DskipTests

# Check if the build succeeded
if [ $? -ne 0 ]; then
  echo -e "${RED}Build failed. Please check the output for errors.${NC}"
  exit 1
fi

# Run
echo -e "${GREEN}Running the application...${NC}"
java -jar target/yieldstreet-accreditation-service-0.0.1-SNAPSHOT.jar --server.port=$PORT

# Check started successfully
if [ $? -eq 0 ]; then
  echo -e "${GREEN}$APP_NAME is running at http://localhost:$PORT${NC}"
else
  echo -e "${RED}Failed to start $APP_NAME. Please check the logs for details.${NC}"
fi