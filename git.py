import subprocess
import os
import datetime

class Git():
    def __init__(self, repodir: str):
        self.repodir = repodir

        self.git_status = "cd " + self.repodir + ";" + "git status"
        self.git_init = "cd " + self.repodir + ";" + "git init"
        self.git_add = "cd " + self.repodir + ";" + "git add ."

        completed_process = subprocess.run("cat /sys/class/dmi/id/modalias", stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True,
                                           universal_newlines=True)
        self.machineid = completed_process.stdout[:50]
        self.git_commit = "cd " + self.repodir + ";" + "git commit -m \"" + self.machineid

    def is_repo(self):
        completed_process = subprocess.run(self.git_status, stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True,
                                           universal_newlines=True)
        return True if completed_process.returncode == 0 else False

    def initrepo(self):
        completed_process = subprocess.run(self.git_init, stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True,
                                           universal_newlines=True)

    def hasprofile(self, profilename):
        if not self.is_repo():
            self.initrepo()

        return os.path.isfile(self.repodir + "/" + profilename + ".json")

    def create_profile(self, profilename):
        os.open(self.repodir + "/" + profilename + ".json", os.O_CREAT)
        subprocess.run(self.git_add, stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True,
                                           universal_newlines=True)
        subprocess.run(self.git_commit + " " + datetime.datetime.now().isoformat() + "\"", stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True,
                                           universal_newlines=True)


