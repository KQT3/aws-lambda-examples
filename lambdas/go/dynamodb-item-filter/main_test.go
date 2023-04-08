package main

import (
	"crypto/rand"
	"crypto/rsa"
	"fmt"
	"github.com/golang-jwt/jwt/v5"
	"github.com/google/uuid"
	"github.com/stretchr/testify/assert"
	"math/big"
	"testing"
	"time"
)

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
	token, _ := createTestToken()

	print(token)
}

func createTestToken() (string, error) {
	privateKey, err := rsa.GenerateKey(rand.Reader, 2048)
	if err != nil {
		return "", err
	}
	publicKey := privateKey.PublicKey
	fmt.Println("publicKey: ", publicKey)
	token := jwt.NewWithClaims(jwt.SigningMethodRS256, jwt.MapClaims{
		"sub": fmt.Sprintf("%s", uuid.New()),
		"exp": time.Now().Add(time.Hour).Unix(),
		"iat": time.Now().Unix(),
		"nbf": time.Now().Unix(),
		"jti": fmt.Sprintf("%d", big.NewInt(64)),
	})

	tokenString, err := token.SignedString(privateKey)
	if err != nil {
		return "", err
	}

	return tokenString, nil
}
