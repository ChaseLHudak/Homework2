//Author: Ben Keller, Chase Hudak

import java.io.IOException;

public class Record {

    private boolean empty;
    public String id, firstName, lastName, age, ticketNum, fare, purchaseDate;

    public Record() {
        empty = true;
        id = ""; // Initialize id to an empty string
    }

    public void updateFields(String[] fields) throws IOException {
        if (fields.length == 7) {
            this.id = fields[0];
            this.lastName = fields[1];
            this.firstName = fields[2];
            this.age = fields[3];
            this.ticketNum = fields[4];
            this.fare = fields[5];
            this.purchaseDate = fields[6];

            empty = false;
        } else {
            throw new IOException();
        }
    }

    public void empty() {
        id = "_empty_";
        lastName = "NIL";
        firstName = "NIL";
        age = "NIL";
        ticketNum = "NIL";
        fare = "NIL";
        purchaseDate = "NIL";
    }

    public boolean isEmpty() {
        return empty;
    }

    public String toString() {
        return "Id: " + this.id +
                ", Last Name: " + this.lastName +
                ", First Name: " + this.firstName +
                ", Age: " + this.age +
                ", Ticket Number: " + this.ticketNum +
                ", Fare: " + this.fare +
                ", Date of Purchase: " + this.purchaseDate;
    }
}
