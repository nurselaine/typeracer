import subprocess
import os
import platform

def main():

    # change working directory
    os.chdir(os.path.dirname(os.path.abspath(__file__)))

    if compileServer():
        print("Compile passed")
        runServer()


def compileServer():
    try:
        subprocess.run(['javac', './Main.java'], check=True)
        return True
    except subprocess.CalledProcessError:
        print("Compile failed")
        return False


def runServer():
    try:
        command = ['java', 'Main']
        runInNewTerminal(command)
        #subprocess.run(['java', 'Main'], check=True)
        #print("Running!")
    except subprocess.CalledProcessError:
        print("Failed to run")


def runInNewTerminal(command):
    os_system = whatsMyOs()
    if os_system == 'Windows':
        subprocess.Popen(["start", "cmd", "/k"] + command, shell=True)
    elif os_system == 'Darwin':
        # For macOS
        subprocess.Popen(["osascript", "-e", 'tell application "Terminal" to do script "' + ' '.join(command) + '"'])
    elif os_system == 'Linux':
        # Adjust the terminal emulator as needed (xterm, gnome-terminal, etc.)
        subprocess.Popen(["xterm", "-e"] + command)
    else:
        print(f"Unsupported OS: {os_system}")


def whatsMyOs():
    return platform.system()


if __name__ == "__main__":
    main()
