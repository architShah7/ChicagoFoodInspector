import csv

with open('inspections.csv', 'r') as infile, open('inspections-out.csv', 'w') as outfile:
    reader = csv.reader(infile, delimiter=',', quotechar='"')
    writer = csv.writer(outfile, delimiter=',', quotechar='"', quoting=csv.QUOTE_ALL)

    for row in reader:
        writer.writerow(row)
    print(0)
