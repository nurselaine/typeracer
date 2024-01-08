import subprocess
import os

client_path = "../client/src/"
client_src = "Client.java"
client_exec = "Client.class"

script_dir = os.path.dirname(__file__) 

path_to_src = os.path.join(script_dir, client_path, client_src)

path_to_exec = os.path.join(script_dir, client_path, client_exec)

print(path_to_src)
print(path_to_exec)

try:
    result = subprocess.run(["javac", path_to_src], check=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
    print("Compilation successful")
except subprocess.CalledProcessError as e:
    print("Compilation failed")
    print(e.stdout)
    print(e.stderr)

try:
    result = subprocess.run(["javac", path_to_exec], check=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
except subprocess.CalledProcessError as e:
    print("Execution failed")
    print(e.stdout)
    print(e.stderr)
