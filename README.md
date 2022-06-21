# Spring Cloud Kubernetes Sample

## Prerequisites

- minikube installed and running on your computer
- minikube ingress addon enabled

on MacOS
```bash
$ # starts a local Kubernetes cluster
$ minikube start --vm-driver="hyperkit"

$ # configure environment to use minikube’s Docker daemon
$ eval $(minikube -p minikube docker-env)

$ # enable a minikube addon
$ minikube addons enable ingress
```

on Windows10 pro
```powershell
C:\> minikube start --vm-driver="hyperv"
C:\> minikube docker-env --shell powershell | Invoke-Expression
C:\> minikube addons enable ingress
```

## set External URL to expose gateway-service

- Retrieve the minikube ip address

```bash
$ minikube ip
192.168.64.3
```

- Edit gradle.properties for gateway-service

```bash
$ cd /path/to/gateway-service
$ vi gradle.properties
---
# set nip.io domain with minikube ip
jkube.domain=192.168.64.3.nip.io
---
```

## Deploy to local cluster

### kafka (TODO: KRaft mode)

```bash
$ kubectl create namespace kafka
$ kubectl create -f 'https://strimzi.io/install/latest?namespace=kafka' -n kafka
$ kubectl apply -f k8s/local/kafka/kafka-persistent-single.yml -n kafka
$ kubectl wait kafka/my-cluster --for=condition=Ready --timeout=300s -n kafka
```

### mysql

```bash
$ kubectl apply -f k8s/local/mysql
```

### Build & Apply all of services

```bash
$ # delete old service
$ ./gradlew k8sUndeploy

$ # apply new service
$ ./gradlew clean k8sApply
```

### Build & Apply a service (e.g. gateway-service)

```bash
$ # delete old service
$ ./gradlew gateway-service:k8sUndeploy

$ # apply new service
$ ./gradlew gateway-service:clean gateway-service:k8sApply

$ # tail the log
$ ./gradlew gateway-service:k8sLog

$ # check pod, svc
$ kubectl get all
```

### Access to the backend services

```bash
$ # check the ingress resource
$ kubectl get ingress
NAME              CLASS   HOSTS                                 ADDRESS     PORTS   AGE
gateway-service   nginx   gateway-service.192.168.64.3.nip.io   localhost   80      11m

$ # send a request to hello-service behind gateway-service
$ curl http://gateway-service.$(minikube ip).nip.io/hello-service/hello
Hello, World!
```

### API Documentation

- Swagger UI

```bash
$ echo http://gateway-service.$(minikube ip).nip.io/webjars/swagger-ui/index.html
http://gateway-service.192.168.64.3.nip.io/webjars/swagger-ui/index.html
```

- Gateway routes definitions

```bash
$ echo http://gateway-service.$(minikube ip).nip.io/gateway-service/actuator/gateway/routes
http://gateway-service.192.168.64.3.nip.io/gateway-service/actuator/gateway/routes
```
