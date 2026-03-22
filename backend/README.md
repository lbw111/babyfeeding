# Backend

## MySQL setup

Prepare a MySQL database that the application can access on `localhost:3306`.
The default JDBC URL is:

```text
jdbc:mysql://localhost:3306/babyfeeding?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true
```

The application uses the `root` user by default.

## Environment variable

Set `MYSQL_PASSWORD` before starting the backend:

```bash
export MYSQL_PASSWORD=your_mysql_password
```

If `MYSQL_PASSWORD` is not set, the application falls back to `password`.

## Run commands

Build and run locally:

```bash
mvn clean package
java -jar target/babyfeeding-backend-1.0.0.jar
```

Build and run with Docker:

```bash
docker build -t babyfeeding-backend .
docker run --rm -p 8765:8765 -p 8766:8766 -e MYSQL_PASSWORD=your_mysql_password babyfeeding-backend
```

## UDP discovery

The backend listens for UDP discovery messages on port `8766`.
When it receives `BFT_DISCOVER`, it replies with:

```text
BFT_HERE:<localIp>:8765
```
