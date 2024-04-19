## Rockbitegames

#### This is the data structure used in this application
![image.png](./image/image.png)


## These are commands to test the application

### Build the game
```bash
$ ./gradlew clean build -Pspring.profiles.active=prod
```

### To Run the game
```bash
$ cd ./docker/
$ docker-compose --profile prod up -d
```

### WAIT SEVERAL SECONDS CONNECTION TO BE ESTABLISHED



### GET All Players
```bash
curl -X GET http://localhost:8081/api/v1/player/all \
-H "Content-Type: application/json" 
```

### DELETE All Players
```bash
curl -X DELETE http://localhost:8081/api/v1/player/all \
-H "Content-Type: application/json" 
```


### Create a Player
```bash
curl -X POST http://localhost:8081/api/v1/player/create \
-H "Content-Type: application/json" \
-d '{
    "playerUuid": "2deb74e2-9841-48d5-b9c9-adf100872ba2",
    "playerName": "Weak-Player",
    "playerEmail": "test@email.com",
    "playerLevel": "1",
    "numberOfWarehouses": 3
}'
```

### Add Materials IRON-1

```bash
curl -X POST http://localhost:8081/api/v1/material/create \
-H "Content-Type: application/json" \
-d ' {
      "materialState": "ADD",
      "warehouseUuidToHostMaterial": null,
      "playerUuid": "2deb74e2-9841-48d5-b9c9-adf100872ba2",
      "materialUuid": "1705b8da-d4a9-4f8e-b9cf-666631b981ba",
      "materialType": "IRON",
      "materialMaxCapacity": 100,
      "materialCurrentValue": 60,
      "name": "IRON ORE",
      "description": "Needed to create shields",
      "icon": "IRON ICON"
}'
```

### Add Materials IRON-2

```bash
curl -X POST http://localhost:8081/api/v1/material/create \
-H "Content-Type: application/json" \
-d ' {
      "materialState": "ADD",
      "warehouseUuidToHostMaterial": null,
      "playerUuid": "2deb74e2-9841-48d5-b9c9-adf100872ba2",
      "materialUuid": "1705b8da-d4a9-4f8e-b9cf-666631b981ba",
      "materialType": "IRON",
      "materialMaxCapacity": 100,
      "materialCurrentValue": 70,
      "name": "IRON ORE",
      "description": "Needed to create shields",
      "icon": "IRON ICON"
}'
```

### Add Materials IRON-3

```bash
curl -X POST http://localhost:8081/api/v1/material/create \
-H "Content-Type: application/json" \
-d ' {
      "materialState": "ADD",
      "warehouseUuidToHostMaterial": null,
      "playerUuid": "2deb74e2-9841-48d5-b9c9-adf100872ba2",
      "materialUuid": "1705b8da-d4a9-4f8e-b9cf-666631b981ba",
      "materialType": "IRON",
      "materialMaxCapacity": 100,
      "materialCurrentValue": 70,
      "name": "IRON ORE",
      "description": "Needed to create shields",
      "icon": "IRON ICON"
}'
```


### REMOVE Materials IRON-1

```bash
curl -X POST http://localhost:8081/api/v1/material/remove \
-H "Content-Type: application/json" \
-d ' {
      "materialState": "REMOVE",
      "playerUuid": "2deb74e2-9841-48d5-b9c9-adf100872ba2",
      "materialUuid": "1705b8da-d4a9-4f8e-b9cf-666631b981ba",
      "materialType": "IRON",
      "materialMaxCapacity": 100,
      "materialCurrentValue": 70,
      "name": "IRON ORE",
      "description": "Needed to create shields",
      "icon": "IRON ICON"
}'
```

### Add Materials BOLT-1

```bash
curl -X POST http://localhost:8081/api/v1/material/create \
-H "Content-Type: application/json" \
-d ' {
      "materialState": "ADD",
      "warehouseUuidToHostMaterial": null,
      "playerUuid": "2deb74e2-9841-48d5-b9c9-adf100872ba2",
      "materialUuid": "a6ea44e8-8163-4dda-adb2-4474c49f6e1c",
      "materialType": "BOLT",
      "materialMaxCapacity": 100,
      "materialCurrentValue": 10,
      "name": "BOLT",
      "description": "Needed to screw details",
      "icon": "BOLT ICON"
}'
```

### Add Materials BOLT-2

```bash
curl -X POST http://localhost:8081/api/v1/material/create \
-H "Content-Type: application/json" \
-d ' {
      "materialState": "ADD",
      "warehouseUuidToHostMaterial": null,
      "playerUuid": "2deb74e2-9841-48d5-b9c9-adf100872ba2",
      "materialUuid": "a6ea44e8-8163-4dda-adb2-4474c49f6e1c",
      "materialType": "BOLT",
      "materialMaxCapacity": 100,
      "materialCurrentValue": 20,
      "name": "BOLT",
      "description": "Needed to screw details",
      "icon": "BOLT ICON"
}'
```

### MOVE Material BOLT-1 into BOLT-2 

### In order to move Material, You should get empty Warehouse UUID. Just copy the response data and paste in ??? place below
```bash
curl -X GET http://localhost:8081/api/v1/warehouse/{2deb74e2-9841-48d5-b9c9-adf100872ba2}/empty-material \
-H "Content-Type: application/json"
```



```bash

curl -X POST http://localhost:8081/api/v1/material/move \
-H "Content-Type: application/json" \
-d ' {
      "materialState": "ADD",
      "warehouseUuidToHostMaterial": ???,
      "playerUuid": "2deb74e2-9841-48d5-b9c9-adf100872ba2",
      "materialUuid": "a6ea44e8-8163-4dda-adb2-4474c49f6e1c",
      "materialType": "BOLT",
      "materialMaxCapacity": 100,
      "materialCurrentValue": 20,
      "name": "BOLT",
      "description": "Needed to screw details",
      "icon": "BOLT ICON"
}'
```

Stop the game.
```bash
$ cd ./docker/
$ docker-compose down

```
Remove Backend Docker Image
```bash
$ docker stop backend
$ docker rmi $(docker images 'rockbitegames-backend' -a -q)
```