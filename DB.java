
//Author: Keidan Smith
//Program Name: DB.java
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;

//DB CLASS
public class DB {
    int recordSize = 101;
    // int recordSize = 102;
    private boolean isOpen = false;
    private RandomAccessFile din;
    private RandomAccessFile file;

    private int numRecords;
    private int recordNum;
    private String id, lastName, firstName, age, ticketNum, fare, purchaseDate;

    // Field Size Limit
    private final int idSize = 9;
    private final int lNameSize = 20;
    private final int fNameSize = 25;
    private final int ageSize = 6;
    private final int tNumSize = 18;
    private final int fareSize = 8;
    private final int pDateSize = 14;
    private String openFileName;

    // CONSTRUCTOR
    public DB() {
        this.din = null;
        this.file = null;
        this.numRecords = 0;
    }

    // OPEN FILE
    public static RandomAccessFile openFile(String fileName) throws IOException {
        return new RandomAccessFile(fileName, "rw");
    }

    // OPEN
    public boolean open(String fileName) throws IOException {
        // Opens and reads .config file
        try {
            this.din = new RandomAccessFile(fileName + ".config", "r");
            String[] line = (this.din.readLine()).split(" ");
            this.numRecords = Integer.parseInt(line[0]);
            this.recordSize = Integer.parseInt(line[1]);
        } catch (FileNotFoundException e) {
            System.out.println("\nCould not find config file, please try again...");
            openFileName = "";
            return false;
        }
        // Opens .data file
        try {
            this.din = new RandomAccessFile(fileName + ".data", "r");
            isOpen = true;
            openFileName = fileName;
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("\nCould not find data file, please try again...");
            openFileName = "";
            return false;
        }
    }

    // CLOSE
    public void close() {
        // Closes currently open database and sets variables to null
        try {
            din.close();
            din = null;
            file = null;
            numRecords = 0;
            isOpen = false;
            openFileName = "";
        } catch (IOException e) {
            System.out.println("There was an error while attempting to close the database file.\n");
        }
    }

    // IS OPEN
    public boolean isOpen() {
        return isOpen;
    }

