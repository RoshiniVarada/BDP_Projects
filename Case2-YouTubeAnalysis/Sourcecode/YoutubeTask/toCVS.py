import csv

with open('output/part-r-00000', newline='') as f:
    reader = csv.reader(f)
    listOfStrngs = []
    print("input: ")
    for row in reader:
        print(row)
        listOfStrngs.append(row)

    listOfStrngs2 = []
    for i in listOfStrngs:
        listOfStrngs2.append(i.__str__().replace("\\t", ", "))
    toCSV = []
    for i in listOfStrngs2:
        x = i.replace("['", "").replace("']", "").replace(" ", "").strip()
        toCSV.append(x)

f = open("output/part-r-00000.csv", "w")
print("to be exported:")
for i in toCSV:
    x = i.replace(",", "\t") + '\n'
    print(x, end="")
    f.write(x)
f.close()

# Terminal commands
# sort -nr -k2  part-r-00000.csv | head -n5
# sort -nr -k2  part-r-00000 | head -n10
