
from confluent_kafka import Producer
from confluent_kafka.serialization import StringSerializer
import json
from time import sleep
import uuid
import random

class KafkaUser():

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.bootstrap_servers = 'localhost:9092'
        self.topic = 'player'
        self.serializer = StringSerializer('utf_8')


    def on_start(self):
        self.producer = Producer({
            'bootstrap.servers': self.bootstrap_servers,
            'acks': 'all',
        })

    def on_stop(self):
        self.producer.flush()

    def send_message(self):
        name = random.randint(1, 25)
        wh_qty = random.randint(1, 5)
        message = {"playerUuid": str(uuid.uuid4()),
                   "playerName": "name_" + str(name),
                   "playerEmail":"test@email.com",
                   "playerLevel":"1",
                    "numberOfWarehouses":wh_qty}

        self.producer.produce(self.topic, key='consuming', value=json.dumps(message))
        self.on_stop()
        

    def teardown(self):
        self.producer.flush()
        self.producer.close()

kafka = KafkaUser()
for i in range(100):
    kafka.on_start()
    kafka.send_message()
    print("Sending data to Kafka broker counter=" , i)


