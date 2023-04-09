#### Build Go zip file

https://docs.aws.amazon.com/lambda/latest/dg/lambda-golang.html

https://github.com/aws/aws-lambda-go

https://github.com/aws/aws-lambda-go/tree/main/events

#### Setup Go

```
require go-version v1.20.3
Set handler to main

GOOS=linux GOARCH=amd64 CGO_ENABLED=0 go build -o main main.go && \
zip main.zip main
```
