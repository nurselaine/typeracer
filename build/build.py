import subprocess
import os

server_path = "./client/src/Client.java"

script_dir = os.path.dirname(__file__) 
absolute_path_to_java_file = os.path.join(script_dir, server_path)

try:
    result = subprocess.run("javac", absolute_path_to_java_file)
    print("Compiled!")

except subprocess.CalledProcessError as e:
    print("ooopsi")
    print(e.stdout)
