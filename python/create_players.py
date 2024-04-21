import requests
from concurrent.futures import ThreadPoolExecutor
import random
import threading
import json
import time
import uuid

url_create_player = "http://localhost:8081/api/v1/player/create"
url_create_material = "http://localhost:8081/api/v1/material/create"



header = {"Content-Type":"application/json"}
player = {
    "playerUuid": "ply_123",
    "playerName": "Weak-Player",
    "playerEmail": "test@email.com",
    "playerLevel": "1",
    "numberOfWarehouses": 3
}

material_payload = {
      "materialState": "ADD",
      "warehouseUuidToHostMaterial": None,
      "playerUuid": "ply_123",
      "materialUuid": "uuid",
      "materialType": "IRON",
      "materialMaxCapacity":  1,
      "materialCurrentValue": 1,
      "name": "IRON ORE",
      "description": "Needed to create shields",
      "icon": "IRON ICON"
}

uuid_list = [uuid.uuid1() for _ in range(10)]

def create_players():
     for i in range(10):
          time.sleep(1)
          player.update({"playerUuid": str(uuid_list[i])})
          player.update({"numberOfWarehouses": random.randint(0, 5)})
          response = requests.post(url=url_create_player,
                             data=json.dumps(player),
                             headers=header)
          print("Player ",  i, "creation statusCode=",  response.status_code, "thread ", threading.get_native_id())

def randomly_add_materials_to_random_players(material):

    
    material_payload.update({"materialType": material})
    for i in range(10):

        material_payload.update({"playerUuid": str(uuid_list[i])})
        material_payload.update({"materialMaxCapacity": str(random.randint(100, 500))})
        material_payload.update({"materialCurrentValue": str(random.randint(0, 100))})
        material_payload.update({"materialUuid": str(uuid.uuid1())})

        time.sleep(1)
        response = requests.post(url=url_create_material,
                      data=json.dumps(material_payload),
                      headers=header)
        print("Material ", i, "creation statusCode=", response.status_code, "thread ", threading.get_native_id())


if __name__ == "__main__":
     create_players()
     randomly_add_materials_to_random_players("IRON")
     randomly_add_materials_to_random_players("BOLT")
     randomly_add_materials_to_random_players("COOPER")