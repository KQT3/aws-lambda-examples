package main

import (
	"context"
	"encoding/json"
	"fmt"
	"log"
	"strings"

	"github.com/aws/aws-lambda-go/events"
	"github.com/aws/aws-lambda-go/lambda"
	"github.com/form3tech-oss/jwt-go"
)

const tableName = "user_images"

type ImageDTO struct {
	ImagesCollectionID  string      `json:"imagesCollectionId"`
	Timestamp           string      `json:"timestamp"`
	Images              []ImageItem `json:"images"`
	ImagesCollectionLen int         `json:"imagesCollectionLen"`
}

type ImageItem struct {
	ImageID string `json:"imageId"`
	URL     string `json:"url"`
}

func HandleLambdaEvent(ctx context.Context, event events.APIGatewayV2HTTPRequest) (string, error) {
	eventJson, _ := json.MarshalIndent(event, "", "  ")
	log.Printf("EVENT: %s", eventJson)
	headers := event.Headers
	authorization := headers["authorization"]
	token := strings.TrimPrefix(authorization, "Bearer ")
	subID, err := getSubId(token)
	if err != nil {
		log.Printf("Failed to get subId: %v\n", err)
	}
	var body map[string]string
	if err := json.Unmarshal([]byte(event.Body), &body); err != nil {
		log.Printf("Failed to unmarshal request body: %v\n", err)
	}

	imageIndex, ok := body["imageIndex"]
	if !ok {
		log.Printf("Request body doesn't contain imageIndex\n")
	}
	log.Printf("subId: %s\n", subID)
	log.Printf("imageIndex: %s\n", imageIndex)

	if err != nil {
		log.Printf("Failed to marshal response: %v\n", err)
	}

	return "works", nil
}

func getSubId(tokenString string) (string, error) {
	token, err := jwt.Parse(tokenString, func(token *jwt.Token) (interface{}, error) {
		return []byte("your-256-bit-secret"), nil // replace with your own secret key
	})
	if err != nil {
		return "", err
	}
	if claims, ok := token.Claims.(jwt.MapClaims); ok && token.Valid {
		return claims["sub"].(string), nil
	} else {
		return "", fmt.Errorf("invalid token")
	}
}

func convertURLToCorrectFormat(urlToBeConverted string) string {
	return strings.Replace(urlToBeConverted, "https://s3.amazonaws.com/chainbot.chaincuet.com.storage", "https://storage-chainbot.chaincuet.com", 1)
}

func main() {
	lambda.Start(HandleLambdaEvent)
}
