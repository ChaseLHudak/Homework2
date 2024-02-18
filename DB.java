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
    //int recordSize = 102;
    private boolean isOpen = false;
    private RandomAccessFile din;
    private RandomAccessFile file;

    private int numRecords;
    private int recordNum;
    private String id, lastName, firstName, age, ticketNum, fare, purchaseDate;

    //Field Size Limit
    private final int idSize = 9;
    private final int lNameSize = 20;
    private final int fNameSize = 25;
    private final int ageSize = 6;
    private final int tNumSize = 18;
    private final int fareSize = 8;
    private final int pDateSize = 14;

    //CONSTRUCTOR
    public DB() {
        this.din = null;
        this.file = null;
        this.numRecords = 0;
    }

    //OPEN FILE
    public static RandomAccessFile openFile(String fileName) throws IOException {
        return new RandomAccessFile(fileName, "rw");
    }

    //OPEN
    public boolean open(String fileName) throws IOException{
        //Opens and reads .config file
        try {
            this.din = new RandomAccessFile(fileName+".config","r");
            String[] line = (this.din.readLine()).split(" ");
            this.numRecords = Integer.parseInt(line[0]);
            this.recordSize = Integer.parseInt(line[1]);
        } 
        catch(FileNotFoundException e) {
            System.out.println("\nCould not find config file, please try again...");
            return false;
        }
        //Opens .data file
        try {
            this.din = new RandomAccessFile(fileName+".data", "r");
            isOpen = true;
            return true;
        }
        catch(FileNotFoundException e) {
            System.out.println("\nCould not find data file, please try again...");
            return false;
        }
    }

    //CLOSE
    public void close() {
        //Closes currently open database and sets variables to null
        try {
            din.close();
            din = null;
            file = null;
            numRecords = 0;
            isOpen = false;
        } 
        catch (IOException e) {
            System.out.println("There was an error while attempting to close the database file.\n");
        }
    }

    //IS OPEN
    public boolean isOpen() {
        return isOpen;
    }

    //WRITE RECORD
    private int writeRecord(int recordNum, String id, String lastName, String firstName, String age, String ticketNum, String fare, String purchaseDate, RandomAccessFile file) {
        //Writes Records To Given Database
        try {
            if (this.id != null) {
                file.skipBytes(recordSize * this.recordNum);
                file.writeBytes(String.format("%" + idSize + "s", id.substring(0, Math.min(this.id.length(), idSize))));
                file.writeBytes(String.format("%" + lNameSize + "s", lastName.substring(0, Math.min(this.lastName.length(), lNameSize))));
                file.writeBytes(String.format("%" + fNameSize + "s", firstName.substring(0, Math.min(this.firstName.length(), fNameSize))));
                file.writeBytes(String.format("%" + ageSize + "s", age.substring(0, Math.min(this.age.length(), ageSize))));
                file.writeBytes(String.format("%" + tNumSize + "s", ticketNum.substring(0, Math.min(this.ticketNum.length(), tNumSize))));
                file.writeBytes(String.format("%" + fareSize + "s", fare.substring(0, Math.min(this.fare.length(), fareSize))));
                file.writeBytes(String.format("%" + pDateSize + "s\n", purchaseDate.substring(0, Math.min(this.purchaseDate.length(), pDateSize))));
                numRecords++;
            } else {
                System.out.println("ID is null.");
                return 0;
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        return 1;
    }

    //EMPTY RECORD LINE
    private void emptyRecord() {
        //Creates an empty record line
        id = String.format("%-9s", "_empty_");
        lastName = String.format("%-20s", "NIL");
        firstName = String.format("%-25s", "NIL");
        age = String.format("%-6s", "NIL");
        ticketNum = String.format("%-18s", "NIL");
        fare = String.format("%-8s", "NIL");
        purchaseDate = String.format("%-14s", "NIL");
    }

    //READ CSV
    private void readCsv(String line) {
        //Reads line and extracts field values
        String[] attribute = line.split(",");
        id = String.format("%-9s", attribute[0]);
        lastName = String.format("%-20s", attribute[1]);
        firstName = String.format("%-25s", attribute[2]);
        age = String.format("%-6s", attribute[3]);
        ticketNum = String.format("%-18s", attribute[4]);
        fare = String.format("%-8s", attribute[5]);
        purchaseDate = String.format("%-14s", attribute[6]);
    }

    //CREATE DATABASE
    public void createDB(String fileName) throws IOException {
        //Creates a database from given csv file
        try {
            this.din = new RandomAccessFile(fileName+".csv", "r");
        }
        catch(FileNotFoundException e) {
            System.out.println("\nFile could not be located, please try again...");
            return;
        }
        RandomAccessFile file = openFile(fileName+".data");

        String line;
        recordNum = 0;
        numRecords = 0;
        while((line = this.din.readLine()) != null) {
            //Reads csv file and writes to data file
            readCsv(line);
            writeRecord(recordNum, id, lastName, firstName, age, ticketNum, fare, purchaseDate,file);
            
            //Adds empty record to data file
            emptyRecord();
            writeRecord(recordNum, id, lastName, firstName, age, ticketNum, fare, purchaseDate,file);
        }

        file.close();
        //Writes numRecords and recordSize to .config file
        file = openFile(fileName+".config");
        file.writeBytes(numRecords + " " + recordSize);
        file.close();
    }

    //READ RECORD
    public boolean readRecord(int recordNum, Record record) {
        //Creates and returns a record from given record number
        boolean Success = false;

        if (isOpen())
        {
           String[] fields;
           if(recordNum >= 0 && recordNum < this.numRecords) {
               try {
                   din.seek(0);
                   din.skipBytes(recordSize * recordNum);
   
                   fields = din.readLine().split("\\s{2,}", 0);
                   record.updateFields(fields);
                   Success = true;
                   }
               catch(IOException e) {
                   System.out.println("There was an error while attempting to read a record from the database file.\n");
                   e.printStackTrace();
               }
           }
           else {
              System.out.println("Record number: " + recordNum + " is out of range.\n");
           }
       }
       else
           System.out.println("Please open the database before trying to read records.\n");
       return Success;
    } 
   
   //DISPLAY RECORD
   public boolean displayRecord(String passengerId) throws IOException {
       if (!isOpen()) {
           System.out.println("Database is not open.");
           return false;
       }

       if (!binarySearch(passengerId)) {
           System.out.println("Record with passenger ID " + passengerId + " not found.");
           return false;
       }

       Record record = new Record();
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

   //BINARY SEARCH
   public boolean binarySearch(String passengerId) throws IOException {
       if (!isOpen()) {
           System.out.println("Database is not open.");
           return false;
       }

       int low = 0;
       int high = numRecords - 1;

       while (low <= high) {
           int mid = (low + high) / 2;
           Record record = new Record();
           if (readRecord(mid, record)) {
               if (record.id.equals(passengerId)) {
                   recordNum = mid; // Set recordNum to the index of the found record
                   return true;
               } else if (record.id.compareTo(passengerId) < 0) {
                   low = mid + 1;
               } else {
                   high = mid - 1;
               }
           } else {
               System.out.println("Error reading record.");
               return false;
           }
       }

       return false; // Record not found
   }

    //CREATE REPORT
    public boolean createReport() throws IOException {
        if (!isOpen()) {
            System.out.println("Database is not open.");
            return false;
        }

        for (int i = 0; i < 20; i++) {
            Record record = new Record();
            if (readRecord(i, record)) {
                if (!record.id.equals("_empty_")) {
                    System.out.println("Record " + ((i/2) + 1) + ":");
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
            case 1: return lNameSize;
            case 2: return fNameSize;
            case 3: return ageSize;
            case 4: return tNumSize;
            case 5: return fareSize;
            case 6: return pDateSize;
            default: return 0; // Handle this case appropriately
        }
    }

//    // DELETE RECORD
//    public boolean deleteRecord(int recordNumber) throws IOException {
//        if (!isOpen()) {
//            System.out.println("Database is not open.");
//            return false;
//        }
//
//        Record record = new Record();
//        if (readRecord(recordNumber, record)) {
//            // Overwrite the record with an empty one
//            Record emptyRecord = new Record();
//            writeRecord(recordNumber, emptyRecord);
//            System.out.println("Record " + recordNumber + " deleted successfully.");
//            return true;
//        } else {
//            System.out.println("Record " + recordNumber + " not found.");
//            return false;
//        }
//    }
//
//    // ADD RECORD
//    public boolean addRecord(Record newRecord) throws IOException {
//        if (!isOpen()) {
//            System.out.println("Database is not open.");
//            return false;
//        }
//
//        int emptyRecordNumber = findEmptyRecord();
//        if (emptyRecordNumber != -1) {
//            // Write the new record to the empty record found
//            writeRecord(emptyRecordNumber, newRecord);
//            System.out.println("Record added successfully.");
//            return true;
//        } else {
//            // If no empty record found, double the file size and update the config file
//            // (implementation not provided here)
//            System.out.println("No empty record found. Implement file resizing logic.");
//            return false;
//        }
//    }

    private int findEmptyRecord() throws IOException {
        for (int i = 0; i < 10; i++) { // Assuming there are 10 records
            Record record = new Record();
            if (readRecord(i, record) && record.isEmpty()) {
                return i;
            }
        }
        return -1; // No empty record found
    }

}
