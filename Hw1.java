//Author: Keidan Smith and Susan Gauch
//Program Name: Hw1.java
import java.io.IOException;
import java.util.Scanner;

//TESTDB CLASS
public class Hw1 {

    // MAIN
    public static void main(String[] args) throws IOException {
        // Initalize database, record, and scanner
        DB db = new DB();
        Record record = new Record();
        Scanner scanner = new Scanner(System.in);
        String command;
        boolean Done = false;

        // Main Menu Loop
        loop: while (!Done) {
            System.out.println(
                    "\nMain Menu: \n" +
                            "1) Create new database\n" +
                            "2) Open database\n" +
                            "3) Close database\n" +
                            "4) Read record\n" +
                            "5) Display record\n" +
                            "6) Update record\n" +
                            "7) Create report\n" +
                            "8) Add record\n" +
                            "9) Delete record\n" +
                            "10) Quit\n");

            System.out.print("Enter number: ");
            command = scanner.nextLine();

            // Switch to handle user input
            switch (command) {
                // 1) Create New Database
                case "1":
                    System.out.print("Please enter name of csv file: ");
                    String fileName = scanner.nextLine();
                    db.createDB(fileName);
                    break;
                // 2) Open Database
                case "2":
                    System.out.print("Enter name of database to open: ");
                    String databaseName = scanner.nextLine();
                    if (!db.isOpen()) {
                        if (db.open(databaseName))
                            System.out.println("\nOpening database...");
                    } else {
                        System.out.println("\nPlease close current database before attempting to open another one...");
                    }
                    break;
                // 3) Close Database
                case "3":
                    if (db.isOpen()) {
                        System.out.println("\nClosing database...");
                        db.close();
                    } else {
                        System.out.println("\nThere is not a database currently open...");
                    }
                    break;
                // 4) Read Record
                case "4":
                    System.out.print("Please enter record number to read: ");
                    String rNum = scanner.nextLine();
                    record = new Record();
                    try {
                        if (db.readRecord(Integer.parseInt(rNum), record)) {
                            System.out.println("\nRecordNum " + rNum + ": " + record.toString());
                        } else {
                            System.out.println("RecordNum " + rNum + ": Could not be read");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("\nThis is not a valid number, please try again...");
                        break;
                    }
                    break;
                case "5":
                    System.out.print("Please enter passenger id to display: ");
                    String pID = scanner.nextLine();
                    boolean found = db.displayRecord(pID);
                    if (!found) {
                        System.out.println("Record with passenger id " + pID + " not found.");
                    }
                    break;
                case "6":
                    System.out.print("Please enter a record number to update: ");
                    String rNum2 = scanner.nextLine();
                    System.out.print("Please choose a field(lastName, firstName, age, ticketNum, fare, purchaseDate): ");
                    String oldField = scanner.nextLine();
                    System.out.print("Please chose what to replace it with: ");
                    String newField = scanner.nextLine();
                    db.updateRecord(Integer.parseInt(rNum2), oldField, newField);
                    break;
                case "7":
                    db.createReport();
                    break;
                case "8":
                    System.out.println("Enter details for the new record:");
                    System.out.print("Record num to overwrite: ");
                    String recordNum = scanner.nextLine();
                    System.out.print("ID: ");
                    String newId = scanner.nextLine();
                    System.out.print("Last Name: ");
                    String newLastName = scanner.nextLine();
                    System.out.print("First Name: ");
                    String newFirstName = scanner.nextLine();
                    System.out.print("Age: ");
                    String newAge = scanner.nextLine();
                    System.out.print("Ticket Number: ");
                    String newTicketNum = scanner.nextLine();
                    System.out.print("Fare: ");
                    String newFare = scanner.nextLine();
                    System.out.print("Purchase Date: ");
                    String newPurchaseDate = scanner.nextLine();

                    Record newRecord = new Record();
                    newRecord.id = newId;
                    newRecord.lastName = newLastName;
                    newRecord.firstName = newFirstName;
                    newRecord.age = newAge;
                    newRecord.ticketNum = newTicketNum;
                    newRecord.fare = newFare;
                    newRecord.purchaseDate = newPurchaseDate;

                    db.addRecord(Integer.parseInt(recordNum), newRecord);
                    break;
                case "9":
                    System.out.print("Please enter a record number to delete: ");
                    int inputNum[] = new int[1];
                    String dNum = scanner.nextLine();
                    Record empty = new Record();
                    empty.empty();
                    System.out.println(empty.lastName);
                    Boolean outPut = db.binarySearch(dNum, inputNum, empty);
                    Boolean output = db.deleteRecord(Integer.parseInt(dNum));
                    break;
                case "10":
                    if (db.isOpen())
                        System.out.println("\nPlease close the database before quitting.\n");
                    else
                        Done = true;
                    break;
                default:
                    System.out.println("\nNot a valid command, please try again...");
            }
        }
        scanner.close();
    }
}
