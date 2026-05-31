# Postman Collections

Collections are grouped by microservice name. Each folder contains:

- `<service>.postman_collection.json`
- `<service>.local.postman_environment.json`

Recommended import order:

1. Import the environment file for the service.
2. Import the collection file.
3. Select the environment in Postman.

Most requests use the API Gateway URL by default:

```text
http://localhost:8080
```

Direct service URLs are also available as environment variables.
