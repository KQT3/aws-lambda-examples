package main

import (
	"context"
	"encoding/base64"
	"encoding/json"
	"fmt"
	"github.com/aws/aws-lambda-go/events"
	"github.com/aws/aws-lambda-go/lambda"
	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/dynamodb"
	"log"
	"strings"
)

const tableName = "user_images"

type ImageCollectionDTO struct {
	Images             []*ImageDTO `json:"images"`
	ImagesCollectionID string      `json:"imagesCollectionId"`
	Timestamp          string      `json:"timestamp"`
}

type ImageDTO struct {
	ImageID string `json:"imageId"`
	URL     string `json:"url"`
}

func HandleLambdaEvent(_ context.Context, event events.APIGatewayV2HTTPRequest) (*ImageCollectionDTO, error) {
	eventJson, _ := json.MarshalIndent(event, "", "  ")
	log.Printf("EVENT: %s", eventJson)
	headers := event.Headers
	authorization := headers["authorization"]
	token := strings.TrimPrefix(authorization, "Bearer ")
	subId, err := getSubId(token)
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
	log.Printf("subId: %s\n", subId)
	log.Printf("imageIndex: %s\n", imageIndex)

	if err != nil {
		log.Printf("Failed to marshal response: %v\n", err)
	}

	itemFromDynamoDB, err := queryItemFromDynamoDB(subId, imageIndex)
	if err != nil {
		log.Printf("Failed to query item from DynamoDB: %v\n", err)
	}

	log.Printf("itemFromDynamoDB: %v\n", itemFromDynamoDB)

	return toDTO(itemFromDynamoDB.Items)
}

func queryItemFromDynamoDB(subId string, imageIndex string) (*dynamodb.QueryOutput, error) {
	session.Must(session.NewSession())

	newSession := session.Must(session.NewSession(&aws.Config{
		Region: aws.String("us-east-1"),
	}))

	dynamoDBService := dynamodb.New(newSession)

	expressionAttributeValues := map[string]*dynamodb.AttributeValue{
		":userId": {
			S: aws.String(subId),
		},
	}

	queryRequest := &dynamodb.QueryInput{
		TableName:                 aws.String(tableName),
		KeyConditionExpression:    aws.String("userId = :userId"),
		ExpressionAttributeValues: expressionAttributeValues,
		ProjectionExpression:      aws.String(fmt.Sprintf("imagesCollection[%s]", imageIndex)),
	}

	return dynamoDBService.Query(queryRequest)
}

func toDTO(items []map[string]*dynamodb.AttributeValue) (*ImageCollectionDTO, error) {
	if len(items) == 0 {
		return nil, fmt.Errorf("empty item list")
	}

	imagesCollectionItem := items[0]["imagesCollection"].L[0]
	if imagesCollectionItem == nil {
		return nil, fmt.Errorf("missing imagesCollection item")
	}

	imagesCollection, ok := imagesCollectionItem.M["images"]
	if !ok {
		return nil, fmt.Errorf("missing images field in imagesCollection")
	}

	imageDTOs := make([]*ImageDTO, 0, len(imagesCollection.L))
	for _, imageItem := range imagesCollection.L {
		image, err := convertImageItemToDTO(imageItem)
		if err != nil {
			return nil, fmt.Errorf("error converting image item to DTO: %v", err)
		}
		imageDTOs = append(imageDTOs, image)
	}

	imagesCollectionIDItem := imagesCollectionItem.M["imagesCollectionId"]
	if imagesCollectionIDItem == nil {
		return nil, fmt.Errorf("missing imagesCollectionId field in imagesCollection")
	}

	imagesCollectionID := aws.StringValue(imagesCollectionIDItem.S)

	timestampItem := imagesCollectionItem.M["timestamp"]
	if timestampItem == nil {
		return nil, fmt.Errorf("missing timestamp field in imagesCollection")
	}
	timestamp := aws.StringValue(timestampItem.S)

	return &ImageCollectionDTO{
		Images:             imageDTOs,
		ImagesCollectionID: imagesCollectionID,
		Timestamp:          timestamp,
	}, nil
}

func convertImageItemToDTO(imageItem *dynamodb.AttributeValue) (*ImageDTO, error) {
	imageIDItem := imageItem.M["imageId"]
	if imageIDItem == nil {
		return nil, fmt.Errorf("missing imageId field in image")
	}

	urlItem := imageItem.M["url"]
	if urlItem == nil {
		return nil, fmt.Errorf("missing url field in image")
	}

	imageID := aws.StringValue(imageIDItem.S)
	url := convertURLToCorrectFormat(aws.StringValue(urlItem.S))

	return &ImageDTO{
		ImageID: imageID,
		URL:     url,
	}, nil
}

func convertURLToCorrectFormat(urlToBeConverted string) string {
	return strings.Replace(urlToBeConverted, "https://s3.amazonaws.com/chainbot.chaincuet.com.storage", "https://storage-chainbot.chaincuet.com", 1)
}

func getSubId(tokenString string) (string, error) {
	parts := strings.Split(tokenString, ".")
	decodeToken, err := base64.RawURLEncoding.DecodeString(parts[1])
	if err != nil {
		return "", err
	}

	var claims map[string]interface{}
	if err := json.Unmarshal(decodeToken, &claims); err != nil {
		return "", err
	}

	sub, ok := claims["sub"].(string)
	if !ok {
		return "", fmt.Errorf("missing or invalid 'sub' claim")
	}
	return sub, nil
}

func main() {
	lambda.Start(HandleLambdaEvent)
}
