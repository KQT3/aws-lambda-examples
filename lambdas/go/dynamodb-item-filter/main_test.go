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
	token := "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJoZmZYS1hTcXY1aDdNd2RibHAwRDRMamROeFNLLTJLUzN2UnY0ajQwQnVZIn0.eyJleHAiOjE2Nzk3NTA2MTYsImlhdCI6MTY3OTc1MDMxNiwiYXV0aF90aW1lIjoxNjc5NzQ5NzE1LCJqdGkiOiI2MGY4M2UyOC1lMmE2LTQ3ZTAtYTg5ZS1mZDBhNjY2YzMwMjgiLCJpc3MiOiJodHRwczovL2F1dGguY2hhaW5jdWV0LmNvbS9hdXRoL3JlYWxtcy9jaGFpbmJvdCIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiIyNjdlOTg1OS0zMzUxLTQxOTQtOWFlNy1mNzNhNWVkOWVmMTEiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJjaGF0Ym90LWNsaWVudCIsInNlc3Npb25fc3RhdGUiOiJiMjUxYjY3MC1kODQyLTRkNmYtYThmNC1jNmFlZTllZmQ2NTUiLCJhY3IiOiIwIiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHBzOi8vY2hhaW5tdXNpYy5jaGFpbmN1ZXQuY29tIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJkZWZhdWx0LXJvbGVzLWNoYWluYm90Iiwib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiIsInVzZXIiXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6Im9wZW5pZCBlbWFpbCBwcm9maWxlIiwic2lkIjoiYjI1MWI2NzAtZDg0Mi00ZDZmLWE4ZjQtYzZhZWU5ZWZkNjU1IiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW1lIjoiY2hhdGJvdEBnbWFpbC5jb20gY2hhdGJvdEBnbWFpbC5jb20iLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJjaGF0Ym90QGdtYWlsLmNvbSIsImdpdmVuX25hbWUiOiJjaGF0Ym90QGdtYWlsLmNvbSIsImZhbWlseV9uYW1lIjoiY2hhdGJvdEBnbWFpbC5jb20iLCJlbWFpbCI6ImNoYXRib3RAZ21haWwuY29tIn0.oKvLjFrU0c3H2zs6vSWbqczU0bjTO2-yk6NgGRteVIC9mSNNzKGcAMkYZ-pC0k4ZCTJhqEVipEkMk1uxBB_ZuwvQt4YSpZj6Us3F-3883YgR_7Zb8booFwlG4GXLT72RnWE3RkBZ9KYuAFVVD9k5lSInWp4DBWDOG3A4s7lbd-HiNVA1av86TGgAFVanUa5AIBbA1VnxSA2rHw7QPj4TutzbPUxQpUqMwmUdxgROPyBbYchB_311V9AY3uQQr7D1Xfi0OaSLeT8DfF76MAruPFmiKkEOuW_PFf6wJgL3KOhg5wBWNyg5R6tN5PSbUYnaQJu6FNRbkl-7HHr4B6aBmA"

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
