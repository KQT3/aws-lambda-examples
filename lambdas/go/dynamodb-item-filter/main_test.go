package main

import (
	"github.com/stretchr/testify/assert"
	"testing"
)

func TestHandleRequest(t *testing.T) {
	event := MyEvent{
		Name: "Alice",
		Age:  25,
	}

	expectedResponse := MyResponse{
		Message: "Alice is 25 years old!",
	}

	response, err := HandleLambdaEvent(event)

	assert.Nil(t, err, "HandleLambdaEvent returned an error")
	assert.Equal(t, expectedResponse, response, "HandleLambdaEvent returned an unexpected response")
}
