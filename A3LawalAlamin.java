/**
 * Assignment3
 * COMP 2140 SECTION A01
 * INSTRUCTOR: HELEN CAMERON
 * ASSIGNMENT: Assignment 3
 * Author: Al-amin Lawal, 7833358
 * Version: 11/11/2019
 *
 * Purpose: The purpose of this program is to model an elevator using stacks and queues
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;
import java.util.ArrayList;

public class A3LawalAlamin {

    public static void main(String[] args) {
        elevatorModel();
    }

    // This method is in charge of reading the textfile containing details about the employee and elevator
    // It is called in the main method
    public static void elevatorModel() {
        BufferedReader reader;
        String line;
        Scanner keyboard;
        String filename;
        // Allow user to choose file with keyboard input
        keyboard = new Scanner(System.in);
        System.out.println("\nEnter the input file");
        filename = keyboard.nextLine();

        try {
            reader = new BufferedReader(new FileReader(filename));
            line = reader.readLine();
            Scanner scan = new Scanner(line);
            Elevator newElevator = new Elevator(Integer.parseInt(scan.next()), Integer.parseInt(scan.next()));
            String newLine;
            System.out.println("Elevator dimensions");
            System.out.println("    Number of floors to service: " + newElevator.getNumFloors());
            System.out.println("    Maximum number of people (load capacity): " + newElevator.getElevatorCapacity());
            System.out.println("Elevator begins on floor 0");
            while ((newLine =reader.readLine())!=null) {
                Scanner newScan = new Scanner(newLine);
                Employee newEmployee = new Employee(Integer.parseInt(newScan.next()),Integer.parseInt(newScan.next()),Integer.parseInt(newScan.next()),Integer.parseInt(newScan.next()));
                newElevator.processArrival(newEmployee);
                newScan.close();
            }
            reader.close();
            newElevator.runElevator();
            newElevator.printStatistics();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}

// Employee class containing arrivalTime, employeeID, arrivalFloor, destinationFloor and tripTime of the employee
class Employee{
    private int employeeID;
    private int arrivalTime;
    private int arrivalFloor;
    private int destinationFloor;
    private int tripTime;

    public Employee(int arrivalTime, int employeeID, int arrivalFloor, int destinationFloor) {
        this.employeeID = employeeID;
        this.arrivalTime = arrivalTime;
        this.arrivalFloor = arrivalFloor;
        this.destinationFloor = destinationFloor;
        tripTime = 0;
    }

    public void setTripTime(int time) {
        tripTime = time;
    }

    public int getTripTime() {
        return tripTime;
    }

    public int getEmployeeID(){
        return employeeID;
    }

    public int getArrivalTime(){
        return arrivalTime;
    }

    public int getWaitingFloor(){
        return arrivalFloor;
    }

    public int getDestinationFloor(){
        return destinationFloor;
    }
}


class Stack {

    private static final int MAX_SIZE = 8;
    private static int count = 1;
    private int top;
    private Employee[] stackArray;

    public Stack(int MAX_SIZE) {
        stackArray = new Employee[MAX_SIZE];
        top = -1;
    }

    public void push(Employee newEmployee) {
        if (isFull()) {
            System.out.println("Cannot be added, the stack is full");
        } else {
            top++;
            stackArray[top] = newEmployee;
        }

    }

    public boolean isEmpty() {
        return top == -1;
    }

    public boolean isFull() {
        boolean full = false;
        if (top >= MAX_SIZE-1) {
            full = true;
        }
        return full;
    }

    public Employee pop() {
        Employee removedEmployee = null;
        if (!isEmpty()) {
            removedEmployee = stackArray[top];
            top--;
        }
        return removedEmployee;
    }

    public Employee top() {
        Employee topEmployee = null;
        if (!isEmpty()) {
            topEmployee = stackArray[top];
        }
        return topEmployee;
    }


    public void printCount() {
        System.out.println(top);
    }
}



class Queue {
    private Node end;

    public Queue() {
        end = null;
    }


    private class Node {
        public Employee newEmployee;
        public Node next;

        public Node(Node newNext, Employee thisEmployee) {
            newEmployee = thisEmployee;
            next = newNext;
        }

        public Node(Employee thisEmployee) {
            newEmployee = thisEmployee;
        }
    }


    public void enter(Employee newEmployee) {
        if (isEmpty()) {
            end = new Node(null,newEmployee);
            end.next = end;
        } else {
            end.next = new Node(end.next, newEmployee);
            end = end.next;
        }
    }


    public Employee front() {
        Employee firstEmployee = null;
        if (!isEmpty()) {
            firstEmployee = end.next.newEmployee;
        }
        return firstEmployee;
    }


    public boolean isEmpty(){
        return end == null;
    }


    public Employee leave() {
        Employee removedEmployee = null;
        if (!isEmpty()){
            if (end.next == end) {
                removedEmployee = end.newEmployee;
                end = null;
            } else {
                removedEmployee = end.next.newEmployee;
                end.next = end.next.next;
            }
        }
        return removedEmployee;
    }
}




class Elevator{

    private int elevatorCapacity;
    private int numFloors;
    private int[] floors;
    private Queue [][] queueArray;
    private int direction;
    private int time;
    private int currentFloor;
    private Stack employees;
    private int numTrips;
    // ArrayList of the tripTimes of all employees in the text file
    ArrayList<Employee> tripTimes;


    public Elevator(int elevatorCapacity, int numFloors) {
        this.elevatorCapacity = elevatorCapacity;
        this.numFloors = numFloors;
        floors = new int[numFloors];
        queueArray = new Queue[numFloors][2];
        for (int i = 0; i < numFloors; i++) {
            for (int j = 0; j < 2; j++) {
                queueArray[i][j] = new Queue();
            }
        }
        // 0 is represented as elevator going up
        // 1 is represented as elevator going down
        direction = 0;
        time = 0;
        currentFloor = 0;
        employees = new Stack(elevatorCapacity);
        tripTimes = new ArrayList<Employee>();
        numTrips = 0;
    }

    // Method to process an employee arrival
    public void processArrival(Employee newEmployee){
        while (newEmployee.getArrivalTime() != time) {
            simulateArrival();
        }
        if (newEmployee.getArrivalTime() == time) {
            if (newEmployee.getWaitingFloor() > newEmployee.getDestinationFloor() ) {
                queueArray[newEmployee.getWaitingFloor()][1].enter(newEmployee);
                System.out.println("Time " + time + ": A person begins waiting to go down: Employee " + newEmployee.getEmployeeID() + ", arrival floor " + newEmployee.getWaitingFloor()
                            + ", arrival time " + newEmployee.getArrivalTime() + ",desired floor " + newEmployee.getDestinationFloor());

            } else if(newEmployee.getDestinationFloor() > newEmployee.getWaitingFloor()) {
                    queueArray[newEmployee.getWaitingFloor()][0].enter(newEmployee);
                    System.out.println("Time " + time + ": A person begins waiting to go up: Employee " + newEmployee.getEmployeeID() + ", arrival floor " + newEmployee.getWaitingFloor()
                            + ", arrival time " + newEmployee.getArrivalTime() + ",desired floor " + newEmployee.getDestinationFloor());
            }
        }
    }

    // Method to model the elevator behaviour during one time unit
    private void simulateArrival() {
        ElevatorAction();
    }

    // Method to finish running the simulation after the last employee arrival
    public void runElevator() {
        while (!nobodyLeft() || !employees.isEmpty()) {
                ElevatorAction();
        }
    }


    private void ElevatorAction() {
        // Controls whether elevator changes direction or not
        if (currentFloor == numFloors - 1 || currentFloor == 0) {
            if (currentFloor == numFloors - 1 && direction == 0) {
                direction = 1;
                System.out.println("Time " + time + ": Elevator changed direction: Now going down");
            } else if (currentFloor == 0 && direction == 1) {
                System.out.println("Time " + time + ": Elevator changed direction: Now going up");
                direction = 0;
            }
        } else if (direction == 0) {
            if ((!waitingAbove() && waitingBelow()) && employees.isEmpty()) {
                System.out.println("Time " + time + "Elevator changed direction: Now going down");
                direction = 1;
            }
        } else if (direction == 1) {
            if ((waitingAbove() && !waitingBelow()) && employees.isEmpty()) {
                System.out.println("Time " + time + "Elevator changed direction: Now going up");
                direction = 0;
            }
        }


        // Controls whether elevator opens its doors
        if (floors[currentFloor] > 0 || (!queueArray[currentFloor][direction].isEmpty() && !employees.isFull())) {
            if (floors[currentFloor] > 0) {
                Stack tempStack = new Stack(numFloors);
                while (!employees.isEmpty()) {
                    Employee removedEmployee;
                    if (employees.top().getDestinationFloor() == currentFloor) {
                        removedEmployee = employees.pop();
                        floors[currentFloor] = floors[currentFloor] - 1;
                        System.out.println("Time " + time + ": Got off the elevator: Employee " + removedEmployee.getEmployeeID() + ", arrival floor " + removedEmployee.getWaitingFloor()
                                + ", arrival time " + removedEmployee.getArrivalTime() + ", desired floor " + removedEmployee.getDestinationFloor());
                        removedEmployee.setTripTime(time-removedEmployee.getArrivalTime());
                        tripTimes.add(removedEmployee);
                        numTrips++;
                    } else {
                        removedEmployee = employees.pop();
                        tempStack.push(removedEmployee);
                    }
                }
                if (!tempStack.isEmpty()) {
                    while (!tempStack.isEmpty()){
                        employees.push(tempStack.pop());
                    }
                }
            }

            if (!queueArray[currentFloor][direction].isEmpty() && !employees.isFull()) {
                while (!queueArray[currentFloor][direction].isEmpty() && !employees.isFull()) {
                    Employee addedEmployee = queueArray[currentFloor][direction].leave();
                    employees.push(addedEmployee);
                    floors[addedEmployee.getDestinationFloor()] = floors[addedEmployee.getDestinationFloor()] + 1;
                    System.out.println("Time " + time + ": Got on the elevator: Employee " + addedEmployee.getEmployeeID() + ", arrival floor " + addedEmployee.getWaitingFloor()
                            + ", arrival time " + addedEmployee.getArrivalTime() + ", desired floor " + addedEmployee.getDestinationFloor());
                }
            }
        } else {
            if (!employees.isEmpty() || checkIfWaiting()) {
                if (direction == 0) {
                    currentFloor = currentFloor+1;
                    System.out.println("Time " + time + ": Elevator moves up to floor " + currentFloor);
                } else {
                    currentFloor= currentFloor-1;
                    System.out.println("Time " + time + ": Elevator moves down to floor " + currentFloor);
                }
            }
        }
        time++;
    }

    // This method will check if there is anyone waiting in the current direction of the elevator. It could be above or below
    // Two helper methods are used
    // If the direction is 0(going up) then we call waitingAbove() which checks if someone is waiting above the currentFloor
    // If the direction is 1(going down), then we call waitingBelow() which checks if someone is waiting below the currentFloor
    private boolean checkIfWaiting() {
        boolean  waiting = false;
        if (direction == 0) {
            waiting = waitingAbove();
        } else {
            waiting = waitingBelow();
        }
        return waiting;
    }

    private boolean waitingAbove() {
        int count = 0;
        boolean waiting = false;
        int start = currentFloor+1;
        for (int i = start; i < numFloors && !waiting; i++) {
            if (!queueArray[i][count].isEmpty()) {
                waiting = true;
            }
        }
        return waiting;
    }


    private boolean waitingBelow(){
        int count = 1;
        boolean waiting = false;
        int start = currentFloor-1;
        for (int i = start; i >= 0; i--) {
            if (!queueArray[i][count].isEmpty()) {
                waiting = true;
            }
        }
        return waiting;
    }

    // This method will be called by runElevator() until there is no one left in all queues
    // Returns true if all queues in queueArray are empty, false otherwise
    private boolean nobodyLeft() {
        boolean empty = true;
        for (int i = 0; i < numFloors; i++){
            for (int j = 0; j < 2; j++) {
                if (!queueArray[i][j].isEmpty()) {
                    empty = false;
                }
            }
        }
        return empty;
    }


    public int getNumFloors() {
        return numFloors;
    }


    public int getElevatorCapacity() {
        return elevatorCapacity;
    }

    // Method to print the statistics about the simulation
    public void printStatistics() {
        System.out.println("\nElevator simulation statistics: ");
        System.out.println("    Total number of trips: " + numTrips);

        int totalTripTime = 0;
        for (int i = 0; i < tripTimes.size(); i++) {
            totalTripTime+= tripTimes.get(i).getTripTime();

        }
        System.out.println("    Total trip time: " + totalTripTime);
        System.out.println("    Average trip time: " + (double)totalTripTime/numTrips);


        Employee minTripTime = tripTimes.get(0);
        for (int i = 1; i < tripTimes.size(); i++) {
            if (tripTimes.get(i).getTripTime() < minTripTime.getTripTime()) {
                minTripTime = tripTimes.get(i);
            }
        }
        System.out.println("    Minimum trip time: " + minTripTime.getTripTime());
        System.out.println("    Minimum trip details: " + "Employee " + minTripTime.getEmployeeID() +
                ", arrival floor " + minTripTime.getWaitingFloor() + ", arrival time " + minTripTime.getArrivalTime() + ", destination floor " + minTripTime.getDestinationFloor());


        Employee maxTripTime = tripTimes.get(0);
        for (int i = 1; i < tripTimes.size(); i++) {
            if (tripTimes.get(i).getTripTime() > maxTripTime.getTripTime()) {
                maxTripTime = tripTimes.get(i);
            }
        }
        System.out.println("    Maximum trip time: " + maxTripTime.getTripTime());
        System.out.println("    Maximum trip details: " + "Employee " + maxTripTime.getEmployeeID() +
                ", arrival floor " + maxTripTime.getWaitingFloor() + ", arrival time " + maxTripTime.getArrivalTime() + ", destination floor " + maxTripTime.getDestinationFloor());

        System.out.println("\nProcessing ends normally");
    }
}