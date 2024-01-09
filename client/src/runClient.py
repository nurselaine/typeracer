import subprocess
import os
import platform

def main():

    # change working directory
    os.chdir(os.path.dirname(os.path.abspath(__file__)))

    if compileClient():
        print("Compile passed")
        runClient()


def compileClient():
    try:
        subprocess.run(['javac', './Main.java'], check=True)
        return True
    except subprocess.CalledProcessError:
        print("Compile failed")
        return False


def runClient():
    try:
        subprocess.run(['java', 'Main'], check=True)
        print("Running!")
    except subprocess.CalledProcessError:
        print("Failed to run")


if __name__ == "__main__":
    main()
