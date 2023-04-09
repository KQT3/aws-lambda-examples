package main

import (
	"context"
	"crypto/rand"
	"crypto/rsa"
	"crypto/x509"
	"encoding/pem"
	"fmt"
	"github.com/aws/aws-lambda-go/events"
	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/service/dynamodb"
	"github.com/golang-jwt/jwt/v5"
	"github.com/stretchr/testify/assert"
	"math/big"
	"testing"
	"time"
)

func TestHandleLambdaEvent(t *testing.T) {
	//given
	token, _, _ := createTestToken()

	event := events.APIGatewayV2HTTPRequest{
		Headers: map[string]string{
			"authorization": "Bearer " + token,
		},
		Body: `{"imageIndex": "1"}`,
	}

	//when
	response, err := HandleLambdaEvent(context.Background(), event)

	//then
	assert.Nil(t, err, "error should be nil")
	assert.Equal(t, "works", response, "response should be 'works'")
}

func TestConvertDynamoDBItemToDTO(t *testing.T) {
	//given
	imageId1 := &dynamodb.AttributeValue{S: aws.String("030cb329-023e-4d26-9c54-3f00fa6d0662")}
	url1 := &dynamodb.AttributeValue{S: aws.String("https://s3.amazonaws.com/chainbot.chaincuet.com.storage/imagebot/c3341d7d-8eb9-4ce5-ac7d-8c4b7e027e42")}
	image1 := map[string]*dynamodb.AttributeValue{"imageId": imageId1, "url": url1}

	imageId2 := &dynamodb.AttributeValue{S: aws.String("792b0ec0-f49e-475a-99a9-0eb4d7ee38bf")}
	url2 := &dynamodb.AttributeValue{S: aws.String("https://s3.amazonaws.com/chainbot.chaincuet.com.storage/imagebot/c3341d7d-8eb9-4ce5-ac7d-8c4b7e027e42")}
	image2 := map[string]*dynamodb.AttributeValue{"imageId": imageId2, "url": url2}

	imageId3 := &dynamodb.AttributeValue{S: aws.String("6f13b587-256a-49f3-a6c3-e799d4b8d605")}
	url3 := &dynamodb.AttributeValue{S: aws.String("https://s3.amazonaws.com/chainbot.chaincuet.com.storage/imagebot/c3341d7d-8eb9-4ce5-ac7d-8c4b7e027e42")}
	image3 := map[string]*dynamodb.AttributeValue{"imageId": imageId3, "url": url3}

	imageId4 := &dynamodb.AttributeValue{S: aws.String("b0fbf557-65a7-4f65-af74-870026b2b8f9")}
	url4 := &dynamodb.AttributeValue{S: aws.String("https://s3.amazonaws.com/chainbot.chaincuet.com.storage/imagebot/c3341d7d-8eb9-4ce5-ac7d-8c4b7e027e42")}
	image4 := map[string]*dynamodb.AttributeValue{"imageId": imageId4, "url": url4}

	images := &dynamodb.AttributeValue{L: []*dynamodb.AttributeValue{
		{M: image1},
		{M: image2},
		{M: image3},
		{M: image4},
	}}

	imagesCollectionId := &dynamodb.AttributeValue{S: aws.String("53ceeda8-e6fe-4f53-ab65-c8e0b1de5dbf")}
	timestamp := &dynamodb.AttributeValue{S: aws.String("2023-03-25T14:04:49.012Z")}

	imagesCollection := &dynamodb.AttributeValue{M: map[string]*dynamodb.AttributeValue{
		"images":             images,
		"imagesCollectionId": imagesCollectionId,
		"timestamp":          timestamp,
	}}

	item := &dynamodb.AttributeValue{L: []*dynamodb.AttributeValue{imagesCollection}}

	items := []*map[string]*dynamodb.AttributeValue{{"imagesCollection": item}}

	dto, err := toDTO(items)
	fmt.Println(*timestamp.S)
	fmt.Println(dto.Timestamp)
	assert.Nil(t, err, "error should be nil")
	assert.Equal(t, *timestamp.S, dto.Timestamp, "timestamp should be '2023-03-25T14:04:49.012Z'")
}

func TestConvertURLToCorrectFormat(t *testing.T) {
	//given
	inputURL := "https://s3.amazonaws.com/chainbot.chaincuet.com.storage/my-image.jpg"
	expectedURL := "https://storage-chainbot.chaincuet.com/my-image.jpg"

	//when
	outputURL := convertURLToCorrectFormat(inputURL)

	//then
	fmt.Println("outputURL: ", outputURL)
	assert.Equal(t, expectedURL, outputURL, "URLs did not match")
}

func TestGetSubId(t *testing.T) {
	//given
	expectedSubId := "f8a6902e-df48-4a8f-931e-63f848cd9743"
	token, _, _ := createTestToken()

	//when
	subId, _ := getSubId(token)

	//then
	assert.Equal(t, expectedSubId, subId, "subId did not match")
}

func createTestToken() (string, []byte, error) {
	privateKey, err := rsa.GenerateKey(rand.Reader, 2048)
	if err != nil {
		return "", nil, err
	}

	publicKeyBytes, err := x509.MarshalPKIXPublicKey(&privateKey.PublicKey)
	if err != nil {
		return "", nil, err
	}
	publicKeyPEM := pem.EncodeToMemory(&pem.Block{
		Type:  "RSA PUBLIC KEY",
		Bytes: publicKeyBytes,
	})

	token := jwt.NewWithClaims(jwt.SigningMethodRS256, jwt.MapClaims{
		"sub": fmt.Sprintf("%s", "f8a6902e-df48-4a8f-931e-63f848cd9743"),
		"exp": time.Now().Add(time.Hour).Unix(),
		"iat": time.Now().Unix(),
		"nbf": time.Now().Unix(),
		"jti": fmt.Sprintf("%d", big.NewInt(64)),
	})

	tokenString, err := token.SignedString(privateKey)
	if err != nil {
		return "", nil, err
	}

	return tokenString, publicKeyPEM, nil
}
