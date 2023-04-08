package main

import (
	"crypto/rand"
	"crypto/rsa"
	"crypto/x509"
	"encoding/pem"
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
	token, bytes, err := createTestToken()
	fmt.Println(token)

	subId, _ := getSubId(token)
	fmt.Println("subId: ", subId)
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
		"sub": fmt.Sprintf("%s", uuid.New()),
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