    // WRITE RECORD
    private int writeRecord(int recordNum, String id, String lastName, String firstName, String age, String ticketNum,
            String fare, String purchaseDate, RandomAccessFile file) {
        // Writes Records To Given Database
        // Delete
        try {
            if (id != null) { // Previous this.id
                System.out.println(id + (id != null) + " IN WRITE" + recordNum + " LastName " + lastName);
                if (recordNum != 0) {
                    file.skipBytes(recordSize * recordNum);
                }

                System.out.println("Passenger ID: " + id);
                System.out.println("Last Name: " + lastName);
                System.out.println("First Name: " + firstName);
                System.out.println("Age: " + age);
                System.out.println("Ticket Number: " + ticketNum);
                System.out.println("Fare: " + fare);
                System.out.println("Purchase Date: " + purchaseDate);
                // file.skipBytes(recordSize * recordNum);
                file.writeBytes(String.format("%" + idSize + "s", id.substring(0, Math.min(id.length(), idSize))));
                file.writeBytes(String.format("%" + lNameSize + "s",
                        lastName.substring(0, Math.min(lastName.length(), lNameSize))));
                file.writeBytes(String.format("%" + fNameSize + "s",
                        firstName.substring(0, Math.min(firstName.length(), fNameSize))));
                file.writeBytes(
                        String.format("%" + ageSize + "s", age.substring(0, Math.min(age.length(), ageSize))));
                file.writeBytes(String.format("%" + tNumSize + "s",
                        ticketNum.substring(0, Math.min(ticketNum.length(), tNumSize))));
                file.writeBytes(
                        String.format("%" + fareSize + "s", fare.substring(0, Math.min(fare.length(), fareSize))));
                file.writeBytes(String.format("%" + pDateSize + "s\n",
                        purchaseDate.substring(0, Math.min(purchaseDate.length(), pDateSize))));
                numRecords++;
            } else {
                System.out.println("ID is null.");
                return 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 1;
    }

    // EMPTY RECORD LINE
    private void emptyRecord() {
        // Creates an empty record line
        id = String.format("%-9s", "_empty_");
        lastName = String.format("%-20s", "NIL");
        firstName = String.format("%-25s", "NIL");
        age = String.format("%-6s", "NIL");
        ticketNum = String.format("%-18s", "NIL");
        fare = String.format("%-8s", "NIL");
        purchaseDate = String.format("%-14s", "NIL");
    }

    // READ CSV
    private void readCsv(String line) {
        // Reads line and extracts field values
        String[] attribute = line.split(",");
        id = String.format("%-9s", attribute[0]);
        lastName = String.format("%-20s", attribute[1]);
        firstName = String.format("%-25s", attribute[2]);
        age = String.format("%-6s", attribute[3]);
        ticketNum = String.format("%-18s", attribute[4]);
        fare = String.format("%-8s", attribute[5]);
        purchaseDate = String.format("%-14s", attribute[6]);
    }

    // CREATE DATABASE
    public void createDB(String fileName) throws IOException {
        // Creates a database from given csv file
        try {
            this.din = new RandomAccessFile(fileName + ".csv", "r");
        } catch (FileNotFoundException e) {
            System.out.println("\nFile could not be located, please try again...");
            return;
        }
        RandomAccessFile file = openFile(fileName + ".data");

        String line;
        recordNum = 0;
        numRecords = 0;
        while ((line = this.din.readLine()) != null) {
            // Reads csv file and writes to data file
            readCsv(line);
            writeRecord(recordNum, id, lastName, firstName, age, ticketNum, fare, purchaseDate, file);

            // Adds empty record to data file
            emptyRecord();
            writeRecord(recordNum, id, lastName, firstName, age, ticketNum, fare, purchaseDate, file);
        }

        file.close();
        // Writes numRecords and recordSize to .config file
        file = openFile(fileName + ".config");
        file.writeBytes(numRecords + " " + recordSize);
        file.close();
    }

    // READ RECORD
    public boolean readRecord(int recordNum, Record record) {
        // Creates and returns a record from given record number
        boolean Success = false;

        if (isOpen()) {
            String[] fields;
            if (recordNum >= 0 && recordNum < this.numRecords) {
                try {
                    din.seek(0);
                    din.skipBytes(recordSize * recordNum);

                    fields = din.readLine().split("\\s{2,}", 0);
                    record.updateFields(fields);
                    Success = true;
                } catch (IOException e) {
                    System.out
                            .println("There was an error while attempting to read a record from the database file.\n");
                    e.printStackTrace();
                }
            } else {
                System.out.println("Record number: " + recordNum + " is out of range.\n");
            }
        } else
            System.out.println("Please open the database before trying to read records.\n");
        return Success;
    }

    // DISPLAY RECORD
    public boolean displayRecord(String passengerId) throws IOException {
        int recordFoundNum[] = new int[1];
        if (!isOpen()) {
            System.out.println("Database is not open.");
            return false;
        }
        Record record = new Record();
        if (!binarySearch(passengerId, recordFoundNum, record)) {
            System.out.println("Record with passenger ID " + passengerId + " not found.");
            return false;
        }

        if (readRecord(recordNum, record)) {
            System.out.println("Record found:");
            System.out.println("Passenger ID: " + record.id);
            System.out.println("Last Name: " + record.lastName);
            System.out.println("First Name: " + record.firstName);
            System.out.println("Age: " + record.age);
            System.out.println("Ticket Number: " + record.ticketNum);
            System.out.println("Fare: " + record.fare);
            System.out.println("Purchase Date: " + record.purchaseDate);
            return true;
        } else {
            System.out.println("Error reading record.");
            return false;
        }
    }

    // // BINARY SEARCH
    // public boolean binarySearch(String passengerId) throws IOException {
    // if (!isOpen()) {
    // System.out.println("Database is not open.");
    // return false;
    // }

    // int low = 0;
    // int high = numRecords - 1;

    // while (low <= high) {
    // int mid = (low + high) / 2;
    // Record record = new Record();
    // if (readRecord(mid, record)) {
    // if (record.id.equals(passengerId)) {
    // recordNum = mid; // Set recordNum to the index of the found record
    // return true;
    // } else if (record.id.compareTo(passengerId) < 0) {
    // low = mid + 1;
    // } else {
    // high = mid - 1;
    // }
    // } else {
    // System.out.println("Error reading record.");
    // return false;
    // }
    // }

    // return false; // Record not found
    // }

    /**
     * Binary Search by record Id
     * 
     * @param Id
     * @param recordNum to store record num location
     * @param record    to store the fields
     */
    public boolean binarySearch(String Id, int[] recordNum, Record record) {
        int Low = 0;
        int High = numRecords - 1;
        boolean Found = false;
        Record tempRecord = new Record();

        while (!Found && High >= Low) {
            int Middle = (Low + High) / 2;

            readRecord(Middle, tempRecord);
            System.out.println("TempRecord: " + tempRecord + " Middle: " + Middle);
            if (tempRecord == null || tempRecord.id.equals("_empty_")) {

                int nonEmptyRecord = findEmptyRecord(Middle, Low, High);
                System.out.println(" tempRecord.id ==" + tempRecord.id + " NonEmptyRecord " + nonEmptyRecord);
                if (nonEmptyRecord == -1) {
                    recordNum[0] = High; // Set to next index if no non-empty record is found
                    System.out.println("Could not find record with ID " + Id);
                    return false;
                }
                Middle = nonEmptyRecord;
                readRecord(Middle, tempRecord);

                if (Integer.parseInt(tempRecord.id) > Integer.parseInt(Id)) {
                    recordNum[0] = Middle - 1;
                } else {
                    recordNum[0] = Middle + 1;
                }
            }

            if (tempRecord != null && !tempRecord.id.equals("_empty_")) {

                int result = Integer.parseInt(tempRecord.id) - Integer.parseInt(Id);
                System.out.println(tempRecord.id + " - " + Id + " =" + result + "=");
                if (result == 0) {
                    Found = true;
                    System.err.println("in found");
                    recordNum[0] = Middle;
                    // Update the record object with the found record
                    // (Assuming record has these fields)
                    if (record.lastName != "NIL") {
                        record.id = tempRecord.id;
                        record.lastName = tempRecord.lastName;
                        record.firstName = tempRecord.firstName;
                        record.age = tempRecord.age;
                        record.ticketNum = tempRecord.ticketNum;
                        record.fare = tempRecord.fare;
                        record.purchaseDate = tempRecord.purchaseDate;
                    }
                    System.err.println(file + " fileName");
                    int tf = writeRecord(Middle, record.id, record.lastName, record.firstName, record.age,
                            record.ticketNum, record.fare, record.purchaseDate, file);
                    if (tf == 1) {
                        System.out.println(Middle + " Record Deleted");
                    } else {
                        System.out.println(Middle + " Record Not Deleted");
                    }

                } else if (result < 0) {
                    Low = Middle + 1;
                } else {
                    High = Middle - 1;
                }
            }
        }

        if (!Found || recordNum[0] == 0) {
            recordNum[0] = High; // Set to next index if no suitable spot is found
            System.out.println("Could not find record with ID " + Id + ".");
        }

        return Found;
    }

    // CREATE REPORT
    public boolean createReport() throws IOException {
        if (!isOpen()) {
            System.out.println("Database is not open.");
            return false;
        }

        for (int i = 0; i < 20; i++) {
            Record record = new Record();
            if (readRecord(i, record)) {
                if (!record.id.equals("_empty_")) {
                    System.out.println("Record " + ((i / 2) + 1) + ":");
                    System.out.println("Passenger ID: " + record.id);
                    System.out.println("Last Name: " + record.lastName);
                    System.out.println("First Name: " + record.firstName);
                    System.out.println("Age: " + record.age);
                    System.out.println("Ticket Number: " + record.ticketNum);
                    System.out.println("Fare: " + record.fare);
                    System.out.println("Purchase Date: " + record.purchaseDate);
                } else {
                    System.out.println("\n");
                }
            } else {
                System.out.println("Error reading record " + (i + 1) + ".");
            }
        }

        return true;
    }

    // UPDATE RECORD
    // UPDATE RECORD
    public void updateRecord(int recordNum, String oldField, String newField) {
        if (!isOpen()) {
            System.out.println("Database is not open.");
            return;
        }

        if (recordNum < 0 || recordNum >= numRecords) {
            System.out.println("Invalid record number.");
            return;
        }

        try {
            RandomAccessFile file = new RandomAccessFile("Titanic.data", "rw");
            long offset = recordNum * recordSize; // Calculate the offset for the record
            file.seek(offset); // Move to the position of the record

            // Read the existing line
            String line = file.readLine();
            String[] fields = line.split("\\s{2,}", 0);

            // Find the index of the oldField
            int fieldIndex = -1;
            switch (oldField) {
                case "lastName":
                    fieldIndex = 1;
                    break;
                case "firstName":
                    fieldIndex = 2;
                    break;
                case "age":
                    fieldIndex = 3;
                    break;
                case "ticketNum":
                    fieldIndex = 4;
                    break;
                case "fare":
                    fieldIndex = 5;
                    break;
                case "purchaseDate":
                    fieldIndex = 6;
                    break;
                default:
                    System.out.println("Invalid field.");
                    file.close();
                    return;
            }

            // Update the field with the new value
            if (fieldIndex != -1) {
                // Pad the new field value with spaces to match the field size
                String paddedField = String.format("%-" + getFieldSize(fieldIndex) + "s", newField);
                fields[fieldIndex] = paddedField;
                // Rewrite the entire record back to the file
                file.seek(offset); // Move back to the same position
                file.writeBytes(String.join("  ", fields)); // Rewrite the modified record
                System.out.println("Record updated successfully.");
            }

            file.close();
        } catch (IOException e) {
            System.out.println("Error updating record.");
            e.printStackTrace();
        }
    }

    // Helper method to get the size of each field based on the field index
    private int getFieldSize(int index) {
        switch (index) {
            case 1:
                return lNameSize;
            case 2:
                return fNameSize;
            case 3:
                return ageSize;
            case 4:
                return tNumSize;
            case 5:
                return fareSize;
            case 6:
                return pDateSize;
            default:
                return 0; // Handle this case appropriately
        }
    }

    // DELETE RECORD
    public boolean deleteRecord(int recordNumber) throws IOException {
        if (!isOpen()) {
            System.out.println("Database is not open.");
            return false;
        }

        Record record = new Record();
        if (readRecord(recordNumber, record)) {
            // Overwrite the record with an empty one
            Record emptyRecord = new Record();
            emptyRecord.empty();

            binarySearch(String.valueOf(recordNumber), null, emptyRecord);
            // writeRecord(recordNumber, emptyRecord.id, emptyRecord.lastName,
            // emptyRecord.firstName, emptyRecord.age,emptyRecord.ticketNum,
            // emptyRecord.fare, emptyRecord.purchaseDate, file);
            // String id, String lastName, String firstName, String age, String ticketNum,
            // String fare, String purchaseDate, RandomAccessFile file
            System.out.println("Record " + recordNumber + " deleted successfully.");
            return true;
        } else {
            System.out.println("Record " + recordNumber + " not found.");
            return false;
        }
    }

    // // ADD RECORD
    // public boolean addRecord(Record newRecord) throws IOException {
    // if (!isOpen()) {
    // System.out.println("Database is not open.");
    // return false;
    // }

    // int emptyRecordNumber = findEmptyRecord();
    // if (emptyRecordNumber != -1) {
    // // Write the new record to the empty record found
    // writeRecord(emptyRecordNumber, newRecord);
    // System.out.println("Record added successfully.");
    // return true;
    // } else {
    // // If no empty record found, double the file size and update the config file
    // // (implementation not provided here)
    // System.out.println("No empty record found. Implement file resizing logic.");
    // return false;
    // }
    // }
    // int nonEmptyRecord = findEmptyRecord(Middle, Low, High);

    // private int findEmptyRecord(int recordN) throws IOException {
    // for (int i = recordN; i < recordSize; i++) { // Assuming there are 10 records
    // Record record = new Record();
    // if (readRecord(i, record) && record.isEmpty()) {
    // return i;
    // }
    // }
    // return -1; // No empty record found
    // }

    private int findEmptyRecord(int start, int lowLimit, int highLimit) {
        int backStep = 1; // Step size for backward search
        int forwardStep = 1; // Step size for forward search

        // Loop to search in both directions
        while (true) {
            // Check backwards
            if (start - backStep >= lowLimit) {
                Record record = new Record();
                readRecord(start - backStep, record);
                if (record != null && !record.id.equals("_empty_")) {
                    return start - backStep;
                }
                backStep += 1;
            }

            // Check forwards
            if (start + forwardStep <= highLimit) {
                Record record = new Record();
                readRecord(start + forwardStep, record);
                if (record != null && !record.id.equals("_empty_")) {
                    return start + forwardStep;
                }
                forwardStep += 1;
            }

            // Terminate if we've reached the end of the search range
            if (start - backStep < lowLimit && start + forwardStep > highLimit) {
                break;
            }
        }

        return -1; // No non-empty record found
    }
}
