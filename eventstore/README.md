# Event Store

## Endpoints

- POST /aggregates/{aggregateName}

```bash
$ # 集約に必要な格納先を作成する
$ curl -s -X POST "http://localhost:8080/aggregates/bankaccount"
```

- POST /events/{aggregateName}/{aggregateId}

```bash
$ # 指定した集約に紐づくイベントを保存する
$ curl -s -X POST 'http://localhost:8080/events/bankaccount/7389dfe9-b019-11ec-9927-0242ac110004' \
-H 'Content-Type: application/json' \
--data-raw '[
    {
        "sequence": 1,
        "eventType": "order_created",
        "payload": "{\"status\":\"pending\"}"
    },
    {
        "sequence": 2,
        "eventType": "order_paid",
        "payload": "{\"status\":\"paid\"}",
        "metadata": "{ \"version\": 1 }"
    },
    {
        "sequence": 3,
        "eventType": "order_shipped",
        "payload": "{\"status\":\"shipped\"}"
    }
]'
```

- GET /events/{aggregateName}/{aggregateId}

```bash
$ # 指定した集約に紐づくイベントを取得する
$ curl -s -X GET 'http://localhost:8080/events/bankaccount/7389dfe9-b019-11ec-9927-0242ac110004'
```

- POST /snapshots/{aggregateName}

```bash
$ # 指定した集約のスナップショットを保存する
$ curl -s -X POST 'http://localhost:8080/snapshots/bankaccount' \
-H 'Content-Type: application/json' \
--data-raw '[
    {
        "aggregateId": "7389dfe9-b019-11ec-9927-0242ac110004",
        "sequence": 3,
        "payload": "{ \"id\": \"aaaabbbbccccdddddeeeeffff\" }",
        "metadata": "{\"version\": 1 }"
    }
]'
```

- GET /snapshots/{aggregateName}/{aggregateId}

```bash
$ # 指定した集約のスナップショットを取得する
$ curl -s -X GET 'http://localhost:8080/snapshots/bankaccount/7389dfe9-b019-11ec-9927-0242ac110004'
```
