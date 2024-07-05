from ast import match_case
from doctest import master
from logging import WARNING
import os
from re import S


class bcolors:
    HEADER = "\033[95m"
    OKBLUE = "\033[94m"
    OKCYAN = "\033[96m"
    OKGREEN = "\033[92m"
    WARNING = "\033[93m"
    FAIL = "\033[91m"
    ENDC = "\033[0m"
    BOLD = "\033[1m"
    UNDERLINE = "\033[4m"


ERROR = f"{bcolors.FAIL}ERROR{bcolors.ENDC}: "
WARN = f"{bcolors.WARNING}WARNING{bcolors.ENDC}: "
PASS = f"{bcolors.OKGREEN}OK{bcolors.ENDC}: "

dir_path = os.getcwd().split("/")[-1]

if dir_path != "bundles":
    print(f"ERROR: Run in bundles folder, current folder {dir_path}")
    exit(1)

master_bundle = []
comments = []
bundleItems = []

oldfile = ""

debug = input("Enable debug? (y/N): ").lower() == "y"

with open("bundle.properties") as f:
    oldfile = f.read()
    f.seek(0)
    print("Reading bundle.properties...")
    for line in f.readlines():
        line = line.strip()

        if len(line) < 4:
            continue

        if line.startswith("##"):
            comment = line.strip().split("#")[-1].strip().upper()
            comments.append(comment)
            master_bundle.append(("comment", comments[-1]))
            if debug:
                print(f"Comment: {comment}")
            continue

        bundleName = line.split("=")[0].strip()
        if all([x[0] != bundleName for x in bundleItems]):
            bundleArr = line.strip().split(" = ")
            if len(bundleArr) != 2:
                print(f"{ERROR}Invalid line: {line}")
                exit(1)

            bundleItems.append(tuple([x.strip() for x in bundleArr]))
            master_bundle.append(("entry", bundleItems[-1]))
            if debug:
                print(
                    f'Bundle Entry: "{bundleArr[0].strip()}" - "{bundleArr[1].strip()}"'
                )
        else:
            print(f'{WARNING}Duplicate line "{line.strip()}". Please check or abort')

newfile = ""

for item in master_bundle:
    match item[0]:
        case "comment":
            newfile += "\n## "
            newfile += item[1]
        case "entry":
            newfile += item[1][0]
            newfile += " = "
            newfile += item[1][1]
        case _:
            print(f'{ERROR}Invalid id from item "{item}"')
            exit(1)

    newfile += "\n"

newfile = newfile.lstrip()

if newfile != oldfile:
    answer = input(
        f"{WARN}Master bundle not formatted correctly, format or abort? (y/n/other to abort): "
    ).lower()
    if answer == "y":
        print("Writing to bundle.properties...")
        with open("bundle.properties", "w") as f:
            f.write(newfile)
        print(f"{PASS}Bundle reformatted")
    elif answer == "n":
        pass
    else:
        print(f"{ERROR}ABORTING")
        exit(1)
else:
    print(f"{PASS}Bundle is formatted correctly")

warned = False

bundleNames = []

print()
files = [f for f in os.listdir(".") if os.path.isfile(f)]
for f in files:
    if f.startswith("bundle_") and f.endswith(".properties"):
        fileExt = f.split("_")[1].split(".")[0]
        if len(fileExt) != 2:
            warned = True
            print(f"{WARNING}Possible bundle, but skipped: {f}")
        else:
            bundleNames.append(f)

if warned:
    print()

print("Bundles to be updated:")
[print(f" - {x}") for x in bundleNames]
print()

if input(f"Is this correct? (y/N): ").lower() != "y":
    print(f"{ERROR}ABORTED")
    exit(1)

for f in bundleNames:
    curBundle = []

    print()
    print(f"Doing bundle {f}:")
    with open(f) as file:
        for line in file.readlines():
            line = line.strip()

            if line.startswith("##"):
                continue
            if line.strip() == "":
                continue

            sline = line.split(" = ")
            if len(sline) != 2:
                print(f'- {ERROR}Wrong line "{line}"')
                exit(1)

            if all(sline[0].strip() != x[0] for x in bundleItems):
                print(
                    f' - {WARN}Entry "{line}" not in master bundle, delete? (y/N)',
                    end="",
                )
                if input() != "y":
                    print(f"{ERROR}ABORTED")
                    exit(1)
                continue
            curBundle.append((sline[0], sline[1]))

        for entry in bundleItems:
            if all(entry[0] != x[0] for x in curBundle):
                TO_DO = "<TODO: LOCALIZE>"
                print(f' - Creating entry "{entry[0]} = {entry[1]} {TO_DO}"')
                curBundle.append((entry[0], entry[1] + f" {TO_DO}"))

    newfile = ""

    for item in master_bundle:
        match item[0]:
            case "comment":
                newfile += "\n## "
                newfile += item[1]
            case "entry":
                entry = []

                for e in curBundle:
                    if e[0] == item[1][0]:
                        entry = e
                        break

                newfile += entry[0]
                newfile += " = "
                newfile += entry[1]

        newfile += "\n"

    with open(f, "w") as f:
        f.write(newfile)

    print(f" - {PASS}Saved {f.name}")
