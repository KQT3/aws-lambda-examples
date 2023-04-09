package main

import (
	"context"
	"encoding/base64"
	"encoding/json"
	"fmt"
	"log"
	"strings"

	"github.com/aws/aws-lambda-go/events"
	"github.com/aws/aws-lambda-go/lambda"
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
	parts := strings.Split(tokenString, ".")
	decodeString, err := base64.RawURLEncoding.DecodeString(parts[1])
	if err != nil {
		return "", err
	}

	var claims map[string]interface{}
	if err := json.Unmarshal(decodeString, &claims); err != nil {
		return "", err
	}
	fmt.Println(claims)
	if err := json.Unmarshal(decodeString, &claims); err != nil {
		return "", err
	}
	sub, ok := claims["sub"].(string)
	if !ok {
		return "", fmt.Errorf("missing or invalid 'sub' claim")
	}
	return sub, nil
}

//func getSubId(tokenString string, publicKey string) (string, error) {
//	token, err := jwt.Parse(tokenString, func(token *jwt.Token) (interface{}, error) {
//		// Verify that the signing method is RSA
//		if _, ok := token.Method.(*jwt.SigningMethodRSA); !ok {
//			return nil, fmt.Errorf("unexpected signing method: %v", token.Header["alg"])
//		}
//
//		// Get the public key from the token's header
//		publicKey, err := jwt.ParseRSAPublicKeyFromPEM([]byte(publicKey))
//		if err != nil {
//			return nil, fmt.Errorf("error parsing public key: %v", err)
//		}
//
//		return publicKey, nil
//	})
//	if err != nil {
//		return "", fmt.Errorf("error parsing token: %v", err)
//	}
//
//	// Extract the sub claim from the token's payload
//	if claims, ok := token.Claims.(jwt.MapClaims); ok && token.Valid {
//		subId, ok := claims["sub"].(string)
//		if !ok {
//			return "", fmt.Errorf("sub claim is not a string")
//		}
//		return subId, nil
//	} else {
//		return "", fmt.Errorf("invalid token claims")
//	}
//}

func convertURLToCorrectFormat(urlToBeConverted string) string {
	return strings.Replace(urlToBeConverted, "https://s3.amazonaws.com/chainbot.chaincuet.com.storage", "https://storage-chainbot.chaincuet.com", 1)
}

func main() {
	lambda.Start(HandleLambdaEvent)
}
