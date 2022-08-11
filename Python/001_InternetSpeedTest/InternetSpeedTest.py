import traceback
import speedtest
import time
from alive_progress import alive_bar
import  subprocess
import sys

def showWaitingBar(waitingTime):
    with alive_bar(waitingTime, title="Time till next trial", elapsed=False, stats=False) as bar:
        for i in range (0, waitingTime):
            bar()
            time.sleep(1)


def getUpDownSpeeds(st):
    strs = st.split(",")
    d = strs[5].replace("\"", "").strip()
    u = strs[6].replace("\"", "").strip()
    down = int(d, 10)*8/1024/1024
    up = int(u, 10)*8/1024/1024
    return (down, up)

## How to use:
#  [python_exec_path] <this_file> <path_to_speedtest_executable> <path_to_log_file>
# 
#  speedtest is run after every 300 seconds.
#  the log file shall contain CSV output.
#
#  Check https://www.speedtest.net/apps/cli to download the speedtest executable.
#
#  CTRL+C CTRL+C is the only exit option :)
#
#  Tested on Windows 11
if __name__ == "__main__":
    try:
        print("\n Speed testing Module")
        f = open(sys.argv[2], 'w')

        while(1):
            t = time.asctime(time.localtime())
            o = subprocess.run(sys.argv[1] + " --format=csv", capture_output=True, text=True)
            (down, up) = getUpDownSpeeds(o.stdout)
            out = t + ',' + str(down) + ',' + str(up) + ',' + o.stdout
            f.write(out)
            f.flush()
            out = t + ',' + "{:.2f}".format(down) + ' mbps, ' + "{:.2f}".format(up) + ' mbps'
            print(out)

            showWaitingBar(300)
    except Exception as e:
        print(type(e))
        print(str(e))
        print(traceback.format_exc())
