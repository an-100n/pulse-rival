#!/bin/bash
set -e

BASE_URL="http://localhost:8080/api/v1"

# 1. Login to get Token
echo "🔑 Logging in..."
TOKEN=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username": "user_1", "password": "password"}' | jq -r '.token')

if [ -z "$TOKEN" ] || [ "$TOKEN" == "null" ]; then
  echo "❌ Login failed. Ensure server is running."
  exit 1
fi
echo "✅ Token acquired."

# 2. Register New User
echo "👤 Registering test user..."
RAND_ID=$RANDOM
USER_ID=$(curl -s -X POST "$BASE_URL/users" \
  -H "Content-Type: application/json" \
  -d "{\"username\": \"champ_$RAND_ID\", \"email\": \"champ_$RAND_ID@example.com\", \"password\": \"password\", \"timezone\": \"UTC\"}" | jq -r '.id')

if [ -z "$USER_ID" ] || [ "$USER_ID" == "null" ]; then
  echo "❌ Registration failed."
  exit 1
fi
echo "✅ User ID: $USER_ID"

# 3. Log Massive Activity
echo "🏃 Logging Activity (1,000,000 Steps)..."
curl -s -X POST "$BASE_URL/activities" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"userId\": \"$USER_ID\",
    \"type\": \"STEPS\",
    \"value\": 1000000,
    \"rawData\": {\"source\": \"verification_script\"}
  }" | jq .

# 4. Check Leaderboard
echo "🏆 Fetching Leaderboard..."
LEADERBOARD=$(curl -s -X GET "$BASE_URL/leaderboards/global" \
  -H "Authorization: Bearer $TOKEN")

echo "$LEADERBOARD" | jq .

# Verify
if echo "$LEADERBOARD" | grep -q "$USER_ID"; then
  echo "✅ SUCCESS: User found in Top 10!"
else
  echo "❌ FAILURE: User not found in Top 10."
  exit 1
fi