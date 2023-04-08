# aws-lambda-examples

https://docs.aws.amazon.com/lambda/latest/dg/lambda-java.html

https://docs.aws.amazon.com/lambda/latest/dg/lambda-golang.html

https://github.com/aws/aws-lambda-go

https://github.com/aws/aws-lambda-go/tree/main/events

https://github.com/awsdocs/aws-lambda-developer-guide/tree/main/sample-apps

#### Setup Java

```
cd lambdas/java
```

#### Setup Go

```
cd lambdas/go
mkdir playground && cd playground && go mod init playground
```

#### Build Go zip file

``` 
Set handler to main
GOOS=linux GOARCH=amd64 CGO_ENABLED=0 go build -o main main.go
zip main.zip main
```
